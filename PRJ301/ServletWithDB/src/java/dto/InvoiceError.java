package dto;

/**
 *
 * @author Khuong
 */
public class InvoiceError {

    private String invIDError;
    private String invDateError;
    private String customerError;
    private String messageError;

    public InvoiceError() {
        this.invIDError = "";
        this.invDateError = "";
        this.customerError = "";
        this.messageError = "";
    }

    public InvoiceError(String invIDError, String invDateError, String customerError, String messageError) {
        this.invIDError = invIDError;
        this.invDateError = invDateError;
        this.customerError = customerError;
        this.messageError = messageError;
    }

    public String getInvIDError() {
        return invIDError;
    }

    public void setInvIDError(String invIDError) {
        this.invIDError = invIDError;
    }

    public String getInvIdError() {
        return invIDError;
    }

    public void setInvIdError(String invIdError) {
        this.invIDError = invIdError;
    }

    public String getInvDateError() {
        return invDateError;
    }

    public void setInvDateError(String invDateError) {
        this.invDateError = invDateError;
    }

    public String getCustomerError() {
        return customerError;
    }

    public void setCustomerError(String customerError) {
        this.customerError = customerError;
    }

    public String getMessageError() {
        return messageError;
    }

    public void setMessageError(String messageError) {
        this.messageError = messageError;
    }

    public boolean hasError() {
        return (invIDError != null && !invIDError.trim().isEmpty())
                || (invDateError != null && !invDateError.trim().isEmpty())
                || (customerError != null && !customerError.trim().isEmpty())
                || (messageError != null && !messageError.trim().isEmpty());
    }
}
