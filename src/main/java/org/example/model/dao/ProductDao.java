package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;
import org.example.model.entity.ProductList;

public interface ProductDao {
    ProductList queryAllProducts(int page) throws CustomException;
    int addNewProduct(Product product) throws CustomException;
    Product searchProductById(int id) throws CustomException;
    int updateProductById(int id) throws CustomException;
    int deleteProductById(int id) throws CustomException;
    void setRow(int number);
    void saveProductToDatabase() throws CustomException;
    void backUp() throws CustomException;
    void restoreVersion() throws CustomException;
}