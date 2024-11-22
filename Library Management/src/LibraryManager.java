package src;

import java.io.*;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.util.Scanner;
import java.util.regex.Pattern;

public class LibraryManager {
    // Array of lists for items, users and loans initialised
    public static ArrayList<Object> users = loadCSV("USERS.csv", User.class, null);
    public static ArrayList<Object> items = loadCSV("ITEMS.csv", Item.class, null);
    public static String loanHeadings = "Loan ID, Library, User ID, Barcode, Type, Number of Renewals, Due Date\n";
    public static ArrayList<Object> loans = loadCSV("LOAN.csv", Loan.class, loanHeadings);


    public static void main(String[] args){
        System.out.println("Current Loans:");
        // Populate program with data from loans
        viewLoanedItems();
        System.out.println("Note: When exiting, please select 'Save and exit' to commit changes to LOAN.csv .");
        boolean running = true;
        Scanner scanner = new Scanner(System.in);
        // Menu with options for other methods
        while (running) {
            System.out.println("""
                    
                    -----MENU-----
                    1)Issue Item
                    2)Renew existing loan
                    3)Mark item as returned
                    4)List items currently on loan
                    5)Report on current loans
                    6)Search for item
                    7)Save progress to file
                    8)Save and exit""");
            System.out.print("Enter your choice (1-8): ");
            try{
                int choice = scanner.nextInt();
                scanner.nextLine();
                // Used switch case rather than many if/else statements for readability, improved efficiency
                switch(choice) {
                    case 1:
                        issueItem(findUser());
                        break;
                    case 2:
                        renewLoan();
                        break;
                    case 3:
                        returnItem();
                        break;
                    case 4:
                        viewLoanedItems();
                        break;
                    case 5:
                        loanReport();
                        break;
                    case 6:
                        searchForItem();
                        break;
                    case 7:
                        updateFile("LOAN.csv", loanHeadings, loans);
                        System.out.println("Loans saved.");
                        break;
                    case 8:
                        updateFile("LOAN.csv", loanHeadings, loans);
                        System.out.println("Loans saved.\nProgram ending. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 8");
                        break;
                }
            }
            // If a non-numeric input is given, this error may occur.
            catch(java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number from 1 to 8");
                scanner.nextLine();
            }
        }
    }
    public static void issueItem(String userId){
        Scanner scanner = new Scanner(System.in);
        // Inputting barcodes became common functionality, so it has its own method.
        String barcode = inputBarcode();
        // As did searching through one of the arraylists using said barcode.
        Object loanObj = findByBarcode(loans, barcode);
        if (loanObj != null){
            System.out.println("Error: item is already on loan.");
            return;
        }
        else if(userId == null || barcode == null){
            return;
        }
        Object foundItem = findByBarcode(items, barcode);
        Item item = (Item) foundItem;
        String title = null;
        String type = null;
        if (item != null) {
            title = item.getTitle();
            type = item.getType();
        }
        // If item is found and not already on loan, user is informed,
        // and asked to confirm that they want to loan it to the given user.
        System.out.println("Item found successfully: "+title+".\nLoan to user: "+userId+"? (y/n):");
        String response = scanner.nextLine().toLowerCase();
        if (response.equals("yes")||response.equals("y")){
            String loanId;
            // If there are no loans, give this loan the default loan ID.
            if (loans.isEmpty()){
                loanId = "BCL000001";
            }
            // Otherwise, take the most recent loan id, and add one to get this loan's id.
            else{
                int lastItemIndex = item.count() -1;
                Loan lastLoan = (Loan) loans.get(lastItemIndex);
                String lastLoanId = lastLoan.getLoanId();
                int lastIdNum = Integer.parseInt(lastLoanId.substring(3));
                loanId = "BCL" + String.format("%05d", lastIdNum + 1);
            }
            // Create new loan object, add it to the loans arrayList.
            String[] data = {loanId, "Belfast City Library", userId, barcode, String.valueOf(0), type, null};
            Loan newLoan = new Loan(data);
            loans.add(newLoan);
            System.out.println("Loan added.");
            }
        }
    public static void renewLoan(){
        Scanner scanner = new Scanner(System.in);
        String barcode = inputBarcode();
        Object object =  findByBarcode(loans, barcode);
        // If findByBarcode cannot find a match in the loans file, then the item given must not be on loan.
        if (object == null){
            System.out.println("Error: Item not on loan.");
            return;
        }
        Loan loan = (Loan) object;
        int noOfRenewals = (loan.getNoOfRenewals());
        String type = loan.getType();
        // Check that the item isn't exceeding its maximum number of renewals.
        if (type.equalsIgnoreCase("multimedia") && noOfRenewals >= 2){
            System.out.println("Error: Multimedia items cannot be renewed more than twice.");
            return;
        }
        else if(type.equalsIgnoreCase("book") && noOfRenewals >= 3){
            System.out.println("Error: Books cannot be renewed more than three times.");
            return;
        }
        System.out.println("Renew loan?");
        String input = scanner.nextLine().toLowerCase();
        if (input.equals("y") || input.equals("yes")){
            loan.renew();
            System.out.println("Loan renewed. New due date: "+loan.getDueDate());
        }
        else{
            System.out.println("Cancelled renewal");
        }
    }
    public static void returnItem(){
        Scanner scanner = new Scanner(System.in);
        String barcode = inputBarcode();
        Object object=  findByBarcode(loans, barcode);
        // if findByBarcode cannot find a match in the loans file, then the item given must not be on loan.
        if (object == null){
            System.out.println("Error: Item not on loan");
            return;
        }
        Loan loan = (Loan) object;
        System.out.println(loan);
        // Confirm that the librarian wants to return the item.
        System.out.println("Return item? (y/n): ");
        String input = scanner.nextLine().toLowerCase();
        // Iterate through the loans array, remove the object with a matching barcode.
        if (input.equals("y")|| input.equals("yes")){
            for (Object currentObj: loans){
                Loan currentLoan = (Loan) currentObj;
                if (currentLoan.getBarcode().equals(loan.getBarcode())){
                    loans.remove(currentObj);
                    System.out.println("Item returned successfully");
                    break;
                }
            }
        }
        else{
            System.out.println("Cancelled return.");
        }
    }
    public static void viewLoanedItems(){
        System.out.println(loanHeadings);
        // Using each loan's toString method to output their details.
        if (loans.isEmpty()){
            System.out.println("Error: No current loans");
            return;
        }
        System.out.println("Listing all loan items: ");
        for (Object loan: loans){
            System.out.println(loan);
        }
    }
    public static void loanReport(){
        // fewRenewals = items with 0 or 1 renewal.
        double fewRenewals = 0;
        // many renewals = items with 2+ renewals
        double manyRenewals = 0;
        int noOfBooks = 0;
        int noOfMultimedia = 0;
        // Library is hard coded.
        String library = "Belfast City Library";
        // Loop through each loan, find its type, how many renewals it's had, update appropriate variables.
        for (Object obj: loans){
            Loan loan = (Loan) obj;
            int renewals = loan.getNoOfRenewals();
            String type = loan.getType();
            System.out.println(type);
            if (renewals >= 2){
                manyRenewals++;
            }
            else{
                fewRenewals++;
            }
            if (type.equalsIgnoreCase("book")){
                noOfBooks++;
            }
            else if(type.equalsIgnoreCase("multimedia")) {
                noOfMultimedia++;
            }
        }
        double totalLoans = manyRenewals + fewRenewals;
        // If the loans file was empty, then inform the user rather than outputting nothing.
        if (totalLoans == 0){
            System.out.println("Error: Couldn't create report - no loans available");
            return;
        }
        // Output a report on all loans including the library, how many books/multimedia there are,
        // and what percentage have been renewed more than once.
        double twoOrMore = (manyRenewals / totalLoans) * 100;
        System.out.println("Library: "+library+ "\nNumber of books: "+noOfBooks+"\nNumber of Multimedia: "
                +noOfMultimedia+"\n"+twoOrMore+"% have been renewed more than once." );
    }
    public static void searchForItem(){
        // Item Search functionality.
        String barcode = inputBarcode();
        Object result = findByBarcode(items, barcode);
        if (result == null){
            System.out.println("Error: Item not found.");
            return;
        }
        System.out.println(result);
    }
    public static Object findByBarcode(ArrayList<Object> array, String barcode) {
        // If the barcode belongs to a given item in an array, then the item will be returned.
        // Otherwise, null will be returned.
        for (Object item : array) {
            if (item instanceof Item) {
                Item currentItem = (Item) item;
                if (currentItem.getBarcode().equals(barcode)) {
                    return currentItem;
                }
            }
            else if(item instanceof Loan){
                Loan currentLoan = (Loan) item;
                if (currentLoan.getBarcode().equals(barcode)){
                    return currentLoan;
                }
            }
        }
        return null;
    }
    public static String inputBarcode() {
        Scanner scanner = new Scanner(System.in);
        // Loops until a barcode matching the regex is input.
        while (true) {
            System.out.println("Enter Item Barcode: ");
            String barcode = scanner.nextLine();
            String barcodeRegex = "\\d{8,9}";
            // Check input against regex.
            if (!barcode.matches(barcodeRegex)) {
                System.out.println("Barcode entered is in the wrong format. Please enter a 8 or 9 digit barcode," +
                                    "or enter cancel to return to the menu.");
            }
            if (barcode.equalsIgnoreCase("cancel")) {
                System.out.println("Search cancelled.");
                return null;
            }
            // End loop, return the barcode;
            else {
                return barcode;
            }
        }
    }
    public static String findUser() {
        Scanner scanner = new Scanner(System.in);
        // Regex to check if id input starts with B00 followed by 7 numeric characters.
        String userIdRegex = "^B00\\d{6}$";
        while (true) {
            // Loops until a user id matching the regex is input.
            System.out.println("Enter user id: ");
            String userId = scanner.nextLine();
            if (userId.equalsIgnoreCase("cancel")) {
                System.out.println("Search cancelled.");
                return null;
            }
            if (!Pattern.matches(userIdRegex, userId)) {
                System.out.println("Error: Please enter a user id in the format B001234567," +
                                    " or enter 'cancel' to return to menu.");
                continue;
            }

            // Once a valid potential user id is input, program tries to find its corresponding user.
            for (Object user : users) {
                User currentUser = (User) user;
                if (currentUser.getUserId().equals(userId)) {
                    // If a match is found, inform the librarian, return the userId.
                    System.out.println("User id recognised.");
                    return userId;
                }
            }
            // Inform the librarian that a match was not found
            System.out.println("Error: User ID not recognised.");
        }
    }
    public static void updateFile(String fileName, String headings, ArrayList<Object> array){
        // Many modules such as this are only used for the loans file, though they take parameters that would allow
        // them to work with other files
        try (FileWriter writer = new FileWriter(fileName)){
            // clear all lines apart from the first line (headings)
            writer.write(headings);
            System.out.println("Saving data to "+fileName+"...");
            //copy each objects 'toString' method output to file.
            for (Object item: array){
                writer.write(item.toString()+"\n");
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    // Function to load data from a given CSV file.
    public static ArrayList<Object> loadCSV(String filename, Class<?> classToUse, String headings){
        ArrayList<Object> array = new ArrayList<>();
        String currentLine;
        String splitData = ",";
        try(BufferedReader br = new BufferedReader(new FileReader(filename))){
            // skip header line
            br.readLine();
            Object obj;

            while ((currentLine = br.readLine()) != null){
                // current line saved as array
                String[] data = currentLine.split(splitData);
                // Checks if a book or multimedia is being created when item is classToUse
                if (classToUse == Item.class && data[3].equalsIgnoreCase("Book")){
                    obj = new Book(data);
                }
                else if(data[3].equalsIgnoreCase("Multimedia")){
                    obj = new Multimedia(data);
                }
                else {
                    Constructor<?> constructor = classToUse.getConstructor(String[].class);
                    // error line
                    obj = constructor.newInstance((Object) data);
                }
                array.add(obj);
            }
        }
        catch(FileNotFoundException e){
            System.out.println("CSV Read Error: File not found - "+filename);
            createCSV(filename, headings);
        }
        catch(NullPointerException e){
            System.out.println("Error - "+filename+" is empty.");
            createCSV(filename, headings);
        }
        catch(Exception e) {
            // Prints stack trace for testing/future debugging purposes to identify other exceptions that may occur.
            System.out.println("Exception occurred: " + e.getMessage());
            e.printStackTrace();
        }
        return array;
    }
    public static void createCSV(String filename, String headings){
        try(FileWriter writer = new FileWriter(filename)){
            System.out.println("New CSV file created:"+ filename);
            writer.write(headings);
        }
         catch (IOException e) {
             System.out.println("Error in creating new CSV file: " + e.getMessage());
        }
    }
}

