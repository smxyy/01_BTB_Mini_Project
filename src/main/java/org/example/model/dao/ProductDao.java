package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;

import java.util.List;

public interface ProductDao {
    ProductList queryAllProducts(int page) throws CustomException;
    Product readProductById(int id) throws CustomException;
    int deleteProductById(int id) throws CustomException;
    void setRow(int number);
    void saveProductToDatabase(List<Product> product, String type) throws CustomException;
    Product getProductById(int id) throws CustomException;
    List<Product> searchProductByName(String name) throws CustomException;
}