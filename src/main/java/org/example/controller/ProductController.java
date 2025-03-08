package org.example.controller;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.example.utils.Helper;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
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
        while(true) {
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
}
