
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;

public class LibraryManager extends ArrayList<Book> {

    public Book createBook(Scanner sc) {
        System.out.print("Nhap Bookid: ");
        sc.nextLine();
        String bookId = sc.nextLine();

        System.out.print("Nhap Title: ");
        String title = sc.nextLine();
        System.out.print("Nhap Author: ");
        String author = sc.nextLine();
        System.out.print("Nhap Publisher: ");
        String publisher = sc.nextLine();
        System.out.print("Nhap PublishedYear: ");
        int publishedYear = sc.nextInt();
        sc.nextLine();
        Book b = new Book(bookId, title, author, publisher, publishedYear);
        return b;
    }

    public void addBook(Book b) {
        add(b);
    }

    public Book searchById(String id) {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Book currentBook = get(i);
            if (currentBook.getBookId().equalsIgnoreCase(id)) {
                return currentBook;
            }
        }
        return null;
    }

    public boolean updateBook(String id, Scanner sc) {

        if (this.searchById(id) == null) {
            return false;
        } else {
            Book currentBook = this.searchById(id);
            System.out.print("Nhap Title: ");
            String title = sc.nextLine();
            currentBook.setTitle(title);
            System.out.print("Nhap Author: ");
            String author = sc.nextLine();
            currentBook.setAuthor(author);
            System.out.print("Nhap Publisher: ");
            String publisher = sc.nextLine();
            currentBook.setPublisher(publisher);
            System.out.print("Nhap PublishedYear: ");
            int publishedYear = sc.nextInt();
            currentBook.setPublishedYear(publishedYear);
            System.out.print("Update Done !");
        }
        return true;
    }

    public boolean deleteBook(String id) {
        if (this.searchById(id) == null) {
            return false;
        } else {
            this.remove(this.searchById(id));

        }
        return true;
    }

    public void displayAll() {
        int sizeOfList = size();
        for (int i = 0; i < sizeOfList; ++i) {
            Book currentBook = get(i);
            System.out.println(currentBook.toString());
        }
        if (sizeOfList == 0) {
            System.out.println("Library is empty !");
        }
    }
}
