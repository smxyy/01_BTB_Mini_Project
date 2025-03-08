package org.example.view;

import org.example.controller.ProductController;
import org.example.custom.exception.CustomException;

public class View {
    public View() throws CustomException {
        this.init();
    }

    void init () throws CustomException {
        ProductController product = new ProductController();
        product.listProducts();
    }
}
