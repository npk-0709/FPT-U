package warehouse;

import java.time.LocalDate;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static WarehouseSystem warehouse = new WarehouseSystem();

    public static void main(String[] args) {
        System.out.println("==================================================================");
        System.out.println("||    HE THONG QUAN LY KHO HANG - WAREHOUSE MANAGEMENT          ||");
        System.out.println("||       CSD201 - Data Structures & Algorithms                  ||");
        System.out.println("==================================================================");

        initSampleData();

        boolean running = true;
        while (running) {
            printMainMenu();
            int choice = readInt("Lua chon: ");
            System.out.println();

            switch (choice) {
                case 1:
                    demoFIFO();
                    break;
                case 2:
                    demoLIFO();
                    break;
                case 3:
                    demoBST();
                    break;
                case 4:
                    interactiveMode();
                    break;
                case 5:
                    warehouse.displayAllInventory();
                    break;
                case 0:
                    System.out.println("Thoat chuong trinh. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("  Lua chon khong hop le!");
            }
        }
        scanner.close();
    }

    static void initSampleData() {
        System.out.println("\n=== Khoi tao du lieu mau ===");

        warehouse.registerProduct(new Product("SP001", "Sua tuoi Vinamilk",     "Thuc pham",  35000), "FIFO");
        warehouse.registerProduct(new Product("SP003", "Banh mi sandwich",      "Thuc pham",  25000), "FIFO");
        warehouse.registerProduct(new Product("SP005", "Nuoc trai cay Tropica", "Thuc pham",  22000), "FIFO");

        warehouse.registerProduct(new Product("SP002", "Bong den LED Philips",  "Dien tu",    45000), "LIFO");
        warehouse.registerProduct(new Product("SP004", "Pin AA Energizer",      "Dien tu",    30000), "LIFO");
        warehouse.registerProduct(new Product("SP006", "O cap dien Panasonic",  "Dien tu",    85000), "LIFO");

        warehouse.importGoods("SP001", 100, LocalDate.now().plusDays(7));
        warehouse.importGoods("SP001", 50,  LocalDate.now().plusDays(14));
        warehouse.importGoods("SP001", 80,  LocalDate.now().plusDays(21));

        warehouse.importGoods("SP002", 200, LocalDate.now().plusDays(365));
        warehouse.importGoods("SP002", 150, LocalDate.now().plusDays(730));

        warehouse.importGoods("SP003", 60,  LocalDate.now().plusDays(3));
        warehouse.importGoods("SP004", 300, LocalDate.now().plusDays(1825));
        warehouse.importGoods("SP005", 90,  LocalDate.now().plusDays(30));
        warehouse.importGoods("SP006", 50,  LocalDate.now().plusDays(3650));

        System.out.println("\n[OK] Khoi tao hoan tat!\n");
    }

    static void demoFIFO() {
        printSeparator("DEMO FIFO - Queue (Hang doi) - Nhap truoc Xuat truoc");

        System.out.println("\n1. Xem ton kho Sua tuoi TRUOC khi xuat:");
        warehouse.displayInventory("SP001");

        System.out.println("\n2. Xuat 120 san pham Sua tuoi (lo cu nhat se xuat truoc):");
        warehouse.exportGoods("SP001", 120);

        System.out.println("\n3. Xem ton kho Sua tuoi SAU khi xuat:");
        warehouse.displayInventory("SP001");

        System.out.println("\n4. Nhap them lo moi (HSD 28 ngay):");
        warehouse.importGoods("SP001", 70, LocalDate.now().plusDays(28));

        System.out.println("\n5. Xuat them 40 san pham:");
        warehouse.exportGoods("SP001", 40);

        System.out.println("\n6. Ton kho cuoi cung:");
        warehouse.displayInventory("SP001");

        System.out.println("\n>> Nhan xet: FIFO dam bao lo hang cu nhat (gan het han) luon");
        System.out.println("   duoc xuat truoc -> giam thieu hang hoa bi hong/qua han.");
        pause();
    }

    static void demoLIFO() {
        printSeparator("DEMO LIFO - Stack (Ngan xep) - Nhap sau Xuat truoc");

        System.out.println("\n1. Xem ton kho Bong den LED TRUOC khi xuat:");
        warehouse.displayInventory("SP002");

        System.out.println("\n2. Xuat 180 san pham Bong den (lo moi nhat se xuat truoc):");
        warehouse.exportGoods("SP002", 180);

        System.out.println("\n3. Xem ton kho Bong den SAU khi xuat:");
        warehouse.displayInventory("SP002");

        System.out.println("\n4. Nhap them 100 bong den loai moi (HSD 1000 ngay):");
        warehouse.importGoods("SP002", 100, LocalDate.now().plusDays(1000));

        System.out.println("\n5. Ton kho (dinh stack la lo moi nhat):");
        warehouse.displayInventory("SP002");

        System.out.println("\n6. Xuat 50 san pham:");
        warehouse.exportGoods("SP002", 50);

        System.out.println("\n7. Ton kho cuoi cung:");
        warehouse.displayInventory("SP002");

        System.out.println("\n>> Nhan xet: LIFO xuat hang moi nhat truoc, phu hop voi hang");
        System.out.println("   xep chong len nhau hoac hang khong co yeu cau ve HSD.");
        pause();
    }

    static void demoBST() {
        printSeparator("DEMO BST - Cay Nhi Phan Tim Kiem - Quan ly theo ma vach");

        System.out.println("\n1. Danh muc san pham (duyet In-Order - tang dan theo barcode):");
        warehouse.displayCatalog();

        System.out.println("\n2. Cau truc cay BST:");
        warehouse.displayBSTStructure();

        System.out.println("\n3. Tim kiem barcode 'SP003':");
        warehouse.searchProduct("SP003");

        System.out.println("\n4. Tim kiem barcode 'SP999' (khong ton tai):");
        warehouse.searchProduct("SP999");

        System.out.println("\n5. Them san pham moi 'SP007':");
        warehouse.registerProduct(
                new Product("SP007", "Mi goi Hao Hao", "Thuc pham", 5000), "FIFO");
        warehouse.importGoods("SP007", 500, LocalDate.now().plusDays(180));

        System.out.println("\n6. Danh muc sau khi them SP007:");
        warehouse.displayCatalog();

        System.out.println("\n7. Xoa san pham 'SP004' khoi BST:");
        warehouse.removeProduct("SP004");

        System.out.println("\n8. Danh muc sau khi xoa SP004:");
        warehouse.displayCatalog();

        System.out.println("\n9. Cau truc BST sau khi xoa:");
        warehouse.displayBSTStructure();

        System.out.println("\n>> Nhan xet: BST tim kiem O(log n) theo barcode,");
        System.out.println("   duyet In-Order cho danh sach sap xep, them/xoa linh hoat.");
        pause();
    }

    static void interactiveMode() {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== CHE DO TUONG TAC ===");
            System.out.println("  1. Dang ky san pham moi");
            System.out.println("  2. Nhap kho");
            System.out.println("  3. Xuat kho");
            System.out.println("  4. Tim kiem san pham (BST)");
            System.out.println("  5. Xem ton kho san pham");
            System.out.println("  6. Xem danh muc (BST In-Order)");
            System.out.println("  7. Xem cau truc BST");
            System.out.println("  0. Quay lai");

            int c = readInt("Lua chon: ");
            switch (c) {
                case 1: interactiveRegister(); break;
                case 2: interactiveImport();   break;
                case 3: interactiveExport();   break;
                case 4: {
                    String bc = readString("Nhap barcode: ");
                    warehouse.searchProduct(bc);
                    break;
                }
                case 5: {
                    String bc = readString("Nhap barcode: ");
                    warehouse.displayInventory(bc);
                    break;
                }
                case 6: warehouse.displayCatalog();        break;
                case 7: warehouse.displayBSTStructure();   break;
                case 0: back = true;                       break;
                default: System.out.println("  Lua chon khong hop le!");
            }
        }
    }

    static void interactiveRegister() {
        System.out.println("\n-- Dang ky san pham moi --");
        String barcode  = readString("Ma vach: ");
        String name     = readString("Ten san pham: ");
        String category = readString("Danh muc: ");
        double price    = readDouble("Gia (VND): ");
        String strategy = readString("Chien luoc (FIFO/LIFO): ");

        warehouse.registerProduct(new Product(barcode, name, category, price), strategy);
    }

    static void interactiveImport() {
        System.out.println("\n-- Nhap kho --");
        String barcode = readString("Ma vach san pham: ");
        int qty  = readInt("So luong nhap: ");
        int days = readInt("Han su dung (so ngay tu hom nay): ");
        warehouse.importGoods(barcode, qty, LocalDate.now().plusDays(days));
    }

    static void interactiveExport() {
        System.out.println("\n-- Xuat kho --");
        String barcode = readString("Ma vach san pham: ");
        int qty = readInt("So luong xuat: ");
        warehouse.exportGoods(barcode, qty);
    }

    static void printMainMenu() {
        System.out.println("\n+------------------------------+");
        System.out.println("|         MENU CHINH           |");
        System.out.println("+------------------------------+");
        System.out.println("|  1. Demo FIFO  (Queue)       |");
        System.out.println("|  2. Demo LIFO  (Stack)       |");
        System.out.println("|  3. Demo BST   (Ma vach)     |");
        System.out.println("|  4. Che do tuong tac         |");
        System.out.println("|  5. Xem tat ca ton kho       |");
        System.out.println("|  0. Thoat                    |");
        System.out.println("+------------------------------+");
    }

    static void printSeparator(String title) {
        System.out.println("\n==================================================================");
        System.out.println("  " + title);
        System.out.println("==================================================================");
    }

    static void pause() {
        System.out.print("\n[Nhan Enter de tiep tuc...]");
        scanner.nextLine();
    }

    static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Vui long nhap so nguyen hop le!");
            }
        }
    }

    static double readDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Vui long nhap so hop le!");
            }
        }
    }

    static String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
