package com.hospitalmanagement.dao;

import java.util.ArrayList;
import java.util.List;

import com.mysql.cj.xdevapi.SessionFactory;

public class DepartmentDao {
    private final SessionFactory factory; // Use SessionFactory to open new sessions

    public DepartmentDao(SessionFactory factory) {
        this.factory = factory;
    }

    // Method to save a department
    public int saveDepartment(Department department) {
        Transaction transaction = null; // Initialize transaction
        int departmentId = 0; // To store the generated ID

        try (Session session = factory.openSession()) { // Use try-with-resources for session management
            transaction = session.beginTransaction(); // Start the transaction
            departmentId = (int) session.save(department); // Save the department and get the ID
            transaction.commit(); // Commit the transaction
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback in case of an error
            }
            e.printStackTrace(); // Log the error for debugging
        }

        return departmentId; // Return the generated ID (may be 0 if failed)
    }

    // Method to get a department by ID
    public Department getDepartmentById(int departmentId) {
        Department department = null;

        try (Session session = factory.openSession()) { // Use try-with-resources for session management
            department = session.get(Department.class, departmentId); // Fetch the department by ID
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
        }

        return department; // Return the department (may be null if not found)
    }

    // Method to get all departments
    public List<Department> getAllDepartments() {
        List<Department> departments = new ArrayList<>();

        try (Session session = this.factory.openSession()) { // Use try-with-resources for session management
            Query<Department> query = session.createQuery("from Department", Department.class);
            departments = query.getResultList(); // Fetch all departments
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
        }

        return departments; // Return the list of departments
    }

    // Method to get the total count of departments
    public int getDepartmentCount() {
        int count = 0;

        try (Session session = factory.openSession()) { // Use try-with-resources for session management
            Query<Long> query = session.createQuery("SELECT COUNT(d) FROM Department d", Long.class);
            count = query.uniqueResult().intValue(); // Get the count as an int
        } catch (Exception e) {
            e.printStackTrace(); // Log the error for debugging
        }

        return count; // Return the total count of departments
    }
}
