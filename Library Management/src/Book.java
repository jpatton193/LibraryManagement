package src;

public class Book extends Item {
    public static int noOfBooks = 0;
    public Book(String[] array) {
        super(array);
        noOfBooks++;
    }
    public int count(){
        return noOfBooks;
    }
}
