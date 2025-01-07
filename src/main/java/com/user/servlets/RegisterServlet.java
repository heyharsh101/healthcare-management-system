package com.shoping.mavenproject7.servlets;

import com.shoping.mavenproject7.entities.User;
import com.shoping.mavenproject7.helper.FactoryProvider;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.regex.Pattern;

public class RegisterServlet extends HttpServlet {

    // Regular expression for validating email
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward the request to the registration form (Register.jsp)
        RequestDispatcher dispatcher = request.getRequestDispatcher("Register.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // Retrieve user input from the request
        String userName = request.getParameter("user_name");
        String userEmail = request.getParameter("user_email");
        String userPassword = request.getParameter("user_password");
        String userPhone = request.getParameter("user_phone");
        String userAddress = request.getParameter("user_address");

        // Validate user input
        StringBuilder validationErrors = new StringBuilder();
        if (userName == null || userName.trim().isEmpty()) {
            validationErrors.append("Username is required.<br>");
        }
        if (userEmail == null || !Pattern.matches(EMAIL_REGEX, userEmail)) {
            validationErrors.append("Invalid email format.<br>");
        }
        if (userPassword == null || userPassword.length() < 6) {
            validationErrors.append("Password must be at least 6 characters long.<br>");
        }
        if (userPhone == null || userPhone.trim().isEmpty()) {
            validationErrors.append("Phone number is required.<br>");
        }
        if (userAddress == null || userAddress.trim().isEmpty()) {
            validationErrors.append("Address is required.<br>");
        }

        // If validation fails, forward back to the registration form with errors
        if (validationErrors.length() > 0) {
            request.setAttribute("errorMessage", validationErrors.toString());
            RequestDispatcher dispatcher = request.getRequestDispatcher("Register.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Attempt to register the user
        try (Session hibernateSession = FactoryProvider.getfactory().openSession()) {
            Transaction transaction = hibernateSession.beginTransaction();

            // Create a new user entity
            User user = new User(userName, userEmail, userPassword, userPhone, "default.jpg", userAddress, "Normal");

            // Save the user entity
            hibernateSession.save(user);
            transaction.commit();

            // Set success message and redirect to index.jsp
            request.setAttribute("message", "Registration Successful!");
            RequestDispatcher dispatcher = request.getRequestDispatcher("index.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            // On failure, forward back to Register.jsp with an error message
            request.setAttribute("errorMessage", "Registration Failed! Please try again later.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("Register.jsp");
            dispatcher.forward(request, response);
        }
    }
}
