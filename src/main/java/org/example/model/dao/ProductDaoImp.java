package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.example.utils.DatabaseConnectionManager;

import java.sql.*;
import java.util.ArrayList;

public class ProductDaoImp implements ProductDao {
    private DatabaseConnectionManager databaseConnectionManager;

    public ProductDaoImp() throws CustomException {
        this.databaseConnectionManager = new DatabaseConnectionManager();
    }

    @Override
    public ProductList queryAllProducts(int page) throws CustomException {
        ArrayList<Product> product = new ArrayList<>();
        int totalRow = 0, perPage = 5;
        try {
            Connection connection = databaseConnectionManager.getConnection();

            String query = "SELECT * FROM stock_tb LIMIT ? OFFSET ?";
            try {
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, perPage);
                statement.setInt(2, (page-1)*perPage);

                ResultSet data = statement.executeQuery();
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
            }

            String total = "SELECT COUNT(id) AS total FROM stock_tb";
            try (PreparedStatement countStatement = connection.prepareStatement(total);
                 ResultSet rows = countStatement.executeQuery()) {
                if (rows.next()) {
                    totalRow = rows.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
        return 0;
    }

    @Override
    public int deleteProductById(int id) throws CustomException {
        // Create a connection instance
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection from DatabaseConnectionManager
            connection = new DatabaseConnectionManager().getConnection();

            // SQL query to delete a product by its ID
            String sql = "DELETE FROM products WHERE id = ?";

            // Prepare the statement
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, id);  // Set the id parameter

            // Execute the update and return the number of rows affected
            int rowsAffected = preparedStatement.executeUpdate();

            return rowsAffected; // Return the number of rows deleted
        } catch (SQLException sqlException) {
            // Handle SQL exceptions
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
