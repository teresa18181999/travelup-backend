package com.travelup.controller;

import com.travelup.dao.UsuarioDAO;
import com.travelup.model.Usuario;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

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
        String telefono = extraerValorJson(json, "telefono");

        if (telefono == null || telefono.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"El campo telefono es obligatorio\"}");
            return;
        }

        try {
            Usuario usuario = usuarioDAO.buscarPorTelefono(telefono);

            if (usuario == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print("{\"exito\":false,\"mensaje\":\"Telefono no registrado\"}");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"mensaje\":\"Usuario encontrado\",\"id\":" + usuario.getId() + ",\"nombre\":\"" + usuario.getNombre() + "\"}");

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