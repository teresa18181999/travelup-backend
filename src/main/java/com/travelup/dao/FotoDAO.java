package com.travelup.dao;

import com.travelup.util.ConexionDB;
import java.sql.*;
import java.util.*;

public class FotoDAO {

    public boolean insertarFoto(int idDia, String rutaArchivo) throws SQLException {
        String sql = "INSERT INTO fotos_dia (id_dia, ruta_archivo) VALUES (?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDia);
            ps.setString(2, rutaArchivo);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> listarFotos(int idDia) throws SQLException {
        String sql = "SELECT id, ruta_archivo FROM fotos_dia WHERE id_dia = ?";
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDia);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> foto = new HashMap<>();
                    foto.put("id", rs.getInt("id"));
                    foto.put("ruta", rs.getString("ruta_archivo"));
                    lista.add(foto);
                }
            }
        }
        return lista;
    }

    public boolean eliminarFoto(int id) throws SQLException {
        String sql = "DELETE FROM fotos_dia WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
