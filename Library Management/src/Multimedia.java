package src;

public class Multimedia extends Item{
    public static int noOfMultimedia = 0;
    public Multimedia(String[] array) {
        super(array);
        noOfMultimedia++;
    }
    public int count(){
    return noOfMultimedia;
    }
}
