package com.travelup.dao;

import com.travelup.util.ConexionDB;
import java.sql.*;
import java.util.*;

public class DiaDAO {

    public List<Map<String, Object>> listarDias(int idViaje) throws SQLException {
        String sql = "SELECT id, fecha, hotel, transporte, actividades_json FROM dias_viaje WHERE id_viaje = ? ORDER BY fecha ASC";
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> dia = new HashMap<>();
                    dia.put("id", rs.getInt("id"));
                    dia.put("fecha", rs.getString("fecha"));
                    dia.put("hotel", rs.getString("hotel"));
                    dia.put("transporte", rs.getString("transporte"));
                    dia.put("actividades", rs.getString("actividades_json"));
                    lista.add(dia);
                }
            }
        }
        return lista;
    }

    public int insertarDia(int idViaje, String fecha, String hotel, String transporte, String actividadesJson) throws SQLException {
        String sql = "INSERT INTO dias_viaje (id_viaje, fecha, hotel, transporte, actividades_json) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, idViaje);
            ps.setString(2, fecha);
            ps.setString(3, hotel);
            ps.setString(4, transporte);
            ps.setString(5, actividadesJson);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }

    public boolean actualizarDia(int id, String hotel, String transporte, String actividadesJson) throws SQLException {
        String sql = "UPDATE dias_viaje SET hotel=?, transporte=?, actividades_json=? WHERE id=?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hotel);
            ps.setString(2, transporte);
            ps.setString(3, actividadesJson);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        }
    }
}