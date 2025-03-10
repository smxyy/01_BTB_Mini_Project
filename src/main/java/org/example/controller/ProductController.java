package org.example.controller;

import org.example.config.Color;
import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.example.utils.Helper;
import org.example.model.entity.ProductTempList;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.*;
import static org.example.model.entity.ProductTempList.updateProductList;
import static org.example.utils.Helper.*;

public class ProductController {
    ProductTempList productTempList = new ProductTempList();
    ProductDaoImp productDaoImp = new ProductDaoImp();

    public ProductController() throws CustomException {
    }

    public ProductList listProducts(int currentPage) throws CustomException {
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

        ProductDaoImp productDao = new ProductDaoImp();
        ProductList products = productDao.queryAllProducts(currentPage);

        if (!products.getResult().isEmpty()) {
            for (Product product : products.getResult()) {
                tbList.addCell(GREEN.getCode() + product.getId() + RESET.getCode(), alignCenter);
                tbList.addCell(BLUE.getCode() + product.getName() + RESET.getCode(), alignCenter);
                tbList.addCell(YELLOW.getCode() + product.getUnitPrice() + RESET.getCode(), alignCenter);
                tbList.addCell(YELLOW.getCode() + product.getQuantity() + RESET.getCode(), alignCenter);
                tbList.addCell(PURPLE.getCode() + product.getImpotedDate() + RESET.getCode(), alignCenter);
            }

            int page = products.getPage();
            int totalPage = products.getTotalPage();
            int totalRecord = products.getTotal();
            tbList.addCell("Page: " + GREEN.getCode() + page + RESET.getCode() + " of " + GREEN.getCode() + totalPage + RESET.getCode(), alignCenter, 2);
            tbList.addCell("Total Record: " + GREEN.getCode() + totalRecord + RESET.getCode(), alignCenter, 3);
        } else {
            for (int i = 0; i < 5; i++)
                tbList.addCell("---", alignCenter);
        }

        System.out.println("\n");
        System.out.println(tbList.render());

        return products;
    }

    public int showPagination(String option, ProductList productList) {
        int pages = productList.getPage();

        int perPage;

        try (FileInputStream file = new FileInputStream("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(file);
            try {
                perPage = Integer.parseInt(properties.getProperty("page.show"));
            } catch (NumberFormatException e) {
                perPage = 5;
            }
        } catch (IOException ioException) {
            perPage = 5;
        }

        switch (option) {
            case "n" -> {
                if (productList.getTotal() - (perPage * pages) >= 1)
                    pages += 1;
            }
            case "p" -> {
                if (pages > 1)
                    pages -= 1;
            }
            case "f" -> pages = 1;
            case "l" -> {
                if (productList.getTotal() % perPage > 0)
                    pages = productList.getTotal() / perPage + 1;
                else
                    pages = productList.getTotal() / perPage;
            }
            case "g" -> {
                while (true) {
                    try {
                        System.out.print(YELLOW.getCode() + "=> Go to page: " + RESET.getCode());
                        String gotoPage = new Scanner(System.in).nextLine();
                        if (!gotoPage.isBlank()) {
                            int pageNum = Integer.parseInt(gotoPage);
                            if (pageNum <= productList.getTotalPage()) {
                                pages = pageNum;
                                break;
                            } else {
                                Helper.printMessage("Page " + pageNum + " doesn't have!", 0);
                            }
                        } else {
                            Helper.printMessage("Page not allowed empty!", 0);
                        }
                    } catch (NumberFormatException e) {
                        Helper.printMessage("Page is allowed only number!", 0);
                    }
                }
            }
        }
        return pages;
    }


    public void setRow() throws CustomException {
        ProductDaoImp product = new ProductDaoImp();
        while (true) {
            try {
                System.out.print(YELLOW.getCode() + "=> Set row: " + RESET.getCode());
                String showPage = new Scanner(System.in).nextLine();
                if (!showPage.isBlank()) {
                    int perPage = Integer.parseInt(showPage);
                    if (perPage <= 100 && perPage > 0) {
                        product.setRow(perPage);
                        Helper.printMessage("Row have been set successfully!", 1);
                        break;
                    } else {
                        Helper.printMessage("Row must be between 1 and 100!", 0);
                    }
                } else {
                    Helper.printMessage("Row not allowed empty!", 0);
                }
            } catch (NumberFormatException e) {
                Helper.printMessage("Row is allowed only number!", 0);
            }
        }
    }

    public void saveProduct() throws CustomException {
        Scanner scanner = new Scanner(System.in);
        boolean isSave = true;
        while (isSave) {
            ProductTempList productTempList = new ProductTempList();
            ProductDaoImp productDao = new ProductDaoImp();

            System.out.println(GREEN.getCode() + "ui" + RESET.getCode() + " for insert product and " + GREEN.getCode() + "uu" + RESET.getCode() + " for update product or " + RED.getCode() + "b" + RESET.getCode() + " for back to menu");
            System.out.print("Enter your option: ");
            String option = scanner.nextLine();
            if (!option.isBlank()) {
                if(Pattern.matches("^[a-zA-Z]+$", option)) {
                    switch (option) {
                        case "ui" -> {
                            productTempList.saveProduct(option);
                            isSave = false;
                        }
                        case "uu" -> {
                            productTempList.saveProduct(option);
                            isSave = false;
                        }
                        case "b" -> {
                            isSave = false;
                        }
                        default -> Helper.printMessage("This option doesn't have!", 0);
                    }
                } else {
                    Helper.printMessage("Option is allowed only letter!", 0);
                }
            } else {
                Helper.printMessage("Option not allowed empty!", 0);
            }
        }
    }

    public void unsavedController() throws CustomException {
        while (true) {
            System.out.println(GREEN.getCode() +  "ui" + RESET.getCode() + " for view unsaved insert and " + GREEN.getCode() +  "uu" + RESET.getCode() + " for view unsaved updated or "  + RED.getCode() + "b" + RESET.getCode() + " for back to menu");
            String ch = Helper.validateString("Enter your option: ", "option");
            ProductTempList productTempList = new ProductTempList();
            if (productTempList.unsavedProduct(ch)) break;
        }
    }

    public void writeController() throws CustomException {
        productTempList.writeProduct();
    }

    public void updateProductTemp() throws CustomException {
        int id = Helper.validateId("Enter product id: ");
        Product product = productDaoImp.readProductById(id);
        if (product == null){
            Helper.printMessage("Product with id: " + id + " does not exist!", 0);
            Helper.displayEmptyTable();
            System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
            scanner.nextLine();
        } else {
            List<Product> productList = new ArrayList<>(List.of(product));
            Helper.displayProduct(false, productList);
            boolean isOption = true;
            while(isOption) {
                System.out.println("1. Name\t 2. Unit Price\t 3. Qty\t 4. All Field\t 5. Exit");
                System.out.print("Enter your option: ");
                String option = scanner.nextLine();
                if (!option.isBlank()) {
                    if (Pattern.matches("^[0-9]+$", option)) {
                        switch (Integer.parseInt(option)) {
                            case 1 -> {
                                String productName = validateString("Input product name: ", "name");

                                if (!updateProductList.isEmpty()) {
                                    int i = 0;
                                    for (Product item : updateProductList) {
                                        if (item.getId() == id) {
                                            updateProductList.remove(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                                updateProductList.add(new Product(id, productName, product.getUnitPrice(), product.getQuantity(), product.getImpotedDate()));

                                Helper.printMessage("Updated successfully!\n",  1);
                            }

                            case 2 -> {
                                double unitPrice = 0;
                                while(true) {
                                    System.out.print("Input product price: ");
                                    String price = scanner.nextLine();

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

                                if (!updateProductList.isEmpty()) {
                                    int i = 0;
                                    for (Product item : updateProductList) {
                                        if (item.getId() == id) {
                                            updateProductList.remove(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                                updateProductList.add(new Product(id, product.getName(), unitPrice, product.getQuantity(), product.getImpotedDate()));

                                Helper.printMessage("Updated successfully!\n",  1);
                            }

                            case 3 -> {
                                int quantity = 0;
                                while(true) {
                                    System.out.print("Input quantity: ");
                                    String qty = scanner.nextLine();

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

                                if (!updateProductList.isEmpty()) {
                                    int i = 0;
                                    for (Product item : updateProductList) {
                                        if (item.getId() == id) {
                                            updateProductList.remove(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                                updateProductList.add(new Product(id, product.getName(), product.getUnitPrice(), quantity, product.getImpotedDate()));

                                Helper.printMessage("Updated successfully!\n",  1);
                            }

                            case 4 -> {
                                String productName = "";
                                double unitPrice = 0;
                                int quantity = 0;
                                while(true) {
                                    System.out.print("Input product name: ");
                                    productName = scanner.nextLine();

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
                                    String price = scanner.nextLine();

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
                                    String qty = scanner.nextLine();

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

                                if (!updateProductList.isEmpty()) {
                                    int i = 0;
                                    for (Product item : updateProductList) {
                                        if (item.getId() == id) {
                                            updateProductList.remove(i);
                                            break;
                                        }
                                        i++;
                                    }
                                }
                                updateProductList.add(new Product(id, productName, unitPrice, quantity, product.getImpotedDate()));

                                Helper.printMessage("Updated successfully!\n",  1);
                            }
                            case 5 -> isOption = false;
                            default -> Helper.printMessage("This option doesn't have!", 0);
                        }
                    } else {
                        Helper.printMessage("Option is allowed only number!", 0);
                    }
                } else {
                    Helper.printMessage("Option not allowed empty!", 0);
                }
            }
        }
    }

    public void readById() throws CustomException {
        int id = Helper.validateId("Please input id to get record: ");
        Product product = productDaoImp.readProductById(id);
        if (product == null){
            Helper.printMessage("Product with id: " + id + " does not exist!", 0);
            Helper.displayEmptyTable();
            System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
            scanner.nextLine();
        } else {
            List<Product> productList = new ArrayList<>(List.of(product));
            Helper.displayProduct(true, productList);
            System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
            scanner.nextLine();
        }
    }

    public void searchByNameController() throws CustomException {
        String name = Helper.validateString("Input Product Name: ", "name");
        List<Product> productList = productDaoImp.searchProductByName(name);
        if (productList.isEmpty()){
            Helper.printMessage("Product that contains: " + name + " does not exist!", 0);
            Helper.displayEmptyTable();
        } else {
            Helper.displayProduct(false, productList);
        }
        System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
        scanner.nextLine();
    }

    public void deleteByIdController() throws CustomException {
        int id = validateId("Enter product ID to delete: ");
        Product product = productDaoImp.readProductById(id);
        if (product == null){
            Helper.printMessage("Product with id: " + id + " does not exist!", 0);
            Helper.displayEmptyTable();
            System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
            scanner.nextLine();
        } else {
            List<Product> productList = new ArrayList<>(List.of(product));
            Helper.displayProduct(true, productList);
            char answer = inputChar(YELLOW.getCode() +  "Are you sure to delete product id : " + RED.getCode() + id + RESET.getCode() + " ? (y/n) : ");
            if ( answer == 'y'){
                int rowAffected = productDaoImp.deleteProductById(id);
                if ( rowAffected > 0)
                    Helper.printMessage("Product deleted successfully!", 1);
                else
                    Helper.printMessage("No product found with ID: " + id + ".", 2);

                System.out.print(YELLOW.getCode() + "Press ENTER to continue..." + RESET.getCode());
            } else {
                return;
            }
        }
    }

}
