
import java.util.LinkedList;
import java.util.Queue;

public class FIFOInventory {
    private Queue<ProductBatch> queue;
    private String productBarcode;
    private int totalQuantity;

    public FIFOInventory(String productBarcode) {
        this.productBarcode = productBarcode;
        this.queue = new LinkedList<>();
        this.totalQuantity = 0;
    }

    public void importBatch(ProductBatch batch) {
        queue.offer(batch);
        totalQuantity += batch.getQuantity();
        System.out.printf("  [FIFO-Enqueue] Lo %s: +%d sp  |  Tong ton: %d\n",
                batch.getBatchId(), batch.getQuantity(), totalQuantity);
    }

    public int exportGoods(int requestedQty) {
        if (queue.isEmpty()) {
            System.out.println("  [FIFO] Kho trong! Khong the xuat hang.");
            return 0;
        }

        int remaining = requestedQty;
        int exported  = 0;
        System.out.printf("  [FIFO] Bat dau xuat %d san pham...\n", requestedQty);

        while (remaining > 0 && !queue.isEmpty()) {
            ProductBatch head = queue.peek();

            if (head.isExpired()) {
                System.out.printf("    [!] Lo %s da HET HAN, loai bo!\n", head.getBatchId());
                totalQuantity -= head.getQuantity();
                queue.poll();
                continue;
            }

            int available = head.getQuantity();
            if (available <= remaining) {
                exported  += available;
                remaining -= available;
                totalQuantity -= available;
                System.out.printf("    [Dequeue] Xuat het lo %s: %d sp (HSD: %s)\n",
                        head.getBatchId(), available, head.getExpiryDate());
                queue.poll();
            } else {
                head.setQuantity(available - remaining);
                exported  += remaining;
                totalQuantity -= remaining;
                System.out.printf("    [Partial] Xuat 1 phan lo %s: %d sp (con lai: %d)\n",
                        head.getBatchId(), remaining, head.getQuantity());
                remaining = 0;
            }
        }

        System.out.printf("  [FIFO] Da xuat: %d | Con thieu: %d | Ton kho: %d\n",
                exported, remaining, totalQuantity);
        return exported;
    }

    public void display() {
        System.out.printf("  [FIFO] Ton kho '%s' (%d sp):\n", productBarcode, totalQuantity);
        if (queue.isEmpty()) {
            System.out.println("    (Kho trong)");
            return;
        }
        int i = 1;
        for (ProductBatch b : queue) {
            System.out.printf("    %d. %s\n", i++, b);
        }
    }

    public int getTotalQuantity()  { return totalQuantity; }
    public String getProductBarcode() { return productBarcode; }
    public boolean isEmpty()       { return queue.isEmpty(); }
}
