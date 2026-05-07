package com.travelup.dao;

import com.travelup.model.Usuario;
import com.travelup.util.ConexionDB;

import java.sql.*;

public class UsuarioDAO {

    public Usuario insertarUsuario(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (nombre, telefono, email) VALUES (?, ?, ?)";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getTelefono());
            ps.setString(3, u.getEmail());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) u.setId(rs.getInt(1));
            }
        }
        return u;
    }

    public Usuario buscarPorTelefono(String telefono) throws SQLException {
        String sql = "SELECT id, nombre, telefono, email FROM usuarios WHERE telefono = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, telefono);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Usuario(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("email")
                    );
                }
            }
        }
        return null;
    }

    public boolean actualizarEmail(int id, String email) throws SQLException {
        String sql = "UPDATE usuarios SET email = ? WHERE id = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        }
    }
}