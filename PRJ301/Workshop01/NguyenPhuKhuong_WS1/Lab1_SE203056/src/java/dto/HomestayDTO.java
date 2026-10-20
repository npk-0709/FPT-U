package dto;
/** 
 * HomestayDTO
 * @author Khuong
 */
public class HomestayDTO {
    private String homeID;
    private String description;
    private int price;
    private String address;
    private String userID;

    public HomestayDTO() {
    }
    public HomestayDTO(String homeID, String description, int price, String address, String userID) {
        this.homeID = homeID;
        this.description = description;
        this.price = price;
        this.address = address;
        this.userID = userID;
    }
    public String getHomeID() {
        return homeID;
    }
    public void setHomeID(String homeID) {
        this.homeID = homeID;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
}
