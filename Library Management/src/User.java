package src;

public class User {
    String userId, firstName, lastName, email;
    public User(String[] array){
        this.userId = array[0];
        this.firstName = array[1];
        this.lastName = array[2];
        this.email = array[3];
    }
    public String getUserId(){
        return userId;
    }
}
