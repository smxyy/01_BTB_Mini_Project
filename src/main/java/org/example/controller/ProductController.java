package org.example.controller;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.*;

public class ProductController {
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
                tbList.addCell(YELLOW.getCode() + product.getId() + RESET.getCode(), alignCenter);
                tbList.addCell(BLUE.getCode() + product.getName() + RESET.getCode(), alignCenter);
                tbList.addCell(YELLOW.getCode() + product.getUnitPrice() + RESET.getCode(), alignCenter);
                tbList.addCell(YELLOW.getCode() + product.getQuantity() + RESET.getCode(), alignCenter);
                tbList.addCell(PURPLE.getCode() + product.getImpotedDate() + RESET.getCode(), alignCenter);
            }

            int page = products.getPage();
            int totalPage = products.getTotalPage();
            int totalRecord = products.getTotal();
            tbList.addCell("Page: " + page + " of " + totalPage, alignCenter, 2);
            tbList.addCell("Total Record: " + totalRecord, alignCenter, 3);
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

        switch(option) {
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
                while(true) {
                    try {
                        System.out.print(YELLOW.getCode() + "=> Go to page: " + RESET.getCode());
                        int gotoPage = Integer.parseInt(new Scanner(System.in).nextLine());
                        if(gotoPage <= productList.getTotalPage()) {
                            pages = gotoPage;
                            break;
                        } else {
                            System.out.println(RED.getCode() + "Page " + gotoPage + " doesn't have!" + RESET.getCode());
                        }
                    } catch (NumberFormatException e) {
                        System.out.println(RED.getCode() + "Page is allowed only number!" + RESET.getCode());
                    }
                }
            }
        }
        return pages;
    }

    // Delete Product by ID
    private void deleteProductById(int id) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter product ID to delete: ");
        int searchId = scanner.nextInt();
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
}
