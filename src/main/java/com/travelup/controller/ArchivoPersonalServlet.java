package com.travelup.controller;

import com.google.gson.Gson;
import com.travelup.dao.ArchivoPersonalDAO;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

@WebServlet("/api/archivos")
public class ArchivoPersonalServlet extends HttpServlet {

    private final ArchivoPersonalDAO archivoDAO = new ArchivoPersonalDAO();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String idUsuarioStr = req.getParameter("idUsuario");
        String idDiaStr = req.getParameter("idDia");

        if (idUsuarioStr == null || idDiaStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Faltan parametros\"}");
            return;
        }

        try {
            out.print(gson.toJson(archivoDAO.listarArchivos(
                Integer.parseInt(idUsuarioStr),
                Integer.parseInt(idDiaStr)
            )));
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
            archivoDAO.insertarArchivo(
                ((Double) datos.get("idUsuario")).intValue(),
                ((Double) datos.get("idDia")).intValue(),
                (String) datos.get("nombre"),
                (String) datos.get("ruta")
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            out.print("{\"exito\":true,\"mensaje\":\"Archivo guardado\"}");
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

        String idStr = req.getParameter("id");
        if (idStr == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.print("{\"exito\":false,\"mensaje\":\"Falta id\"}");
            return;
        }

        try {
            archivoDAO.eliminarArchivo(Integer.parseInt(idStr));
            resp.setStatus(HttpServletResponse.SC_OK);
            out.print("{\"exito\":true,\"mensaje\":\"Archivo eliminado\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print("{\"exito\":false,\"mensaje\":\"Error interno del servidor\"}");
        }
    }
}