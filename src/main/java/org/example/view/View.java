package org.example.view;

import org.example.config.Color;
import org.example.controller.BackupRestoreController;
import org.example.controller.ProductController;
import org.example.custom.exception.CustomException;
import org.example.model.entity.ProductList;
import org.example.utils.Helper;
import org.example.model.entity.ProductTempList;
import org.nocrala.tools.texttablefmt.BorderStyle;
import org.nocrala.tools.texttablefmt.CellStyle;
import org.nocrala.tools.texttablefmt.ShownBorders;
import org.nocrala.tools.texttablefmt.Table;

import java.io.File;
import java.util.Map;
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
        BackupRestoreController backupRestoreController = new BackupRestoreController();
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
                                product.writeController();
                            }
                            case "r" -> {
                                product.readById();
                            }
                            case "u" -> {
                                product.updateProductTemp();
                            }
                            case "d" -> {
                                product.deleteByIdController();
                            }
                            case "s" -> {
                                product.searchByNameController();

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
                                System.out.println("\n" + "=".repeat(35));
                                char answer = Helper.inputChar("Are you sure you want to backup the data (y/n)? : ");
                                if (answer == 'y'){
                                    int result = backupRestoreController.handleBackup();
                                    if (result == 0)
                                        Helper.printMessage("Database backup successfully!", 1);
                                    else
                                        Helper.printMessage("Fail to backup data from database!", 0);
                                    Helper.printMessage("Enter to continue...", 2);
                                    scanner.nextLine();
                                }
                                else if (answer == 'n') {
                                    break;
                                }
                                else {
                                    Helper.printMessage("Invalid Input", 2);
                                }
                            }
                            case "re" -> {
                                // Load existing backup files from the properties file
                                backupRestoreController.loadBackupFilesController();
                                CellStyle cellStyle = new CellStyle(CellStyle.HorizontalAlign.LEFT);
                                Table table = new Table(2, BorderStyle.UNICODE_ROUND_BOX_WIDE, ShownBorders.SURROUND_HEADER_AND_COLUMNS);

                                // Table column's width
                                table.setColumnWidth(0, 5, 8);

                                // Table header
                                table.addCell("List of Backup Data", new CellStyle(CellStyle.HorizontalAlign.CENTER), 2);

                                // Table body
                                Map<Integer, String> backupFiles = backupRestoreController.getBackupFilesController();
                                for (Map.Entry<Integer, String> entry : backupFiles.entrySet()) {
                                    table.addCell(String.valueOf(entry.getKey()), cellStyle);
                                    table.addCell(entry.getValue(), cellStyle);
                                }
                                System.out.println(table.render());
                                int choice = Helper.validateChoice(backupFiles.size(), "=> Enter backup_id to restore: ", false, true,
                                        "The backup version that you input, doesn't exist!");
                                for (Map.Entry<Integer, String> entry : backupFiles.entrySet()) {
                                    if (choice == entry.getKey()){
                                        String backupFilePath = entry.getValue();
                                        File file = new File(backupFilePath);
                                        if (!file.exists()) {
                                            System.out.println("Backup file not found: " + backupFilePath);
                                        } else {
                                            System.out.println("Found backup file: " + backupFilePath);
                                        }

                                        int result = backupRestoreController.handleRestore(backupFilePath);
                                        if (result == 0){
                                            Helper.printMessage("Database restore successfully!", 1);
                                        } else {
                                            Helper.printMessage("Fail to restore data from file to database!", 0);
                                        }
                                    }
                                }
                            }
                            case "e" -> {
                                Helper.printMessage("I wish you all the best!", 99);
                                System.exit(0);
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

        System.out.print(GREEN.getCode() + "\t\tN." + RESET.getCode() + " Next Page\t\t");
        System.out.print(GREEN.getCode() + "P." + RESET.getCode() + " Previous Page\t\t");
        System.out.print(GREEN.getCode() + "F." + RESET.getCode() + " First Page\t\t");
        System.out.print(GREEN.getCode() + "L." + RESET.getCode() + " Last Page\t\t");
        System.out.print(GREEN.getCode() + "G." + RESET.getCode() + " Goto\n\n");

        System.out.print(GREEN.getCode() + "W)" + RESET.getCode() + " Write\t\t");
        System.out.print(GREEN.getCode() + "R)" + RESET.getCode() + " Read (id)\t\t");
        System.out.print(GREEN.getCode() + "U)" + RESET.getCode() + " Update\t\t");
        System.out.print(GREEN.getCode() + "D)" + RESET.getCode() + " Delete\t\t");
        System.out.print(GREEN.getCode() + "S)" + RESET.getCode() + " Search (name)\t\t");
        System.out.print(GREEN.getCode() + "Se)" + RESET.getCode() + " Set row\n");
        System.out.print(GREEN.getCode() + "Sa)" + RESET.getCode() + " Saved\t\t");
        System.out.print(GREEN.getCode() + "Un)" + RESET.getCode() + " Unsaved\t\t\t");
        System.out.print(GREEN.getCode() + "Ba)" + RESET.getCode() + " Back up\t\t");
        System.out.print(GREEN.getCode() + "Re)" + RESET.getCode() + " Restore\t\t");
        System.out.print(GREEN.getCode() + "E)" + RESET.getCode() + " Exit\n");
        System.out.println(" ".repeat(25) + "_".repeat(57));
    }

}
