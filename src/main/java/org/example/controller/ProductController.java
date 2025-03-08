package org.example.controller;

import org.example.custom.exception.CustomException;
import org.example.model.dao.ProductDaoImp;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import static org.example.config.Color.*;

public class ProductController {
    public void listProducts() throws CustomException {
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
        ProductList products = productDao.queryAllProducts(1);

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
    }
}
