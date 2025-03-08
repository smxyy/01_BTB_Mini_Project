package org.example.utils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;
import org.example.config.Color;

public class Helper {
    static Scanner scanner = new Scanner(System.in);
    public static void printMessage(String message, int flag){
        if (flag == 1)
            System.out.println( "✅ " + Color.GREEN.getCode() + message + Color.RESET.getCode());
        else if (flag == 0)
            System.out.println( "❌ " + Color.RED.getCode() + message + Color.RESET.getCode());
        else if (flag == 2)
            System.out.println( "⚠️ " + Color.YELLOW.getCode() + message + Color.RESET.getCode());
        else if (flag == 99)
            System.out.println("🥰 " + Color.PURPLE.getCode() + message + Color.RESET.getCode() + " 🚀");
    }

    public static int validateChoice(int choice, String label, boolean zero, boolean count, String message){
        boolean validatedChoice;
        int checkedChoice = 0;
        int wrongInputCount = 0; // Track the number of wrong inputs
        do {
            System.out.print(label);
            String input = scanner.nextLine();
            String regex = "^[0-9]+$";
            validatedChoice = Pattern.matches(regex, input);
            if(validatedChoice) {
                checkedChoice = Integer.parseInt(input);
                if(zero){
                    if(checkedChoice > choice) {
                        printMessage("Please input choice that starts from 0 to " + choice + "!", 0);
                        validatedChoice = false;
                    }
                } else {
                    if(checkedChoice == 0) {
                        printMessage("Please input choice that starts from 1 to " + choice + "!", 0);
                        validatedChoice = false;
                    } else if (checkedChoice > choice) {
                        printMessage(message, 0);
                    }
                }
            } else {
                if (count) {
                    wrongInputCount++; // Increment wrong input count
                    if (input.isEmpty()){
                        printMessage("Empty is not allowed!",0);
                    } else {
                        printMessage("Invalid input! Accepts Id", 0);
                    }
                    if (wrongInputCount == 3) {
                        // Ask user if they want to reset the count
                        System.out.print("Do you want to continue? (y/n) : ");
                        String retry = scanner.nextLine().toLowerCase();
                        if (retry.equals("y")) {
                            wrongInputCount = 0;
                        } else {
                            return -1;
                        }
                    }
                }
            }

        } while(!validatedChoice);
        return checkedChoice;
    }

    public static <T extends Number> T validateNumberInput(String label, Class<T> type, String keyword){
        String input;
        String regex = "^\\d+(\\.\\d+)?$";

        do {
            System.out.print(label);
            input = scanner.nextLine();

            if (input.matches(regex)){
                try {
                    if (type == Integer.class){
                        int value = Integer.parseInt(input);
                        if (value > 0)
                            return type.cast(value);
                        else
                            printMessage(keyword + " must be greater than 0. Please try again.", 0);
                    } else if (type == Double.class) {
                        double value = Double.parseDouble(input);
                        if (value > 0)
                            return type.cast(value);
                        else
                            printMessage(keyword + " must be greater than 0. Please try again.", 0);
                    }
                } catch (NumberFormatException e){
                    printMessage("Invalid number format. Please try again.", 0);
                }
            } else
                printMessage("Invalid input. Enter a valid number.", 0);
        } while(true);
    }

    public static String validateString(String label, String type){
        String input;
        String regex = "";
        String message = "Please enter a valid " + type + "!";
        boolean validatedInput;
        do {
            System.out.print(label);
            input = scanner.nextLine();
            if(type.equalsIgnoreCase("address")){
                regex = "^[0-9a-zA-Z\\s,#.-]{2,100}$";
            } else if (type.equalsIgnoreCase("name")){
                regex = "^[a-zA-Z ]+$";
            }
            validatedInput = Pattern.matches(regex, input);
            if(!validatedInput)
                printMessage(message, 0);
        } while(!validatedInput);

        return input;
    }

    public static char inputChar(String label){
        String input;
        String message = "Please enter only y/Y or n/N!!";
        boolean validatedChoice;
        String regex = "^[yYnN]$";
        do{
            System.out.print(Color.YELLOW.getCode() + label + Color.RESET.getCode());
            input = scanner.nextLine();
            validatedChoice = Pattern.matches(regex, input);
            if(!validatedChoice)
                printMessage(message, 0);
        } while(!validatedChoice);
        return input.toLowerCase().charAt(0);
    }
}
