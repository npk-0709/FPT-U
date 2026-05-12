
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);
        System.out.println("---------------MileStone Library--------------");
        System.out.print("Nhap Bookid: ");
        String bookId = myScanner.nextLine();

        System.out.print("Nhap Title: ");
        String title = myScanner.nextLine();
        System.out.print("Nhap Author: ");
        String author = myScanner.nextLine();
        System.out.print("Nhap Publisher: ");
        String publisher = myScanner.nextLine();
        System.out.print("Nhap PublishedYear: ");
        int publishedYear = myScanner.nextInt();
        myScanner.nextLine();

        System.out.print("Nhap borrowerId: ");
        String borrowerId = myScanner.nextLine();
        System.out.print("Nhap fullName: ");
        String fullName = myScanner.nextLine();
        System.out.print("Nhap phone: ");
        String phone = myScanner.nextLine();
        System.out.print("Nhap PublishedYear: ");
        String email = myScanner.nextLine();

        System.out.print("Nhap loanId: ");
        String loanId = myScanner.nextLine();
        System.out.print("Nhap borrowDate: ");
        String borrowDate = myScanner.nextLine();
        System.out.print("Nhap returnDate: ");
        String returnDate = myScanner.nextLine();

        Borrower borrower1 = new Borrower(borrowerId, fullName, phone, email);
        Book book1 = new Book(bookId, title, author, publisher, publishedYear);
        Loan loan1 = new Loan(loanId, book1, borrower1, borrowDate, returnDate);

        book1.displayInfo();
        System.out.print("New author: ");
        String newauthor = myScanner.nextLine();
        book1.setAuthor(newauthor);
        book1.displayInfo();

        borrower1.displayInfo();
        loan1.displayInfo();

        System.out.println("---------------MileStone Library--------------");
        System.out.println("----------------------------------------------");
    }

}
