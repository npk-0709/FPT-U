
package dto;

/**
 *
 * @author Khuong
 */
public class UserDTO {
    private String user_ID;
    private String fullName;
    private String password;
    private String role_ID;

    public UserDTO() {
    }

    public UserDTO(String user_ID, String fullName, String password, String role_ID) {
        this.user_ID = user_ID;
        this.fullName = fullName;
        this.password = password;
        this.role_ID = role_ID;
    }

    public String getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(String user_ID) {
        this.user_ID = user_ID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole_ID() {
        return role_ID;
    }

    public void setRole_ID(String role_ID) {
        this.role_ID = role_ID;
    }
    
}
