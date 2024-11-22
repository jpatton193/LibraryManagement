package src;

public class Item{
    // barcode is String rather than int because number can be larger than int limit
    public String barcode;
    public static int noOfItems = 0;
    // author/artist aka creator
    private String creator;
    private String title;
    private int year;
    private String isbn;
    private String type;
    public Item(String[] array){
        this.barcode = array[0];
        this.creator = array[1];
        this.title = array[2];
        this.type = array[3];
        String yearString = array[4];
        // convert year to int
        this.year = Integer.parseInt(yearString);
        this.isbn = array[5];
        noOfItems++;
    }
    public String getBarcode(){
        return barcode;
    }
    public String getTitle(){
        return title;
    }

    public String toString(){
        return(barcode + ","+ creator+","+title+","+type+","+year+","+isbn);
    }
    public String getType(){
        return type;
    }
    public int count(){
        return noOfItems;
    }
}