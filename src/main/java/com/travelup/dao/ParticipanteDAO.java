package com.travelup.dao;

import com.travelup.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParticipanteDAO {

    public boolean añadirParticipante(int idViaje, int idUsuario) throws SQLException {
        String sql = "INSERT INTO viaje_participantes (id_viaje, id_usuario) VALUES (?, ?)";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }

    public List<Integer> listarParticipantes(int idViaje) throws SQLException {
        String sql = "SELECT id_usuario FROM viaje_participantes WHERE id_viaje = ?";
        List<Integer> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(rs.getInt("id_usuario"));
                }
            }
        }
        return lista;
    }

    public boolean eliminarParticipante(int idViaje, int idUsuario) throws SQLException {
        String sql = "DELETE FROM viaje_participantes WHERE id_viaje = ? AND id_usuario = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
}
