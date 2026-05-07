package com.travelup.dao;

import com.travelup.util.ConexionDB;
import java.sql.*;
import java.util.*;

public class ArchivoPersonalDAO {

    public boolean insertarArchivo(int idUsuario, int idDia, String nombre, String rutaPdf) throws SQLException {
        String sql = "INSERT INTO archivos_personales (id_usuario, id_dia, nombre, ruta_pdf) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idDia);
            ps.setString(3, nombre);
            ps.setString(4, rutaPdf);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Map<String, Object>> listarArchivos(int idUsuario, int idDia) throws SQLException {
        String sql = "SELECT id, nombre, ruta_pdf FROM archivos_personales WHERE id_usuario = ? AND id_dia = ?";
        List<Map<String, Object>> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idDia);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> archivo = new HashMap<>();
                    archivo.put("id", rs.getInt("id"));
                    archivo.put("nombre", rs.getString("nombre"));
                    archivo.put("ruta", rs.getString("ruta_pdf"));
                    lista.add(archivo);
                }
            }
        }
        return lista;
    }

    public boolean eliminarArchivo(int id) throws SQLException {
        String sql = "DELETE FROM archivos_personales WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}