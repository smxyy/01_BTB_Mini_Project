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

import static org.example.config.Color.CYAN;
import static org.example.config.Color.RESET;

public class ProductTempList {
    static List<Product> writeProductList = new ArrayList<>();
    static List<Product> updateProductList=new ArrayList<>();

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
        int productId = 1;
        System.out.print("Input product name: ");
        String productName = scan.nextLine();

        System.out.print("Input product price: ");
        double unitPrice = scan.nextDouble();

        System.out.print("Input quantity: ");
        int quantity = scan.nextInt();

        Date importDate = Date.valueOf(LocalDate.now());

        Product product = new Product(productId, productName, unitPrice, quantity, importDate);
        writeProductList.add(product);
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

