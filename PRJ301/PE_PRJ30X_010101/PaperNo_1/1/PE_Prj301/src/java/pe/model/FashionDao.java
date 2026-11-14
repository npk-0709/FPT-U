/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import pe.utils.DbUtils;

/**
 *
 * @author Computing Fundamental - HCM Campus
 */
public class FashionDao {

    public List<FashionDto> searchByName(String keyword) {
        List<FashionDto> list = new ArrayList<>();
        String sql = "SELECT id, name, description, price, size, status FROM tblFashion WHERE name LIKE ?";
        try (Connection conn = DbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FashionDto f = new FashionDto(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("price"),
                        rs.getString("size"),
                        rs.getBoolean("status")
                    );
                    list.add(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public FashionDto getById(String id) {
        FashionDto f = null;
        String sql = "SELECT id, name, description, price, size, status FROM tblFashion WHERE id = ?";
        try (Connection conn = DbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    f = new FashionDto(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getFloat("price"),
                        rs.getString("size"),
                        rs.getBoolean("status")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }

    public boolean update(FashionDto fashion) {
        String sql = "UPDATE tblFashion SET name = ?, description = ?, price = ?, size = ?, status = ? WHERE id = ?";
        try (Connection conn = DbUtils.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fashion.getName());
            ps.setString(2, fashion.getDescription());
            ps.setFloat(3, fashion.getPrice());
            ps.setString(4, fashion.getSize());
            ps.setBoolean(5, fashion.isStatus());
            ps.setString(6, fashion.getId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
