
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner myScanner = new Scanner(System.in);
        LibraryManager libm = new LibraryManager();
        while (true) {
            System.out.println("---------------MileStone Library--------------");
            System.out.println("1. Add Book");
            System.out.println("2. Search Book by ID");
            System.out.println("3. Update Book");
            System.out.println("4. Delete Book");
            System.out.println("5. Display All Books");
            System.out.println("6. Exit\n->");
            int getChoice = myScanner.nextInt();
            if (getChoice == 6) {
                break;
            } else if (getChoice == 1) {
                Book b = libm.createBook(myScanner);
                if (libm.searchById(b.getBookId()) == null) {
                    libm.addBook(b);
                } else {
                    System.out.println("Book existed !");
                }

            } else if (getChoice == 2) {

                System.out.print("Input BookId to Find: ");
                myScanner.nextLine();
                String bookId = myScanner.nextLine();
                if (libm.searchById(bookId) != null) {
                    System.out.println(libm.searchById(bookId).toString());
                } else {
                    System.out.println("Can't find book ");
                }

            } else if (getChoice == 3) {
                System.out.print("Input BookId to Find: ");
                myScanner.nextLine();
                String bookId = myScanner.nextLine();
                if (libm.searchById(bookId) == null) {
                    System.out.println("No Book To Update !");
                } else {
                    libm.updateBook(bookId, myScanner);
                }
            } else if (getChoice == 4) {
                System.out.print("Input BookId to Find: ");
                myScanner.nextLine();
                String bookId = myScanner.nextLine();
                if (libm.deleteBook(bookId) == true) {
                    System.out.println("Delete Successfully !");
                } else {
                    System.out.println("Can't find book ID!");
                }
            } else if (getChoice == 5) {
                libm.displayAll();
            } else {
                System.out.println("Input Again !");
            }
        }

        System.out.println("----------------------------------------------");
    }

}
