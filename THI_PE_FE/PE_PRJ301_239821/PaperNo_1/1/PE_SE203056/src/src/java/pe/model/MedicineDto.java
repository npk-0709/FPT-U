/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.model;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class MedicineDto {

    private String medCode;
    private String nameOfMedicine;
    private int quantity;
    private float price;
    private String expiryDate;
    private boolean status;

    public MedicineDto() {
    }
    

    public MedicineDto(String medCode, String nameOfMedicine, int quantity, float price, String expiryDate, boolean status) {
        this.medCode = medCode;
        this.nameOfMedicine = nameOfMedicine;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public String getMedCode() {
        return medCode;
    }

    public void setMedCode(String medCode) {
        this.medCode = medCode;
    }

    public String getNameOfMedicine() {
        return nameOfMedicine;
    }

    public void setNameOfMedicine(String nameOfMedicine) {
        this.nameOfMedicine = nameOfMedicine;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
    
    

}
