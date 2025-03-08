package org.example.model.entity;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class ProductTempList {
    static List<Product> writeProductList;
    static List<Product> updateProductList;

    // Delete Product by ID
    public void deleteProductById() throws CustomException {
        ProductDaoImp productDao = new ProductDaoImp();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();
        productDao.deleteProductById(id);
        scanner.nextLine();
        while (true) {
            System.out.print("Do you want to delete this product? (y/n): ");
            String choice = scanner.nextLine().trim();
            if (Pattern.matches("[Yy]", choice)) {
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
        int productId=1;
        System.out.print("Input product name: ");
        String productName = scan.nextLine();

        System.out.print("Input product price: ");
        double unitPrice = scan.nextDouble();

        System.out.print("Input quantity: ");
        int quantity = scan.nextInt();

        Date importDate = Date.valueOf(LocalDate.now());

        Product product = new Product(productId,productName,unitPrice,quantity,importDate);
        writeProductList.add(product);
    }

}
