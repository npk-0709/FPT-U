
import java.time.LocalDate;

public class ProductBatch {
    private String batchId;
    private int quantity;
    private LocalDate importDate;
    private LocalDate expiryDate;

    public ProductBatch(String batchId, int quantity, LocalDate importDate, LocalDate expiryDate) {
        this.batchId = batchId;
        this.quantity = quantity;
        this.importDate = importDate;
        this.expiryDate = expiryDate;
    }

    public String getBatchId()           { return batchId; }
    public int getQuantity()             { return quantity; }
    public void setQuantity(int q)       { this.quantity = q; }
    public LocalDate getImportDate()     { return importDate; }
    public LocalDate getExpiryDate()     { return expiryDate; }

    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }

    @Override
    public String toString() {
        return String.format("[Lo: %s | SL: %4d | Nhap: %s | HSD: %s%s]",
                batchId, quantity, importDate, expiryDate,
                isExpired() ? " <<HET HAN!>>" : "");
    }
}
