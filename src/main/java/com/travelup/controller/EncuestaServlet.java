package com.travelup.controller;

import com.travelup.dao.EncuestaDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/encuesta")
public class EncuestaServlet extends HttpServlet {

    private final EncuestaDAO encuestaDAO = new EncuestaDAO();

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
        int jsonStart = json.indexOf("\"respuestas\"");
        
        if (idViajeStr == null || idUsuarioStr == null || jsonStart == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Faltan campos obligatorios\"}");
            return;
        }

        try {
            int idViaje = Integer.parseInt(idViajeStr);
            int idUsuario = Integer.parseInt(idUsuarioStr);
            boolean todos = encuestaDAO.todosRespondieron(idViaje);

            encuestaDAO.guardarEncuesta(idViaje, idUsuario, json);

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"todosRespondieron\":" + todos + "}");

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