package file;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import model.StockTransaction;
import model.TransactionType;

public class TransactionFile extends AbstractFileIO<StockTransaction> {

    private String fileName = "transactions.txt";

    public TransactionFile() {
    }

    public TransactionFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            this.fileName = fileName.trim();
        }
    }

    @Override
    protected String getFileName() {
        return fileName;
    }

    @Override
    protected StockTransaction dataToObject(String line) {

        String[] parts = line.split(",", 7);
        if (parts.length < 6) {
            return null;
        }

        String txId = parts[0].trim();
        String typeStr = parts[1].trim();
        String barcode = parts[2].trim();
        String batchId = parts[3].trim();
        String qtyStr = parts[4].trim();
        String timeStr = parts[5].trim();
        String note = (parts.length >= 7) ? parts[6].trim() : "";

        if (txId.isEmpty() || barcode.isEmpty() || batchId.isEmpty()) {
            return null;
        }

        try {
            TransactionType type = TransactionType.valueOf(typeStr);
            int quantity = Integer.parseInt(qtyStr);
            LocalDateTime timestamp = LocalDateTime.parse(timeStr);
            return new StockTransaction(txId, type, barcode, batchId, quantity, timestamp, note);
        } catch (IllegalArgumentException | DateTimeParseException e) {
            return null;
        }
    }

    @Override
    protected String toFileString(StockTransaction t) {
        return t.getTransactionId() + ", "
                + t.getType().name() + ", "
                + t.getBarcode() + ", "
                + t.getBatchId() + ", "
                + t.getQuantity() + ", "
                + t.getTimestamp() + ", "
                + (t.getNote() != null ? t.getNote() : "");
    }
}
