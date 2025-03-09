package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.example.utils.DatabaseConnectionManager;
import org.example.utils.Helper;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

import static org.example.config.Color.*;

public class ProductDaoImp implements ProductDao {
    private DatabaseConnectionManager databaseConnectionManager;

    public ProductDaoImp() throws CustomException {
        this.databaseConnectionManager = new DatabaseConnectionManager();
    }

    @Override
    public ProductList queryAllProducts(int page) throws CustomException {
        ArrayList<Product> product = new ArrayList<>();
        int totalRow = 0, perPage;
        Properties properties = new Properties();
        String propertiesPath = "src/main/resources/config.properties";

        try (FileInputStream file = new FileInputStream(propertiesPath)) {
            properties.load(file);
            try {
                perPage = Integer.parseInt(properties.getProperty("page.show"));
            } catch (NumberFormatException e) {
                perPage = 5;
            }
        } catch (IOException ioException) {
            perPage = 5;
        }

        Connection connection = null;
        try {
            connection = databaseConnectionManager.getConnection();

            // Ensure the table exists by creating it if it doesn't exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS stock_tb (" +
                    "id serial PRIMARY KEY, " +
                    "name varchar(100), " +
                    "unit_price DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0), " +
                    "stock_qty INTEGER NOT NULL CHECK (stock_qty >= 0), " +
                    "import_date DATE NOT NULL DEFAULT CURRENT_DATE" +
                    ");";

            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)) {
                createTableStatement.executeUpdate(); // Create the table if it does not exist
            } catch (SQLException e) {
                throw new CustomException("Error creating table: " + e.getMessage());
            }

            // Count the total number of rows in the stock_tb table
            String total = "SELECT COUNT(id) AS total FROM stock_tb";
            try (PreparedStatement countStatement = connection.prepareStatement(total);
                 ResultSet rows = countStatement.executeQuery()) {
                if (rows.next()) {
                    totalRow = rows.getInt("total");
                }
            }

            if (totalRow > 0) {
                int maxPage = (totalRow / perPage) + (totalRow % perPage > 0 ? 1 : 0);
                page = Math.min(page, maxPage);
            }

            // Query for the product data from the stock_tb table
            String query = "SELECT * FROM stock_tb ORDER BY id LIMIT ? OFFSET ?";
            ResultSet data = null;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, perPage);
                statement.setInt(2, (page - 1) * perPage);

                data = statement.executeQuery();
                // Process the result set
                while (data.next()) {
                    int id = data.getInt(1);
                    String name = data.getString(2);
                    double price = data.getDouble(3);
                    int qty = data.getInt(4);
                    Date date = data.getDate(5);

                    product.add(new Product(id, name, price, qty, date));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (data != null) data.close();
            }
        } catch (SQLException e) {
            throw new CustomException("Error: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new CustomException("Error connection: " + e.getMessage());
            }
        }

        return new ProductList(product, page, perPage, totalRow);
    }

    @Override
    public Product getProductById(int searchId) throws CustomException {
        String query = "SELECT * FROM stock_tb WHERE id = ? LIMIT 1";
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet result = null;
        Product product = new Product();
        try {
            connection = databaseConnectionManager.getConnection();
            statement = connection.prepareStatement(query);
            statement.setInt(1, searchId);
            result = statement.executeQuery();
            // Process the result set
            while (result.next()) {
                int id = result.getInt(1);
                String name = result.getString(2);
                double price = result.getDouble(3);
                int qty = result.getInt(4);
                Date date = result.getDate(5);

                product = new Product(id, name, price, qty, date);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (connection != null) connection.close();
                if (statement != null) statement.close();
                if (result != null) result.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return product;
    }

    @Override
    public Product readProductById(int id) throws CustomException {
        String sql = """
                SELECT * FROM stock_tb WHERE id = ? LIMIT 1
                """;

        try (
                Connection connection = databaseConnectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ){
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int productId = resultSet.getInt(1);
                String productName = resultSet.getString(2);
                double unitPrice = resultSet.getDouble(3);
                int quantity = resultSet.getInt(4);
                Date importedDate = resultSet.getDate(5);

                return new Product(productId, productName, unitPrice, quantity, importedDate);
            }

        } catch (SQLException e){
            throw new CustomException("Error searching for product: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int deleteProductById(int id) throws CustomException {
        // Create a connection instance
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = new DatabaseConnectionManager().getConnection();
            String sql = """         
               DELETE FROM "stock_tb"
               WHERE id = ?
           """;

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected;
        } catch (SQLException sqlException) {
            throw new CustomException("Error deleting product: " + sqlException.getMessage());
        } finally {
            // Close resources
            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlException) {
                throw new CustomException("Error closing resources: " + sqlException.getMessage());
            }
        }
    }

    @Override
    public void setRow(int number) {
        Properties properties = new Properties();
        String propertiesPath = "src/main/resources/config.properties";
        try (FileInputStream input = new FileInputStream(propertiesPath)) {
            properties.load(input);
            properties.setProperty("page.show", number+"");
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the updated properties back to the file
        try (FileOutputStream output = new FileOutputStream(propertiesPath)) {
            properties.store(output, "Updated properties file");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveProductToDatabase(List<Product> product, String type) throws CustomException {
        Connection connection = null;
        try {
            connection = databaseConnectionManager.getConnection();
            connection.setAutoCommit(false);

            if (Objects.equals(type, "insert")) {
                for (Product pro : product) {
                    String query = """
                        INSERT INTO stock_tb (name, unit_price, stock_qty, import_date)
                        VALUES (?, ?, ?, ?) RETURNING *;
                    """;
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, pro.getName());
                        preparedStatement.setDouble(2, pro.getUnitPrice());
                        preparedStatement.setInt(3, pro.getQuantity());
                        preparedStatement.setDate(4, pro.getImpotedDate());
                        ResultSet result = preparedStatement.executeQuery();
                        Helper.printMessage("Insert successfully!", 1);
                    } catch (SQLException e) {
                        throw new CustomException("Error: " + e.getMessage());
                    }
                }
                connection.commit();
                System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
                new Scanner(System.in).nextLine();
            }

            if (Objects.equals(type, "update")) {
                for (Product pro : product) {
                    String query = """
                        UPDATE stock_tb
                        SET name = ?, unit_price = ?, stock_qty = ?, import_date = ?
                        WHERE id = ? RETURNING *;
                    """;
                    try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                        preparedStatement.setString(1, pro.getName());
                        preparedStatement.setDouble(2, pro.getUnitPrice());
                        preparedStatement.setInt(3, pro.getQuantity());
                        preparedStatement.setDate(4, pro.getImpotedDate());
                        preparedStatement.setInt(5, pro.getId());
                        ResultSet result = preparedStatement.executeQuery();
                        Helper.printMessage("Update successfully!", 1);
                    } catch (SQLException e) {
                        throw new CustomException("Error: " + e.getMessage());
                    }
                }
                connection.commit();
                System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
                new Scanner(System.in).nextLine();
            }

        } catch (SQLException e) {
            throw new CustomException("Error: " + e.getMessage());
        } finally {
            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                throw new CustomException("Error connection: " + e.getMessage());
            }
        }
    }

    // Search by Name
    @Override
    public List<Product> searchProductByName(String name) throws CustomException {
            List<Product> productList = new ArrayList<>();
            String sql = """
            SELECT * FROM stock_tb
            WHERE LOWER(name) LIKE LOWER(?)
            """;
            try (
                    Connection connection = databaseConnectionManager.getConnection();
                    PreparedStatement preparedStatement = connection.prepareStatement(sql)
            ) {
                preparedStatement.setString(1, "%" + name + "%");
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        Product product = new Product();
                        product.setId(resultSet.getInt("id"));
                        product.setName(resultSet.getString("name"));
                        product.setUnitPrice(resultSet.getDouble("unit_price"));
                        product.setQuantity(resultSet.getInt("stock_qty"));
                        product.setImpotedDate(resultSet.getDate("import_date"));
                        productList.add(product);
                    }
                }
            } catch (SQLException sqlexception) {
                throw new CustomException(sqlexception.getMessage());
            }
            return productList;
    }
}