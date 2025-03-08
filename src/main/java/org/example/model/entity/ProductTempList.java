package org.example.model.entity;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.example.utils.Helper;
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
    private int productId = 1;

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
        String productName = "";
        double unitPrice = 0;
        int quantity = 0;

        while(true) {
            System.out.print("Input product name: ");
            productName = scan.nextLine();

            if (!productName.isBlank()) {
                if (Pattern.matches("^[a-zA-Z ]+$", productName)) {
                    break;
                } else {
                    Helper.printMessage("Product name is allowed only letter!", 0);
                }
            } else {
                Helper.printMessage("Product name not allowed empty!", 0);
            }
        }

        while(true) {
            System.out.print("Input product price: ");
            String price = scan.nextLine();

            if (!price.isBlank()) {
                if (Pattern.matches("^[0-9]+$", price) || Pattern.matches("^([0-9]+).([0-9])+$", price)) {
                    unitPrice = Double.parseDouble(price);
                    break;
                } else {
                    Helper.printMessage("Product price is allowed only number!", 0);
                }
            } else {
                Helper.printMessage("Product price not allowed empty!", 0);
            }
        }

        while(true) {
            System.out.print("Input quantity: ");
            String qty = scan.nextLine();

            if (!qty.isBlank()) {
                if (Pattern.matches("^[0-9]+$", qty)) {
                    quantity = Integer.parseInt(qty);
                    break;
                } else {
                    Helper.printMessage("Product quantity is allowed only number!", 0);
                }
            } else {
                Helper.printMessage("Product quantity not allowed empty!", 0);
            }
        }

        Date importDate = Date.valueOf(LocalDate.now());

        Product product = new Product(productId,productName,unitPrice,quantity,importDate);
        if (writeProductList == null) {
            writeProductList = new ArrayList<>();
        }

        writeProductList.add(product);
        productId++;
        Helper.printMessage("Product added successfully!", 1);
    }

    public void saveProduct(String option) throws CustomException {
        ProductDaoImp productDao = new ProductDaoImp();

        switch (option) {
            case "ui" -> {
                if (!writeProductList.isEmpty()) {
                    productDao.saveProductToDatabase(writeProductList, "insert");
                    writeProductList.clear();
                } else {
                    Helper.printMessage("There is no product for insert!", 0);
                }
            }
            case "uu" -> {
                if (!updateProductList.isEmpty()) {
                    productDao.saveProductToDatabase(updateProductList, "update");
                    updateProductList.clear();
                } else {
                    Helper.printMessage("There is no product for update!", 0);
                }
            }
        }
    }

    public boolean unsavedProduct(String ch) {
        Scanner scanner = new Scanner(System.in);
        switch (ch.toLowerCase()) {
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
                        tbUnsaved.addCell(GREEN.getCode() + ui.getId() + RESET.getCode(), alignCenter);
                        tbUnsaved.addCell(BLUE.getCode() + ui.getName() + RESET.getCode(), alignCenter);
                        tbUnsaved.addCell(YELLOW.getCode() + ui.getUnitPrice() + RESET.getCode(), alignCenter);
                        tbUnsaved.addCell(YELLOW.getCode() + ui.getQuantity() + RESET.getCode(), alignCenter);
                        tbUnsaved.addCell(PURPLE.getCode() + ui.getImpotedDate() + RESET.getCode(), alignCenter);
                    });
                }
                System.out.println(tbUnsaved.render());
                System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
                new Scanner(System.in).nextLine();
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
                        tbUnSavedUpdate.addCell(GREEN.getCode() + uu.getId() + RESET.getCode(), alignCenter);
                        tbUnSavedUpdate.addCell(BLUE.getCode() + uu.getName() + RESET.getCode(), alignCenter);
                        tbUnSavedUpdate.addCell(YELLOW.getCode() + uu.getUnitPrice() + RESET.getCode(), alignCenter);
                        tbUnSavedUpdate.addCell(YELLOW.getCode() + uu.getQuantity() + RESET.getCode(), alignCenter);
                        tbUnSavedUpdate.addCell(PURPLE.getCode() + uu.getImpotedDate() + RESET.getCode(), alignCenter);
                    });
                }
                System.out.println(tbUnSavedUpdate.render());
                System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
                new Scanner(System.in).nextLine();
                return true;

            }
            case "b" -> {
                return true;
            }
            default -> {
                Helper.printMessage("This option doesn't have!", 0);
                return false;
            }
        }

    }
}

