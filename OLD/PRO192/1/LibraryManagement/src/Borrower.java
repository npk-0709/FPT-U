/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Borrower {

    private String borrowerId;
    private String fullName;
    private String phone;
    private String email;

    public Borrower(String borrowerId, String fullName, String phone, String email) {
        this.borrowerId = borrowerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
    }

    public Borrower() {
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Borrower{" + "borrowerId=" + borrowerId + ", fullName=" + fullName + ", phone=" + phone + ", email=" + email + '}';
    }

    public void displayInfo() {
        System.out.println(this.toString());
    }
}
