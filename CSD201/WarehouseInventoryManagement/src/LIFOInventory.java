package warehouse;

import java.util.Stack;

public class LIFOInventory {
    private Stack<ProductBatch> stack;
    private String productBarcode;
    private int totalQuantity;

    public LIFOInventory(String productBarcode) {
        this.productBarcode = productBarcode;
        this.stack = new Stack<>();
        this.totalQuantity = 0;
    }

    public void importBatch(ProductBatch batch) {
        stack.push(batch);
        totalQuantity += batch.getQuantity();
        System.out.printf("  [LIFO-Push]  Lo %s: +%d sp  |  Tong ton: %d\n",
                batch.getBatchId(), batch.getQuantity(), totalQuantity);
    }

    public int exportGoods(int requestedQty) {
        if (stack.isEmpty()) {
            System.out.println("  [LIFO] Kho trong! Khong the xuat hang.");
            return 0;
        }

        int remaining = requestedQty;
        int exported  = 0;
        System.out.printf("  [LIFO] Bat dau xuat %d san pham...\n", requestedQty);

        while (remaining > 0 && !stack.isEmpty()) {
            ProductBatch top = stack.peek();

            if (top.isExpired()) {
                System.out.printf("    [!] Lo %s da HET HAN, loai bo!\n", top.getBatchId());
                totalQuantity -= top.getQuantity();
                stack.pop();
                continue;
            }

            int available = top.getQuantity();
            if (available <= remaining) {
                exported  += available;
                remaining -= available;
                totalQuantity -= available;
                System.out.printf("    [Pop] Xuat het lo %s: %d sp (Nhap: %s)\n",
                        top.getBatchId(), available, top.getImportDate());
                stack.pop();
            } else {
                top.setQuantity(available - remaining);
                exported  += remaining;
                totalQuantity -= remaining;
                System.out.printf("    [Partial] Xuat 1 phan lo %s: %d sp (con lai: %d)\n",
                        top.getBatchId(), remaining, top.getQuantity());
                remaining = 0;
            }
        }

        System.out.printf("  [LIFO] Da xuat: %d | Con thieu: %d | Ton kho: %d\n",
                exported, remaining, totalQuantity);
        return exported;
    }

    public void display() {
        System.out.printf("  [LIFO] Ton kho '%s' (%d sp):\n", productBarcode, totalQuantity);
        if (stack.isEmpty()) {
            System.out.println("    (Kho trong)");
            return;
        }
        for (int i = stack.size() - 1; i >= 0; i--) {
            String tag = (i == stack.size() - 1) ? " <- DINH (xuat tiep theo)" : "";
            System.out.printf("    %d. %s%s\n", stack.size() - i, stack.get(i), tag);
        }
    }

    public int getTotalQuantity()  { return totalQuantity; }
    public String getProductBarcode() { return productBarcode; }
    public boolean isEmpty()       { return stack.isEmpty(); }
}
