/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Khuong
 */
public class Loan {
    private String loanId;
    private Book book;
    private Borrower borrower;
    private String borrowDate;
    private String returnDate;

    public Loan(String loanId, Book book, Borrower borrower, String borrowDate, String returnDate) {
        this.loanId = loanId;
        this.book = book;
        this.borrower = borrower;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public Loan() {
    }

    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public Borrower getBorrower() {
        return borrower;
    }

    public void setBorrower(Borrower borrower) {
        this.borrower = borrower;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    @Override
    public String toString() {
        return "Loan{" + "loanId=" + loanId + ", book=" + book + ", borrower=" + borrower + ", borrowDate=" + borrowDate + ", returnDate=" + returnDate + '}';
    }
    public void displayInfo() {
        System.out.println(this.toString());
    }
}
