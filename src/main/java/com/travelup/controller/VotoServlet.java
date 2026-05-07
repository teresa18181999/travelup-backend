package com.travelup.controller;

import com.travelup.dao.VotoDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/votos")
public class VotoServlet extends HttpServlet {

    private final VotoDAO votoDAO = new VotoDAO();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }

        String json = body.toString().trim();
        String idViajeStr = extraerValorJson(json, "idViaje");
        String idUsuarioStr = extraerValorJson(json, "idUsuario");
        String tipo = extraerValorJson(json, "tipo");
        String opcion = extraerValorJson(json, "opcion");
        String voto = extraerValorJson(json, "voto");

        if (idViajeStr == null || idUsuarioStr == null || tipo == null || opcion == null || voto == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Faltan campos obligatorios\"}");
            return;
        }

        try {
            votoDAO.guardarVoto(Integer.parseInt(idViajeStr), Integer.parseInt(idUsuarioStr), tipo, opcion, voto);

            List<String> matches = votoDAO.obtenerMatches(Integer.parseInt(idViajeStr), tipo);
            StringBuilder matchesJson = new StringBuilder("[");
            for (int i = 0; i < matches.size(); i++) {
                matchesJson.append("\"").append(matches.get(i)).append("\"");
                if (i < matches.size() - 1) matchesJson.append(",");
            }
            matchesJson.append("]");

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"matches\":" + matchesJson + "}");

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