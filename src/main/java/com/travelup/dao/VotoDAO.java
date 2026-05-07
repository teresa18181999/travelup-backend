package com.travelup.dao;

import com.travelup.util.ConexionDB;
import java.sql.*;
import java.util.*;

public class VotoDAO {

    public boolean guardarVoto(int idViaje, int idUsuario, String tipo, String opcion, String voto) throws SQLException {
        String sql = "INSERT INTO votos (id_viaje, id_usuario, tipo, opcion, voto) VALUES (?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE voto = ?";

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);
            ps.setInt(2, idUsuario);
            ps.setString(3, tipo);
            ps.setString(4, opcion);
            ps.setString(5, voto);
            ps.setString(6, voto);
            return ps.executeUpdate() > 0;
        }
    }

    public List<String> obtenerMatches(int idViaje, String tipo) throws SQLException {
        String sql = "SELECT opcion FROM votos WHERE id_viaje = ? AND tipo = ? AND voto = 'like' GROUP BY opcion HAVING COUNT(*) = (SELECT COUNT(*) FROM viaje_participantes WHERE id_viaje = ?)";
        List<String> matches = new ArrayList<>();

        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idViaje);
            ps.setString(2, tipo);
            ps.setInt(3, idViaje);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) matches.add(rs.getString("opcion"));
            }
        }
        return matches;
    }
}