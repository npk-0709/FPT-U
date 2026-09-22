
public class Main {
    public static void main(String[] args) {
        // tạo menu cho toàn bộ chức năng của SLL, bao gồm các chức năng: addHead, addTail, traversal, searchByPid, deleteByPid, sortByPrice, saveToFile, loadFromFile

        SLL list = new SLL();

        while (true) {
            System.out.println("1. Add head");
            System.out.println("2. Add tail");
            System.out.println("3. Traversal");
            System.out.println("4. Search by pid");
            System.out.println("5. Delete by pid");
            System.out.println("6. Sort by price");
            System.out.println("7. Save to file");
            System.out.println("8. Load from file");
            System.out.println("9. Exit");
            System.out.print("Choose an option: ");

            int choice = new java.util.Scanner(System.in).nextInt();

            switch (choice) {
                case 1:
                    // add head
                    System.out.println("Enter product name");


                    break;
                case 2:
                    // add tail
                    break;
                case 3:
                    list.traversal();
                    break;
                case 4:
                    // search by pid
                    break;
                case 5:
                    // delete by pid
                    break;
                case 6:
                    // sort by price
                    break;
                case 7:
                    // save to file
                    break;
                case 8:
                    // load from file
                    break;
                case 9:
                    System.exit(0);
                default:
                    System.out.println("Invalid option");
            }
        }

    }
}