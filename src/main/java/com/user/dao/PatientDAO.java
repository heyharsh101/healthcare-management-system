package com.hospitalmanagement.dao;

import com.hospitalmanagement.entities.Patient;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;

public class PatientDao {
    private final SessionFactory factory; // Use final for immutability

    public PatientDao(SessionFactory factory) {
        this.factory = factory;
    }

    // Method to save a patient
    public boolean savePatient(Patient patient) {
        Transaction tx = null; // Initialize transaction
        Session session = null; // Initialize session
        try {
            session = this.factory.openSession(); // Open a new session
            tx = session.beginTransaction(); // Start transaction
            session.save(patient); // Save the patient
            tx.commit(); // Commit the transaction
            return true; // Return true on success
        } catch (Exception e) {
            if (tx != null) {
                tx.rollback(); // Rollback the transaction on error
            }
            e.printStackTrace(); // Log the exception
            return false; // Return false on failure
        } finally {
            if (session != null) {
                session.close(); // Close the session to prevent resource leaks
            }
        }
    }

    // Method to get all patients
    public List<Patient> getAllPatients() {
        List<Patient> patients = new ArrayList<>();
        try (Session session = this.factory.openSession()) { // Use try-with-resources for session management
            Query<Patient> query = session.createQuery("from Patient", Patient.class);
            patients = query.getResultList(); // Fetch the results
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return patients; // Return the list of patients
    }

    // Method to get patients by doctor ID
    public List<Patient> getAllPatientsByDoctorID(int doctorId) {
        List<Patient> patients = new ArrayList<>();
        try (Session session = this.factory.openSession()) { // Use try-with-resources for session management
            Query<Patient> query = session.createQuery("from Patient as patient where patient.doctor.doctorId =: id", Patient.class);
            query.setParameter("id", doctorId);
            patients = query.getResultList(); // Fetch the results
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return patients; // Return the list of patients
    }

    // Method to count total number of patients
    public long getPatientCount() {
        long count = 0; // Initialize count
        try (Session session = this.factory.openSession()) { // Use try-with-resources for session management
            String hql = "SELECT COUNT(patient) FROM Patient patient"; // HQL query to count patients
            Query<Long> query = session.createQuery(hql, Long.class); // Create query
            count = query.uniqueResult(); // Get the count result
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return count; // Return the total count of patients
    }

    // Method to search patients by name or medical history
    public List<Patient> searchPatientsByNameOrHistory(String searchQuery) {
        List<Patient> patients = new ArrayList<>();
        try (Session session = this.factory.openSession()) { // Use try-with-resources for session management
            // Create HQL query to search patients by name or medical history
            Query<Patient> query = session.createQuery("FROM Patient WHERE name LIKE :searchTerm OR medicalHistory LIKE :searchTerm", Patient.class);
            query.setParameter("searchTerm", "%" + searchQuery + "%");
            patients = query.getResultList(); // Fetch the results
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception
        }
        return patients; // Return the list of patients
    }
}
