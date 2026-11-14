package file;

import model.Product;

public class ProductFile extends AbstractFileIO<Product> {

    private String fileName = "products.txt";

    public ProductFile() {
    }

    public ProductFile(String fileName) {
        if (fileName != null && !fileName.trim().isEmpty()) {
            this.fileName = fileName.trim();
        }
    }

    @Override
    protected String getFileName() {
        return fileName;
    }

    @Override
    protected Product dataToObject(String line) {

        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null;
        }

        String barcode = parts[0].trim();
        String priceStr = parts[parts.length - 1].trim();
        String unit = parts[parts.length - 2].trim();
        String category = parts[parts.length - 3].trim();

        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i <= parts.length - 4; i++) {
            if (i > 1) {
                nameBuilder.append(",");
            }
            nameBuilder.append(parts[i]);
        }
        String name = nameBuilder.toString().trim();

        if (barcode.isEmpty() || name.isEmpty() || category.isEmpty() || unit.isEmpty()) {
            return null;
        }

        try {
            double price = Double.parseDouble(priceStr);
            if (price < 0) {
                return null;
            }
            return new Product(barcode, name, category, unit, price);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    protected String toFileString(Product p) {
        return p.getBarcode() + ", " + p.getName() + ", " + p.getCategory() + ", " + p.getUnit() + ", " + p.getPrice();
    }
}
