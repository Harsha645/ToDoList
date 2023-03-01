package tm;

public class todoTM {
    private String id;
    private String description;
    private String userID;

    public todoTM() {
    }

    public todoTM(String id, String description, String userID) {
        this.id = id;
        this.description = description;
        this.userID = userID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String toString() {
        return description;
    }
}
