package com.travelup.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/api/verificar")
public class VerificarCodigoServlet extends HttpServlet {

    private static final String CODIGO_VALIDO = "123456";

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
        String codigo = extraerValorJson(json, "codigo");

        if (codigo == null || codigo.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"El codigo es obligatorio\"}");
            return;
        }

        String codigoLimpio = codigo.replace("-", "");

        if (codigoLimpio.equals(CODIGO_VALIDO)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"mensaje\":\"Codigo correcto\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            out.print("{\"exito\":false,\"mensaje\":\"Codigo incorrecto\"}");
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