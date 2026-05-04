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

@WebServlet("/api/viajes/*")
public class ViajeDetalleServlet extends HttpServlet {

    private final ViajeDAO viajeDAO = new ViajeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int id = extraerIdDeUrl(req);
        if (id == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"ID invalido\"}");
            return;
        }

        try {
            Viaje v = viajeDAO.buscarPorId(id);
            if (v == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"exito\":false,\"mensaje\":\"Viaje no encontrado\"}");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{" +
                "\"id\":" + v.getId() + "," +
                "\"nombre\":\"" + v.getNombre() + "\"," +
                "\"destino\":\"" + (v.getDestino() != null ? v.getDestino() : "") + "\"," +
                "\"fechaInicio\":\"" + (v.getFechaInicio() != null ? v.getFechaInicio() : "") + "\"," +
                "\"fechaFin\":\"" + (v.getFechaFin() != null ? v.getFechaFin() : "") + "\"," +
                "\"estado\":\"" + v.getEstado() + "\"," +
                "\"modo\":\"" + v.getModo() + "\"," +
                "\"ciudadOrigen\":\"" + (v.getCiudadOrigen() != null ? v.getCiudadOrigen() : "") + "\"," +
                "\"idCreador\":" + v.getIdCreador() +
            "}");

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int id = extraerIdDeUrl(req);
        if (id == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"ID invalido\"}");
            return;
        }

        try {
            boolean eliminado = viajeDAO.eliminarViaje(id);
            if (eliminado) {
                resp.setStatus(HttpServletResponse.SC_OK);
                out.print("{\"exito\":true,\"mensaje\":\"Viaje eliminado\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"exito\":false,\"mensaje\":\"Viaje no encontrado\"}");
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int id = extraerIdDeUrl(req);
        if (id == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"ID invalido\"}");
            return;
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                body.append(line);
            }
        }

        String json = body.toString().trim();

        try {
            Viaje v = viajeDAO.buscarPorId(id);
            if (v == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"exito\":false,\"mensaje\":\"Viaje no encontrado\"}");
                return;
            }

            String nombre = extraerValorJson(json, "nombre");
            String destino = extraerValorJson(json, "destino");
            String fechaInicio = extraerValorJson(json, "fechaInicio");
            String fechaFin = extraerValorJson(json, "fechaFin");
            String estado = extraerValorJson(json, "estado");

            if (nombre != null) v.setNombre(nombre);
            if (destino != null) v.setDestino(destino);
            if (fechaInicio != null) v.setFechaInicio(fechaInicio);
            if (fechaFin != null) v.setFechaFin(fechaFin);
            if (estado != null) v.setEstado(estado);

            viajeDAO.actualizarViaje(v);

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"mensaje\":\"Viaje actualizado\"}");

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    private int extraerIdDeUrl(HttpServletRequest req) {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) return -1;
            return Integer.parseInt(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return -1;
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