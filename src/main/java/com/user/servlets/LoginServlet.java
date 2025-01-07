package com.hospitalmanagement.servlets;

import com.hospitalmanagement.dao.UserDao;
import com.hospitalmanagement.entities.User;
import com.hospitalmanagement.helper.FactoryProvider;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession httpSession = request.getSession();

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Basic validation
        if (email == null || email.isEmpty()) {
            request.setAttribute("message", "Email cannot be empty.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
            dispatcher.forward(request, response);
            return;
        }
        if (password == null || password.isEmpty()) {
            request.setAttribute("message", "Password cannot be empty.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
            dispatcher.forward(request, response);
            return;
        }

        UserDao userDao = new UserDao(FactoryProvider.getFactory());
        User user = userDao.getUserByEmailAndPassword(email, password);

        if (user != null) {
            System.out.println("User Type: " + user.getUserType()); // Debug output

            // Successful login
            httpSession.setAttribute("user", user);
            httpSession.setAttribute("welcomeMessage", "Welcome " + user.getUserName() + "!");

            // Check user type and redirect accordingly
            if (user.getUserType().equalsIgnoreCase("Admin")) {
                response.sendRedirect("AdminDashboard.jsp");
            } else if (user.getUserType().equalsIgnoreCase("Doctor")) {
                response.sendRedirect("DoctorDashboard.jsp");
            } else if (user.getUserType().equalsIgnoreCase("Patient")) {
                response.sendRedirect("PatientDashboard.jsp");
            } else {
                // Handle unexpected user types
                request.setAttribute("message", "Unrecognized user type.");
                RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
                dispatcher.forward(request, response);
            }
        } else {
            // Handle invalid login
            request.setAttribute("message", "Invalid details. Please try again.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Login.jsp");
            dispatcher.forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "LoginServlet for Hospital Management System";
    }
}
