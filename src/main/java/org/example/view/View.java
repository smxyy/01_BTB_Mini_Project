package org.example.view;

import org.example.controller.ProductController;
import org.example.custom.exception.CustomException;
import org.example.model.entity.ProductList;
import org.example.utils.Helper;
import org.example.model.entity.ProductTempList;

import java.util.Scanner;
import java.util.regex.Pattern;

import static org.example.config.Color.*;

public class View {
    public View() throws CustomException {
        this.init();
    }

    void init () throws CustomException {
        Scanner scanner = new Scanner(System.in);
        ProductController product = new ProductController();
        ProductTempList tempList = new ProductTempList();

        int currentPage = 1;
        while(true) {
            ProductList productList = product.listProducts(currentPage);
            this.showMenu();

            boolean isOption = false;
            do {
                isOption = false;
                System.out.print(YELLOW.getCode() + "=> Choose an option(): " + RESET.getCode());
                String option = scanner.nextLine().toLowerCase();
                if (!option.isBlank()) {
                    if(Pattern.matches("^[a-zA-Z]+$", option)) {
                        switch (option) {
                            case "n", "p", "f", "l", "g" -> currentPage = product.showPagination(option, productList);
                            case "w" -> {
                                tempList.writeProduct();
                            }
                            case "r" -> {
                            }
                            case "u" -> {
                            }
                            case "d" -> {
                                tempList.deleteProductById();
                            }
                            case "s" -> {
                            }
                            case "se" -> {
                                product.setRow();
                                currentPage = 1;
                            }
                            case "sa" -> {
                                product.saveProduct();
                            }
                            case "un" -> {
                                product.unsavedController();
                            }
                            case "ba" -> {
                            }
                            case "re" -> {
                            }
                            case "e" -> {
                                return;
                            }
                            default -> {
                                Helper.printMessage("This option doesn't have!", 0);
                                isOption = true;
                            }
                        }
                    } else {
                        Helper.printMessage("Option is allowed only letter!", 0);
                        isOption = true;
                    }
                } else {
                    Helper.printMessage("Option not allowed empty!", 0);
                    isOption = true;
                }
            } while (isOption);
        }
    }

    public void showMenu() {
        System.out.println(" ".repeat(25) + "_".repeat(25) + " Menu " + "_".repeat(25));

        System.out.print(GREEN.getCode() + "\t\tN." + RESET.getCode()+ " Next Page\t\t");
        System.out.print(GREEN.getCode() + "P." + RESET.getCode()+ " Previous Page\t\t");
        System.out.print(GREEN.getCode() + "F." + RESET.getCode()+ " First Page\t\t");
        System.out.print(GREEN.getCode() + "L." + RESET.getCode()+ " Last Page\t\t");
        System.out.print(GREEN.getCode() + "G." + RESET.getCode()+ " Goto\n\n");

        System.out.print(GREEN.getCode() + "W)" + RESET.getCode() + " Write\t\t");
        System.out.print(GREEN.getCode() + "R)" + RESET.getCode() + " Read (id)\t\t");
        System.out.print(GREEN.getCode() + "U)" + RESET.getCode() + " Update\t\t");
        System.out.print(GREEN.getCode() + "D)" + RESET.getCode() + " Delete\t\t");
        System.out.print(GREEN.getCode() + "S)" + RESET.getCode() + " Search (name)\t\t");
        System.out.print(GREEN.getCode() + "Se)" + RESET.getCode() + " Set row\n");
        System.out.print(GREEN.getCode() + "Sa)" + RESET.getCode() + " Saved\t\t");
        System.out.print(GREEN.getCode() + "Un)" + RESET.getCode() + " Unsaved\t\t\t");
        System.out.print(GREEN.getCode() + "Ba)" + RESET.getCode() + " Back up\t\t");
        System.out.print(GREEN.getCode() + "Re)" + RESET.getCode() + " Resort\t\t");
        System.out.print(GREEN.getCode() + "E)" + RESET.getCode() + " Exit\n");
        System.out.println(" ".repeat(25) + "_".repeat(57));
    }

}
