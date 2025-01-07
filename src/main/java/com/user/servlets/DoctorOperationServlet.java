package com.hospitalmanagement.servlets;

import com.hospitalmanagement.dao.DepartmentDao;
import com.hospitalmanagement.dao.DoctorDao;
import com.hospitalmanagement.entities.Department;
import com.hospitalmanagement.entities.Doctor;
import com.hospitalmanagement.helper.FactoryProvider;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.List;

@MultipartConfig
public class DoctorOperationServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SessionFactory factory = FactoryProvider.getFactory();
        Session session = factory.openSession();

        String message = null;

        try {
            String operation = request.getParameter("operation");

            if ("Add Doctor".equalsIgnoreCase(operation)) {
                String doctorName = request.getParameter("doctorName");
                String doctorSpecialization = request.getParameter("doctorSpecialization");
                String contactNumber = request.getParameter("contactNumber");
                String departmentIdParam = request.getParameter("departmentId");

                if (doctorName != null && doctorSpecialization != null && contactNumber != null && departmentIdParam != null) {
                    try {
                        int departmentId = Integer.parseInt(departmentIdParam);

                        Part part = request.getPart("doctorImage"); // Get image part
                        String imagePath = saveImage(part); // Save the image and get its path

                        Doctor doctor = new Doctor();
                        doctor.setName(doctorName);
                        doctor.setSpecialization(doctorSpecialization);
                        doctor.setContactNumber(contactNumber);
                        doctor.setImage(imagePath); // Save the image path in the doctor entity

                        // Get department by ID
                        DepartmentDao departmentDao = new DepartmentDao(factory);
                        Department department = departmentDao.getDepartmentById(departmentId);
                        doctor.setDepartment(department);

                        // DoctorDao
                        DoctorDao doctorDao = new DoctorDao(factory);
                        session.beginTransaction();
                        doctorDao.saveDoctor(doctor);

                        session.getTransaction().commit();
                        message = "Doctor added successfully with name: " + doctorName;
                    } catch (Exception e) {
                        if (session.getTransaction() != null) {
                            session.getTransaction().rollback();
                        }
                        e.printStackTrace();
                        message = "Error adding doctor: " + e.getMessage();
                    }
                } else {
                    message = "Doctor details are missing.";
                }
            } else {
                message = "Operation is not defined for doctors.";
            }
        } finally {
            session.close();
        }

        // Fetch total doctors to display on admin page
        List<Doctor> doctors = new DoctorDao(factory).getAllDoctors();
        int totalDoctors = doctors.size();

        // Set message and total doctors as request attributes
        request.setAttribute("message", message);
        request.setAttribute("totalDoctors", totalDoctors);
        // Forward the request to admin.jsp
        request.getRequestDispatcher("Admin.jsp").forward(request, response);
    }

    // Helper method to save image
    private String saveImage(Part part) throws IOException {
        String fileName = part.getSubmittedFileName(); // Get the original file name
        String imagePath = getServletContext().getRealPath("Image" + File.separator + "doctor")
                + File.separator + fileName; // Build the absolute path to save the image

        // Ensure the directory exists, create it if not
        File directory = new File(getServletContext().getRealPath("Image" + File.separator + "doctor"));
        if (!directory.exists()) {
            directory.mkdirs(); // Create the directory if it doesn't exist
        }

        // Save the uploaded file
        File file = new File(imagePath);
        part.write(file.getAbsolutePath()); // Write the file to the disk

        // Return the relative path to store in the database
        return "Image/doctor/" + fileName; // Return only the relative path for the image
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
        return "Doctor Operation Servlet";
    }
}