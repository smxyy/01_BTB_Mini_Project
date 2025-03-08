package org.example.model.entity;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.*;

public class ProductTempList {
    static List<Product> writeProductList = new ArrayList<>();
    static List<Product> updateProductList=new ArrayList<>();

    // search By Name
    public void searchByName() throws CustomException {
        ProductDaoImp productDao = new ProductDaoImp();
        Scanner sc = new Scanner(System.in);
        System.out.print("Input Product Name: ");
        String name = sc.nextLine();
        productDao.searchProductByName(name);
    }

    private void showProductList(List<Product> productList) {
        if (productList.isEmpty()) {
            System.out.println("No products available.");
            return;
        }

        CellStyle alignCenter = new CellStyle(CellStyle.HorizontalAlign.CENTER);
        Table tbList = new Table(5, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);
        tbList.setColumnWidth(0, 10, 40);
        tbList.setColumnWidth(1, 30, 70);
        tbList.setColumnWidth(2, 15, 40);
        tbList.setColumnWidth(3, 15, 40);
        tbList.setColumnWidth(4, 20, 40);

        tbList.addCell(CYAN.getCode() + "ID" + RESET.getCode(), alignCenter);
        tbList.addCell(CYAN.getCode() + "Name" + RESET.getCode(), alignCenter);
        tbList.addCell(CYAN.getCode() + "Unit Price" + RESET.getCode(), alignCenter);
        tbList.addCell(CYAN.getCode() + "Qty" + RESET.getCode(), alignCenter);
        tbList.addCell(CYAN.getCode() + "Import Date" + RESET.getCode(), alignCenter);

        for (Product product : productList) {
            tbList.addCell(YELLOW.getCode() + product.getId() + RESET.getCode(), alignCenter);
            tbList.addCell(BLUE.getCode() + product.getName() + RESET.getCode(), alignCenter);
            tbList.addCell(YELLOW.getCode() + product.getUnitPrice() + RESET.getCode(), alignCenter);
            tbList.addCell(YELLOW.getCode() + product.getQuantity() + RESET.getCode(), alignCenter);
            tbList.addCell(PURPLE.getCode() + product.getImpotedDate() + RESET.getCode(), alignCenter);
        }

        System.out.println(tbList.render());
    }

    // Update Product
    public void updateTempProductById() throws CustomException {
        Scanner scanner = new  Scanner(System.in);
        System.out.print("Input ID to update: ");
        String id = scanner.nextLine();
        int conertID = Integer.parseInt(id);

        Product productToUpdate = updateProductList.stream()
                .filter(p -> p.getId() == conertID)
                .findFirst().orElse(null);

        if (productToUpdate != null) {
            showProductList(updateProductList);
            System.out.println("1. Name 2. Unit Price 3. Qty 4. All Fields 5. Exit");
            System.out.print("Choose an option to update: ");
            String choice = scanner.next();
            scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter new name: ");
                    String newName = scanner.nextLine();
                    productToUpdate.setName(newName);
                    break;
                case "2":
                    System.out.print("Enter new unit price: ");
                    double newPrice = scanner.nextInt();
                    productToUpdate.setUnitPrice(newPrice);
                    break;
                case "3":
                    System.out.print("Enter new Qty: ");
                    int newQty = scanner.nextInt();
                    productToUpdate.setQuantity(newQty);
                    break;
                case "4":
                    System.out.print("Enter new name: ");
                    newName = scanner.nextLine();
                    productToUpdate.setName(newName);

                    System.out.print("Enter new unit price: ");
                    newPrice = scanner.nextDouble();
                    productToUpdate.setUnitPrice(newPrice);

                    System.out.print("Enter new Qty: ");
                    newQty = scanner.nextInt();
                    productToUpdate.setQuantity(newQty);
                    break;
                case "5":
                    System.out.println("Exiting update menu.");
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        } else {
            System.out.println("Product with ID " + id + " not found.");
        }
    }

    // Delete Product by ID
    public void deleteProductById() throws CustomException {
        ProductDaoImp productDao = new ProductDaoImp();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product ID to delete: ");
        int id = scanner.nextInt();
        productDao.deleteProductById(id);
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

    public boolean unsavedProduct(String ch) {
        switch (ch) {
            case "ui" -> {
                CellStyle alignCenter = new CellStyle(CellStyle.HorizontalAlign.CENTER);
                Table tbUnsaved = new Table(5, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);
                tbUnsaved.setColumnWidth(0, 10, 40);
                tbUnsaved.setColumnWidth(1, 30, 70);
                tbUnsaved.setColumnWidth(2, 15, 40);
                tbUnsaved.setColumnWidth(3, 15, 40);
                tbUnsaved.setColumnWidth(4, 20, 40);
                tbUnsaved.addCell(CYAN.getCode() + "ID" + RESET.getCode(), alignCenter);
                tbUnsaved.addCell(CYAN.getCode() + "Name" + RESET.getCode(), alignCenter);
                tbUnsaved.addCell(CYAN.getCode() + "Unit Price" + RESET.getCode(), alignCenter);
                tbUnsaved.addCell(CYAN.getCode() + "Qty" + RESET.getCode(), alignCenter);
                tbUnsaved.addCell(CYAN.getCode() + "Import Date" + RESET.getCode(), alignCenter);
                if (!writeProductList.isEmpty()) {
                    writeProductList.forEach(ui -> {
                        tbUnsaved.addCell(String.valueOf(ui.getId()));
                        tbUnsaved.addCell(ui.getName());
                        tbUnsaved.addCell(String.valueOf(ui.getUnitPrice()));
                        tbUnsaved.addCell(String.valueOf(ui.getQuantity()));
                        tbUnsaved.addCell(String.valueOf(ui.getImpotedDate()));
                    });
                }
                System.out.println(tbUnsaved.render());
                return true;
            }
            case "uu"-> {

                CellStyle alignCenter = new CellStyle(CellStyle.HorizontalAlign.CENTER);
                Table tbUnSavedUpdate = new Table(5, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.ALL);
                tbUnSavedUpdate.setColumnWidth(0, 10, 40);
                tbUnSavedUpdate.setColumnWidth(1, 30, 70);
                tbUnSavedUpdate.setColumnWidth(2, 15, 40);
                tbUnSavedUpdate.setColumnWidth(3, 15, 40);
                tbUnSavedUpdate.setColumnWidth(4, 20, 40);
                tbUnSavedUpdate.addCell(CYAN.getCode() + "ID" + RESET.getCode(), alignCenter);
                tbUnSavedUpdate.addCell(CYAN.getCode() + "Name" + RESET.getCode(), alignCenter);
                tbUnSavedUpdate.addCell(CYAN.getCode() + "Unit Price" + RESET.getCode(), alignCenter);
                tbUnSavedUpdate.addCell(CYAN.getCode() + "Qty" + RESET.getCode(), alignCenter);
                tbUnSavedUpdate.addCell(CYAN.getCode() + "Import Date" + RESET.getCode(), alignCenter);
                if (!updateProductList.isEmpty()) {
                    updateProductList.forEach(uu -> {
                        tbUnSavedUpdate.addCell(String.valueOf(uu.getId()));
                        tbUnSavedUpdate.addCell(uu.getName());
                        tbUnSavedUpdate.addCell(String.valueOf(uu.getUnitPrice()));
                        tbUnSavedUpdate.addCell(String.valueOf(uu.getQuantity()));
                        tbUnSavedUpdate.addCell(String.valueOf(uu.getImpotedDate()));
                    });
                }
                System.out.println(tbUnSavedUpdate.render());
                return true;

            }
            default -> {
                System.out.println("Don't have this case");
                return false;
            }
        }

    }

}

