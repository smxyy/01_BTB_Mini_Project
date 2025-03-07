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

            String query = "SELECT * FROM stock_tb ORDER BY id LIMIT ? OFFSET ?";
            ResultSet data = null;
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, perPage);
                statement.setInt(2, (page-1)*perPage);

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
    public Product searchProductById(int id) throws CustomException {
        String sql = """
                SELECT * FROM products WHERE id = ?
                """;

        try (
                Connection connection = databaseConnectionManager.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
                ){
            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int productId = resultSet.getInt("id");
                String productName = resultSet.getString("name");
                double unitPrice = resultSet.getDouble("unit_price");
                int quantity = resultSet.getInt("quantity");
                Date importedDate = resultSet.getDate("imported_date");

                return new Product(productId, productName, unitPrice, quantity, importedDate);
            }

        } catch (SQLException e){
            throw new CustomException("Error searching for product: " + e.getMessage());
        }
        return null;
    }

    @Override
    public int updateProductById(int id) throws CustomException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = databaseConnectionManager.getConnection();
            String sql = "UPDATE products SET name = ?, unit_price = ?, quantity = ?, imported_date = ? WHERE id = ?";
            preparedStatement = connection.prepareStatement(sql);
            Product product = searchProductById(id);

            preparedStatement.setString(1, product.getName());
            preparedStatement.setDouble(2, product.getUnitPrice());
            preparedStatement.setInt(3,product.getQuantity());
            preparedStatement.setDate(4, product.getImpotedDate());
            preparedStatement.setInt(5, id);

            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new CustomException("Error updating product: " + e.getMessage());
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
    public int searchProductByName(String name) throws CustomException {
            List<Product> productList = new ArrayList<>();
            String sql = """
            SELECT * FROM stock_tb
            WHERE  LOWER(name)  LIKE LOWER(?)
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
                    if (productList.isEmpty()) {
                        System.out.println(RED+"Product not found"+RESET);
                    }
                    Table table = new Table(5, BorderStyle.UNICODE_BOX, ShownBorders.ALL);
                    String[] pColumnNames = {"Id", "Name", "Unit Price", "Qty", "Import Date"};

                    for (String col : pColumnNames) {
                        table.addCell(col, new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                    }
                    for (int i = 0; i < 5; i++) {
                        table.setColumnWidth(i, 25, 25);
                    }
                    for (Product product : productList) {

                        table.addCell(String.valueOf(product.getId()), new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                        table.addCell(product.getName(), new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                        table.addCell(String.valueOf(product.getQuantity()), new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                        table.addCell(String.valueOf(product.getUnitPrice()), new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                        table.addCell(product.getImpotedDate().toString(), new CellStyle(CellStyle.HorizontalAlign.CENTER), 1);
                    }
                    System.out.println(table.render());
                    System.out.println("Press Enter to continue...");

                }
            } catch (SQLException sqlexception) {
                System.out.println("Problem during get all products from database: " + sqlexception.getMessage());
            }
            return productList.size();

    }
}
