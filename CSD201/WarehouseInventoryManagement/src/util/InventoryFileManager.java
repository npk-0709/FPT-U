package util;

import controller.ProductManager;
import datastructure.BatchNode;
import datastructure.BatchQueue;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import model.Batch;
import model.Product;

public final class InventoryFileManager {

    private static final String[] HEADER = {
        "barcode", "name", "category", "unit", "price", "batch_id",
        "quantity", "import_price", "manufacturing_date", "expiry_date", "import_date"
    };

    private InventoryFileManager() {
    }

    public static int importInventory(Path path, ProductManager productManager) throws IOException {
        if (path == null || productManager == null) {
            throw new IllegalArgumentException("File path and product manager are required.");
        }

        Map<String, Product> importedProducts = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IOException("The CSV file is empty.");
            }
            if (headerLine.startsWith("\uFEFF")) {
                headerLine = headerLine.substring(1);
            }
            if (!parseCsvLine(headerLine).equals(java.util.Arrays.asList(HEADER))) {
                throw new IOException("Unexpected CSV header. Use the sample_inventory.csv column order.");
            }

            String line;
            int lineNumber = 1;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }

                List<String> fields;
                try {
                    fields = parseCsvLine(line);
                } catch (IllegalArgumentException ex) {
                    throw rowError(lineNumber, ex.getMessage());
                }
                if (fields.size() != HEADER.length) {
                    throw rowError(lineNumber, "Expected " + HEADER.length + " columns.");
                }

                Product product = createOrGetProduct(fields, importedProducts, lineNumber);
                Batch batch = createBatch(fields, product.getBarcode(), lineNumber);
                if (batch != null) {
                    product.getBatchQueue().enqueue(batch);
                }
            }
        }

        if (importedProducts.isEmpty()) {
            throw new IOException("The CSV file contains no inventory rows.");
        }
        for (Product product : importedProducts.values()) {
            if (productManager.hasProduct(product.getBarcode())) {
                throw new IOException("Barcode already exists: " + product.getBarcode());
            }
        }
        for (Product product : importedProducts.values()) {
            if (!productManager.addProduct(product)) {
                throw new IOException("Could not add product: " + product.getBarcode());
            }
        }
        return importedProducts.size();
    }

    public static int exportInventory(Path path, List<Product> products) throws IOException {
        if (path == null || products == null) {
            throw new IllegalArgumentException("File path and product list are required.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writeRow(writer, java.util.Arrays.asList(HEADER));
            for (Product product : products) {
                BatchQueue queue = product.getBatchQueue();
                BatchNode current = queue.getFront();
                if (current == null) {
                    writeProductRow(writer, product, null);
                } else {
                    while (current != null) {
                        writeProductRow(writer, product, current.getData());
                        current = current.getNext();
                    }
                }
            }
        }
        return products.size();
    }

    private static Product createOrGetProduct(
            List<String> fields, Map<String, Product> products, int lineNumber) throws IOException {
        String barcode = required(fields.get(0), "barcode", lineNumber);
        String name = required(fields.get(1), "name", lineNumber);
        String category = required(fields.get(2), "category", lineNumber);
        String unit = required(fields.get(3), "unit", lineNumber);
        double price = parseNonNegativeDouble(fields.get(4), "price", lineNumber);
        String key = barcode.toLowerCase(Locale.ROOT);
        Product product = products.get(key);

        if (product == null) {
            product = new Product(barcode, name, category, unit, price);
            products.put(key, product);
        } else if (!product.getName().equals(name)
                || !product.getCategory().equals(category)
                || !product.getUnit().equals(unit)
                || Double.compare(product.getPrice(), price) != 0) {
            throw rowError(lineNumber, "Product details differ between rows for barcode " + barcode + ".");
        }
        return product;
    }

    private static Batch createBatch(List<String> fields, String barcode, int lineNumber) throws IOException {
        boolean allEmpty = true;
        for (int i = 5; i < HEADER.length; i++) {
            allEmpty &= fields.get(i).trim().isEmpty();
        }
        if (allEmpty) {
            return null;
        }
        for (int i = 5; i < HEADER.length; i++) {
            if (fields.get(i).trim().isEmpty()) {
                throw rowError(lineNumber, "Batch columns must either all be filled or all be blank.");
            }
        }

        String batchId = fields.get(5).trim();
        int quantity;
        try {
            quantity = Integer.parseInt(fields.get(6).trim());
        } catch (NumberFormatException ex) {
            throw rowError(lineNumber, "Invalid quantity: " + fields.get(6));
        }
        if (quantity <= 0) {
            throw rowError(lineNumber, "Batch quantity must be greater than zero.");
        }
        double importPrice = parseNonNegativeDouble(fields.get(7), "import_price", lineNumber);
        LocalDate manufacturingDate = parseDate(fields.get(8), "manufacturing_date", lineNumber);
        LocalDate expiryDate = parseDate(fields.get(9), "expiry_date", lineNumber);
        LocalDate importDate = parseDate(fields.get(10), "import_date", lineNumber);
        if (expiryDate.isBefore(manufacturingDate)) {
            throw rowError(lineNumber, "expiry_date cannot be before manufacturing_date.");
        }
        return new Batch(batchId, barcode, quantity, importPrice,
                manufacturingDate, expiryDate, importDate);
    }

    private static String required(String value, String field, int lineNumber) throws IOException {
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            throw rowError(lineNumber, field + " cannot be blank.");
        }
        return trimmed;
    }

    private static double parseNonNegativeDouble(String value, String field, int lineNumber) throws IOException {
        try {
            double parsed = Double.parseDouble(value.trim());
            if (Double.isNaN(parsed) || Double.isInfinite(parsed) || parsed < 0) {
                throw new NumberFormatException();
            }
            return parsed;
        } catch (NumberFormatException ex) {
            throw rowError(lineNumber, field + " must be a non-negative number.");
        }
    }

    private static LocalDate parseDate(String value, String field, int lineNumber) throws IOException {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException ex) {
            throw rowError(lineNumber, field + " must use yyyy-MM-dd.");
        }
    }

    private static IOException rowError(int lineNumber, String message) {
        return new IOException("CSV line " + lineNumber + ": " + message);
    }

    private static List<String> parseCsvLine(String line) {
        java.util.ArrayList<String> fields = new java.util.ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean afterQuote = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (inQuotes) {
                if (character == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                        afterQuote = true;
                    }
                } else {
                    field.append(character);
                }
            } else if (character == ',') {
                fields.add(field.toString());
                field.setLength(0);
                afterQuote = false;
            } else if (character == '"' && field.length() == 0 && !afterQuote) {
                inQuotes = true;
            } else if (afterQuote) {
                throw new IllegalArgumentException("Unexpected character after a quoted field.");
            } else {
                field.append(character);
            }
        }
        if (inQuotes) {
            throw new IllegalArgumentException("Unclosed quoted field.");
        }
        fields.add(field.toString());
        return fields;
    }

    private static void writeProductRow(BufferedWriter writer, Product product, Batch batch) throws IOException {
        java.util.ArrayList<String> fields = new java.util.ArrayList<>();
        fields.add(product.getBarcode());
        fields.add(product.getName());
        fields.add(product.getCategory());
        fields.add(product.getUnit());
        fields.add(Double.toString(product.getPrice()));
        if (batch == null) {
            fields.add("");
            fields.add("");
            fields.add("");
            fields.add("");
            fields.add("");
            fields.add("");
        } else {
            fields.add(batch.getBatchId());
            fields.add(Integer.toString(batch.getQuantity()));
            fields.add(Double.toString(batch.getImportPrice()));
            fields.add(batch.getManufacturingDate().toString());
            fields.add(batch.getExpiryDate().toString());
            fields.add(batch.getImportDate().toString());
        }
        writeRow(writer, fields);
    }

    private static void writeRow(BufferedWriter writer, List<String> fields) throws IOException {
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                writer.write(',');
            }
            String field = fields.get(i);
            boolean quote = field.indexOf(',') >= 0 || field.indexOf('"') >= 0
                    || field.indexOf('\n') >= 0 || field.indexOf('\r') >= 0
                    || !field.equals(field.trim());
            if (quote) {
                writer.write('"');
                writer.write(field.replace("\"", "\"\""));
                writer.write('"');
            } else {
                writer.write(field);
            }
        }
        writer.newLine();
    }
}
