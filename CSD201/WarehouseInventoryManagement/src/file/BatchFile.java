package file;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import model.Batch;

public class BatchFile extends AbstractFileIO<Batch> {

    private String fileName = "batches.txt";

    public BatchFile() {
    }

    public BatchFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            this.fileName = fileName.trim();
        }
    }

    @Override
    protected String getFileName() {
        return fileName;
    }

    @Override
    protected Batch dataToObject(String line) {

        String[] parts = line.split(",", 7);
        if (parts.length < 7) {
            return null;
        }

        String batchId = parts[0].trim();
        String barcode = parts[1].trim();
        String qtyStr = parts[2].trim();
        String priceStr = parts[3].trim();
        String mfgStr = parts[4].trim();
        String expStr = parts[5].trim();
        String impStr = parts[6].trim();

        if (batchId.isEmpty() || barcode.isEmpty()) {
            return null;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            if (quantity <= 0) {
                return null;
            }
            double importPrice = Double.parseDouble(priceStr);
            if (importPrice < 0) {
                return null;
            }
            LocalDate mfgDate = LocalDate.parse(mfgStr);
            LocalDate expDate = LocalDate.parse(expStr);
            LocalDate impDate = LocalDate.parse(impStr);

            if (expDate.isBefore(mfgDate)) {
                return null;
            }

            return new Batch(batchId, barcode, quantity, importPrice, mfgDate, expDate, impDate);
        } catch (NumberFormatException | DateTimeParseException e) {
            return null;
        }
    }

    @Override
    protected String toFileString(Batch b) {
        return b.getBatchId() + ", "
                + b.getBarcode() + ", "
                + b.getQuantity() + ", "
                + b.getImportPrice() + ", "
                + b.getManufacturingDate() + ", "
                + b.getExpiryDate() + ", "
                + b.getImportDate();
    }
}
