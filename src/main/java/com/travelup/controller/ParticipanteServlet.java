package com.travelup.controller;

import com.travelup.dao.ParticipanteDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/viajes/*/participantes")
public class ParticipanteServlet extends HttpServlet {

    private final ParticipanteDAO participanteDAO = new ParticipanteDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        int idViaje = extraerIdViaje(req);
        if (idViaje == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"ID de viaje invalido\"}");
            return;
        }

        try {
            List<Integer> participantes = participanteDAO.listarParticipantes(idViaje);
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < participantes.size(); i++) {
                json.append(participantes.get(i));
                if (i < participantes.size() - 1) json.append(",");
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

        int idViaje = extraerIdViaje(req);
        if (idViaje == -1) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"ID de viaje invalido\"}");
            return;
        }

        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        }

        String idUsuarioStr = extraerValorJson(body.toString(), "idUsuario");
        if (idUsuarioStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Falta idUsuario\"}");
            return;
        }

        try {
            participanteDAO.añadirParticipante(idViaje, Integer.parseInt(idUsuarioStr));
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"exito\":true,\"mensaje\":\"Participante añadido\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }

    private int extraerIdViaje(HttpServletRequest req) {
        try {
            String path = req.getRequestURI();
            String[] partes = path.split("/");
            for (int i = 0; i < partes.length; i++) {
                if (partes[i].equals("viajes") && i + 1 < partes.length) {
                    return Integer.parseInt(partes[i + 1]);
                }
            }
            return -1;
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