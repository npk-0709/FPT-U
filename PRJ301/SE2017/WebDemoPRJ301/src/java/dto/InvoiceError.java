/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dto;

/**
 *
 * @author ADMIN
 */
public class InvoiceError {

    private String invIDErr;
    private String invDateErr;
    private String customerErr;

    public InvoiceError() {
        this.invIDErr = "";
        this.invDateErr = "";
        this.customerErr = "";
    }

    public InvoiceError(String invIDErr, String invDateErr, String customerErr) {
        this.invIDErr = invIDErr;
        this.invDateErr = invDateErr;
        this.customerErr = customerErr;
    }

    public String getInvIDErr() {
        return invIDErr;
    }

    public void setInvIDErr(String invIDErr) {
        this.invIDErr = invIDErr;
    }

    public String getInvDateErr() {
        return invDateErr;
    }

    public void setInvDateErr(String invDateErr) {
        this.invDateErr = invDateErr;
    }

    public String getCustomerErr() {
        return customerErr;
    }

    public void setCustomerErr(String customerErr) {
        this.customerErr = customerErr;
    }
}
