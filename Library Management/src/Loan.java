package src;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class Loan {
    public static int noOfLoans;
    private String userID;
    private String barcode;
    private String dueDate;
    private String loanID;
    private String library;
    private int noOfRenewals;
    private String type;
    private int loanWeeks;

    public Loan(String[] array){
        this.loanID = array[0];
        this.library = array[1];
        this.userID = array[2];
        this.barcode = array[3];
        this.noOfRenewals = Integer.parseInt(array[4]);
        this.type = array[5];
        if (array[6] == null) {
            // If type = book, then loanWeeks = 4, otherwise it is 1.
            this.loanWeeks = this.type.equals("book") ? 4 : 1;
            this.dueDate = calculateDate(loanWeeks, null);
        }
        else{
            this.dueDate = array[6];
        }
        noOfLoans++;
    }
    public void renew(){
        // As a book can be renewed for 2 weeks and multimedia 1 week,
        // I divided the int loanWeeks by two to get the correct amount of weeks for the renewal.
        dueDate = calculateDate((loanWeeks/2), dueDate);
        noOfRenewals++;
    }
    public String calculateDate(int weeks, String initialDueDate){
        LocalDate startDate;
        if (initialDueDate == null){
            startDate = LocalDate.now();
        }
        else{
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            startDate =  LocalDate.parse(initialDueDate, formatter);
        }
        //function to calculate date (weeks) amount of weeks from today's date
        LocalDate result = startDate.plusWeeks(weeks);
        // Changes format from LocalDate's default to dd/mm/yyyy e.g. 01/01/2000
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return result.format(formatter);
    }
    public String getType(){
        return type;
    }
    public String getBarcode(){
        return barcode;
    }
    public String getLoanId(){
        return loanID;
    }
    public String getDueDate(){
        return dueDate;
    }
    public int getNoOfRenewals(){
        return noOfRenewals;
    }
    public String toString(){
        return (loanID+","+library+","+userID+","+barcode+","+type+","+noOfRenewals+","+dueDate);
    }
}
