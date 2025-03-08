package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.utils.DatabaseConnectionManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
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
    public List<Product> queryAllProducts() throws CustomException {
        return List.of();
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
    // Delete Product by ID
    private void deleteProductById() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        while (true) {
            System.out.print("Do you want to delete this product? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (Pattern.matches("[Yy]", choice)) {
                try {
                    int result = deleteProductById(id);
                    if (result > 0) {
                        System.out.println("Deleted product successfully.");
                    } else {
                        System.out.println("Delete failed or product not found.");
                    }
                } catch (CustomException e) {
                    System.out.println("Error: " + e.getMessage());
                }
                break;
            } else if (Pattern.matches("[Nn]", choice)) {
                return;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }


    // Write Product
    private void writeProduct(){
        Scanner scan = new Scanner(System.in);
        int productId=1;
        System.out.print("Input product name: ");
        String productName = scan.nextLine();

        System.out.print("Input product price: ");
        double unitPrice = scan.nextDouble();

        System.out.print("Input quantity: ");
        int quantity = scan.nextInt();

        Date importDate = Date.valueOf(LocalDate.now());

        Product product = new Product(productId,productName,unitPrice,quantity,importDate);
        writeProduct(product);
    }
    // display Write Product
    private void writeProduct(Product product){
        System.out.println(product);
    }
}
