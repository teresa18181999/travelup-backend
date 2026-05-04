package com.travelup.dao;

import com.travelup.model.Viaje;
import com.travelup.util.ConexionDB;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ViajeDAO {

    public Viaje insertarViaje(Viaje v) throws SQLException {
        String sql = "INSERT INTO viajes (nombre, destino, fecha_inicio, fecha_fin, estado, modo, ciudad_origen, id_creador) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, v.getNombre());
            ps.setString(2, v.getDestino());
            ps.setString(3, v.getFechaInicio());
            ps.setString(4, v.getFechaFin());
            ps.setString(5, "en_curso");
            ps.setString(6, v.getModo());
            ps.setString(7, v.getCiudadOrigen());
            ps.setInt(8, v.getIdCreador());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    v.setId(rs.getInt(1));
                }
            }
        }
        return v;
    }

    public List<Viaje> listarPorUsuario(int idUsuario, String estado) throws SQLException {
        String sql = "SELECT v.* FROM viajes v JOIN viaje_participantes vp ON v.id = vp.id_viaje WHERE vp.id_usuario = ? AND v.estado = ?";
        List<Viaje> lista = new ArrayList<>();

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, estado);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    public Viaje buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM viajes WHERE id = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    public boolean eliminarViaje(int id) throws SQLException {
        String sql = "DELETE FROM viajes WHERE id = ?";

        try (Connection conn = ConexionDB.obtenerConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    
    public boolean actualizarViaje(Viaje v) throws SQLException {
    String sql = "UPDATE viajes SET nombre=?, destino=?, fecha_inicio=?, fecha_fin=?, estado=? WHERE id=?";

    try (Connection conn = ConexionDB.obtenerConexion();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, v.getNombre());
        ps.setString(2, v.getDestino());
        ps.setString(3, v.getFechaInicio());
        ps.setString(4, v.getFechaFin());
        ps.setString(5, v.getEstado());
        ps.setInt(6, v.getId());

        return ps.executeUpdate() > 0;
    }
}

    private Viaje mapear(ResultSet rs) throws SQLException {
        Viaje v = new Viaje();
        v.setId(rs.getInt("id"));
        v.setNombre(rs.getString("nombre"));
        v.setDestino(rs.getString("destino"));
        v.setFechaInicio(rs.getString("fecha_inicio"));
        v.setFechaFin(rs.getString("fecha_fin"));
        v.setFotoPortada(rs.getString("foto_portada"));
        v.setEstado(rs.getString("estado"));
        v.setModo(rs.getString("modo"));
        v.setCiudadOrigen(rs.getString("ciudad_origen"));
        v.setIdCreador(rs.getInt("id_creador"));
        return v;
    }
}