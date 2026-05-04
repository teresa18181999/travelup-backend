package com.travelup.controller;

import com.travelup.dao.ViajeDAO;
import com.travelup.model.Viaje;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/viajes")
public class ViajeServlet extends HttpServlet {

    private final ViajeDAO viajeDAO = new ViajeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        String idUsuarioStr = req.getParameter("idUsuario");
        String estado = req.getParameter("estado");

        if (idUsuarioStr == null || estado == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Faltan parametros idUsuario o estado\"}");
            return;
        }

        try {
            int idUsuario = Integer.parseInt(idUsuarioStr);
            List<Viaje> viajes = viajeDAO.listarPorUsuario(idUsuario, estado);

            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < viajes.size(); i++) {
                Viaje v = viajes.get(i);
                json.append("{")
                    .append("\"id\":").append(v.getId()).append(",")
                    .append("\"nombre\":\"").append(v.getNombre()).append("\",")
                    .append("\"destino\":\"").append(v.getDestino() != null ? v.getDestino() : "").append("\",")
                    .append("\"fechaInicio\":\"").append(v.getFechaInicio() != null ? v.getFechaInicio() : "").append("\",")
                    .append("\"fechaFin\":\"").append(v.getFechaFin() != null ? v.getFechaFin() : "").append("\",")
                    .append("\"estado\":\"").append(v.getEstado()).append("\",")
                    .append("\"modo\":\"").append(v.getModo()).append("\"")
                    .append("}");
                if (i < viajes.size() - 1) json.append(",");
            }
            json.append("]");

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print(json.toString());

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        PrintWriter out = resp.getWriter();

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        String json = body.toString().trim();
        String nombre = extraerValorJson(json, "nombre");
        String modo = extraerValorJson(json, "modo");
        String idCreadorStr = extraerValorJson(json, "idCreador");

        if (nombre == null || modo == null || idCreadorStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Faltan campos obligatorios\"}");
            return;
        }

        try {
            Viaje v = new Viaje();
            v.setNombre(nombre);
            v.setModo(modo);
            v.setIdCreador(Integer.parseInt(idCreadorStr));
            v.setDestino(extraerValorJson(json, "destino"));
            v.setFechaInicio(extraerValorJson(json, "fechaInicio"));
            v.setFechaFin(extraerValorJson(json, "fechaFin"));
            v.setCiudadOrigen(extraerValorJson(json, "ciudadOrigen"));

            Viaje creado = viajeDAO.insertarViaje(v);

            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"exito\":true,\"mensaje\":\"Viaje creado\",\"id\":" + creado.getId() + "}");

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    private String extraerValorJson(String json, String clave) {
        String patron = "\"" + clave + "\"";
        int idx = json.indexOf(patron);
        if (idx == -1) return null;

        int inicio = json.indexOf('"', idx + patron.length() + 1);
        if (inicio == -1) return null;

        int fin = json.indexOf('"', inicio + 1);
        if (fin == -1) return null;

        return json.substring(inicio + 1, fin);
    }
}