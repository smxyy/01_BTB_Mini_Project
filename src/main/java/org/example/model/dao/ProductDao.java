package org.example.model.dao;

import org.example.custom.exception.CustomException;
import org.example.model.entity.Product;

import java.util.List;

public interface ProductDao {
    List<Product> queryAllProducts() throws CustomException;
    int addNewProduct(Product product) throws CustomException;
    Product searchProductById(int id) throws CustomException;
    int updateProductById(int id) throws CustomException;
    int deleteProductById(int id) throws CustomException;
    void setRow(int number);
    void saveProductToDatabase() throws CustomException;
    void backUp() throws CustomException;
    void restoreVersion() throws CustomException;
}