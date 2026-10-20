package util;

/**
 * Result codes for warehouse operations.
 * 
 * @author Group4_SE2023
 */
public enum ResultCode {
    OK("Operation completed successfully!"), 
    NOT_FOUND("Product not found!"), 
    INVALID_INPUT("Invalid input data!"), 
    INFSUFFICIENT_STOCK("Insufficient stock to issue!"), 
    INSUFFICIENT_STOCK("Insufficient stock to issue!"), 
    DUPLICATE("Identifier already exists in the system!");

    private final String message;

    ResultCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return this == OK;
    }

    @Override
    public String toString() {
        return name() + ": " + message;
    }
}
