package org.example.model.entity;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.*;

public class ProductTempList {
    static List<Product> writeProductList;
    static List<Product> updateProductList;

    // Delete Product by ID
    public void deleteProductById() throws CustomException {
        ProductDaoImp productDao = new ProductDaoImp();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        if (id <= 0) {
            System.out.println("Invalid ID. Product ID must be greater than 0.");
            return;
        }
        while (true) {
            System.out.print("Do you want to delete this product? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (Pattern.matches("[Yy]", choice)) {
                int result = productDao.deleteProductById(id);
                if (result >0) {
                    System.out.println("Product deleted successfully.");
                }else {
                    System.out.println("⚠️ No product found with ID " + id + ".");
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
    public void writeProduct() throws CustomException {
        Scanner scan = new Scanner(System.in);
        int productId=10;
        productId++;
        System.out.print("Input product name: ");
        String productName = scan.nextLine();

        System.out.print("Input product price: ");
        double unitPrice = scan.nextDouble();

        System.out.print("Input quantity: ");
        int quantity = scan.nextInt();

        Date importDate = Date.valueOf(LocalDate.now());

        Product product = new Product(productId,productName,unitPrice,quantity,importDate);
        if (writeProductList == null) {
            writeProductList = new ArrayList<>();
        }

        writeProductList.add(product);
        System.out.println("✅ Product added successfully!");

    }
}
