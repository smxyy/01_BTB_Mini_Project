package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.example.utils.DatabaseConnectionManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.RED;
import static org.example.config.Color.RESET;

import static org.example.config.Color.RED;
import static org.example.config.Color.RESET;

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

            String total = "SELECT COUNT(id) AS total FROM stock_tb";
            try (PreparedStatement countStatement = connection.prepareStatement(total);
                 ResultSet rows = countStatement.executeQuery()) {
                if (rows.next()) {
                    totalRow = rows.getInt("total");
                }
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
    public int addNewProduct(Product product) throws CustomException {

        return 0;
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
    public void saveProductToDatabase() throws CustomException {

    }

    @Override
    public void backUp() throws CustomException {

    }

    @Override
    public void restoreVersion() throws CustomException {

    }
}
