/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import pe.model.UserDto;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class UserDao {

    public UserDto login(String userID, String password) {
        Connection conn = null;
        PreparedStatement p = null;
        ResultSet rs = null;
        UserDto user = null;
        try {
            conn = pe.utils.DbUtils.getConnection();
            p = conn.prepareStatement("select * from [dbo].[User] where userID=? and password=?");
            p.setString(1, userID);
            p.setString(2,  password);
            rs = p.executeQuery();
            if (rs.next()) {
                user = new UserDto(
                        rs.getString("userID"),
                        rs.getString("fullName"),
                        rs.getString("password"),
                        rs.getInt("gender"),
                        rs.getString("roleID"),
                        rs.getString("birthDay"),
                        rs.getInt("active")
                );

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

}
