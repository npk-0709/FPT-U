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
public class UserDto {

    private String userID;
    private String fullName;
    private String password;
    private int gender;
    private String roleID;
    private String birthDay;
    private int active;

    public UserDto() {
    }

    public UserDto(String userID, String fullName, String password, int gender, String roleID, String birthDay, int active) {
        this.userID = userID;
        this.fullName = fullName;
        this.password = password;
        this.gender = gender;
        this.roleID = roleID;
        this.birthDay = birthDay;
        this.active = active;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }



}
