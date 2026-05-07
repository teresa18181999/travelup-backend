package com.travelup.controller;

import com.google.gson.Gson;
import com.travelup.dao.DiaDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/dias")
public class DiaServlet extends HttpServlet {

    private final DiaDAO diaDAO = new DiaDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String idViajeStr = req.getParameter("idViaje");
        if (idViajeStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Falta idViaje\"}");
            return;
        }

        try {
            out.print(gson.toJson(diaDAO.listarDias(Integer.parseInt(idViajeStr))));
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
            while ((line = reader.readLine()) != null) body.append(line);
        }

        Map datos = gson.fromJson(body.toString(), Map.class);
        if (datos == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"JSON invalido\"}");
            return;
        }

        try {
            int id = diaDAO.insertarDia(
                ((Double) datos.get("idViaje")).intValue(),
                (String) datos.get("fecha"),
                (String) datos.get("hotel"),
                (String) datos.get("transporte"),
                (String) datos.get("actividades")
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"exito\":true,\"id\":" + id + "}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }
}
