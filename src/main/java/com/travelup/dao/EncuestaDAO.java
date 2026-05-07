package com.travelup.dao;

import com.travelup.util.ConexionDB;
import java.sql.*;

public class EncuestaDAO {

    public boolean guardarEncuesta(int idViaje, int idUsuario, String respuestasJson) throws SQLException {
        String sql = "INSERT INTO encuestas (id_viaje, id_usuario, respuestas_json) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE respuestas_json = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);
            ps.setInt(2, idUsuario);
            ps.setString(3, respuestasJson);
            ps.setString(4, respuestasJson);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean todosRespondieron(int idViaje) throws SQLException {
        String sqlP = "SELECT COUNT(*) FROM viaje_participantes WHERE id_viaje = ?";
        String sqlE = "SELECT COUNT(*) FROM encuestas WHERE id_viaje = ?";

        try (Connection conn = ConexionDB.getConnection()) {
            int participantes = 0, encuestas = 0;

            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                ps.setInt(1, idViaje);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) participantes = rs.getInt(1);
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(sqlE)) {
                ps.setInt(1, idViaje);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) encuestas = rs.getInt(1);
                }
            }

            return participantes > 0 && participantes == encuestas;
        }
    }
}