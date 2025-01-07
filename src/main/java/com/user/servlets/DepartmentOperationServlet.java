package com.hospitalmanagement.servlets;

import com.hospitalmanagement.dao.DepartmentDao;
import com.hospitalmanagement.entities.Department;
import com.hospitalmanagement.helper.FactoryProvider;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

@MultipartConfig
public class DepartmentOperationServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        SessionFactory factory = FactoryProvider.getFactory();
        Session session = factory.openSession();

        try (PrintWriter out = response.getWriter()) {
            String operation = request.getParameter("operation");

            if ("Add Department".equalsIgnoreCase(operation)) {
                String name = request.getParameter("departmentName");
                String description = request.getParameter("departmentDescription");

                if (name != null && description != null) {
                    // Create a new Department object
                    Department department = new Department();
                    department.setDepartmentName(name);
                    department.setDepartmentDescription(description);

                    DepartmentDao departmentDao = new DepartmentDao(factory);

                    try {
                        session.beginTransaction(); // Start transaction
                        int deptId = departmentDao.saveDepartment(department); // Save department
                        session.getTransaction().commit(); // Commit the transaction
                        out.println("Department saved with ID: " + deptId);
                    } catch (Exception e) {
                        if (session.getTransaction() != null) {
                            session.getTransaction().rollback(); // Rollback in case of an error
                        }
                        e.printStackTrace();
                        out.println("Error saving department: " + e.getMessage());
                    }
                } else {
                    out.println("Department name or description is missing.");
                }
            } else {
                out.println("Operation is not defined for departments.");
            }
        } finally {
            session.close(); 
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
        return "Department Operation Servlet";
    }
}
