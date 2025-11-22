/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.student.controller;

import com.student.dao.StudentDAO;
import com.student.model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect("student?action=list");
            return;
        }
        
        switch (action) {
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
            case "search":
                searchStudent(request, response);
                break;
            case "sort":
                sortStudents(request, response);
                break;
            case "filter":
                filterStudents(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
        }
    }
    
    // List all students
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

    // Get current page parameter (default 1)
    String pageParam = request.getParameter("page");
    int currentPage = 1;
    if (pageParam != null) {
        try {
            currentPage = Integer.parseInt(pageParam);
        } catch (NumberFormatException e) {
            currentPage = 1;
        }
    }

    int recordsPerPage = 10; // number of records per page
    int totalRecords = studentDAO.getTotalStudents();
    int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);

    // Handle edge cases
    if (currentPage < 1) currentPage = 1;
    if (currentPage > totalPages) currentPage = totalPages;

    int offset = (currentPage - 1) * recordsPerPage;

    List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);

    // Set attributes for JSP
    request.setAttribute("students", students);
    request.setAttribute("currentPage", currentPage);
    request.setAttribute("totalPages", totalPages);
    request.setAttribute("recordsPerPage", recordsPerPage);
    request.setAttribute("totalRecords", totalRecords);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
    dispatcher.forward(request, response);
    }
    
    // Show form for new student
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Show form for editing student
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student existingStudent = studentDAO.getStudentById(id);
        
        request.setAttribute("student", existingStudent);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    // Insert new student
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    // 1. Get parameters and create Student object
    String studentCode = request.getParameter("studentCode");
    String fullName = request.getParameter("fullName");
    String email = request.getParameter("email");
    String major = request.getParameter("major");
    Student student = new Student(studentCode, fullName, email, major);
    // 2. Validate
    if (!validateStudent(student, request)) {
        // Set student as attribute (to preserve entered data)
        request.setAttribute("student", student);
        // Forward back to form
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
        return; // STOP here
    }
    
    // 3. If valid, proceed with insert
    if (studentDAO.addStudent(student)) {
        response.sendRedirect("student?action=list&message=Added successfully");
    } else {
        response.sendRedirect("student?action=list&error=Failed to add student");
        }
    }
    
    // Update student
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

    // 1. Read parameters
    int id = Integer.parseInt(request.getParameter("id"));
    String studentCode = request.getParameter("studentCode");
    String fullName = request.getParameter("fullName");
    String email = request.getParameter("email");
    String major = request.getParameter("major");

    // 2. Create Student object
    Student student = new Student(studentCode, fullName, email, major);
    student.setId(id);

    // 3. Validate
    if (!validateStudent(student, request)) {
        // Keep entered values for re-display
        request.setAttribute("student", student);

        // Return to form with error messages
        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-form.jsp");
        dispatcher.forward(request, response);
        return; // STOP execution to avoid update
    }

    // 4. If valid, update in database
    if (studentDAO.updateStudent(student)) {
        response.sendRedirect("student?action=list&message=Student updated successfully");
    } else {
        response.sendRedirect("student?action=list&error=Failed to update student");
        }
    }
    
    // Delete student
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    //Search Student
    private void searchStudent(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {
    
    String keyword = request.getParameter("keyword");
    
    StudentDAO dao = new StudentDAO();
    List<Student> students;
    if (keyword == null || keyword.trim().isEmpty()) {
        students = dao.getAllStudents();   // <-- your existing method
        keyword = ""; // to avoid null issues in JSP
    } else {
        students = dao.searchStudents(keyword.trim());
    }
    
    request.setAttribute("students", students);
    request.setAttribute("keyword", keyword);
    
    RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
    dispatcher.forward(request, response);
    }
    //Validate Students
    private boolean validateStudent(Student student, HttpServletRequest request) {
    boolean isValid = true;
    // Student code pattern: 2 letters + 3+ digits
    String codePattern = "[A-Z]{2}[0-9]{3,}";

    // Email pattern (simple version)
    String emailPattern = "^[a-z0-9+_.-]+@(.+)$";
    // Name Pattern
    String namePattern = "^[A-Za-z\\s]+$";

        // Validate student code
        if (student.getStudentCode() == null || student.getStudentCode().trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required");
        isValid = false;
        } else if (!student.getStudentCode().matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 letters + 3+ digits (e.g., SV001)");
            isValid = false;
        }
        // TODO: Validate full name

        if (student.getFullName() == null || student.getFullName().trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required");
            isValid = false;
        } else if (!student.getFullName().matches(namePattern)) {
            request.setAttribute("errorName", "Full name must contain only letters and spaces");
            isValid = false;
        }
        // TODO: Validate Email
        if (student.getEmail() == null ||student.getEmail().isEmpty() ) {
            request.setAttribute("errorEmail", "Email is required and follow the format");
            isValid = false;
        } else if (!student.getEmail().matches(emailPattern)) {
            request.setAttribute("errorEmail", "Invalid format.");
            isValid = false;
        }

        // TODO: Validate major

        if (student.getMajor() == null || student.getMajor().trim().isEmpty()) {
        request.setAttribute("errorMajor", "Major is required");
        isValid = false;
        } else if (
                !student.getMajor().equals("Business Administration") &&
                !student.getMajor().equals("Software Engineering") &&
                !student.getMajor().equals("Information Technology") &&
                !student.getMajor().equals("Computer Science") 
            ) {
            request.setAttribute("errorMajor", "Invalid major");
            isValid = false;
        }
        return isValid;
    }
    private void sortStudents(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException {

    String sortBy = request.getParameter("sortBy"); // e.g., "full_name"
    String order = request.getParameter("order");   // "asc" or "desc"

    List<Student> students = studentDAO.getStudentsSorted(sortBy, order);

    request.setAttribute("students", students);
    request.setAttribute("sortBy", sortBy);
    request.setAttribute("order", order);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
    dispatcher.forward(request, response);
}

    // Filter students by major
    private void filterStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String major = request.getParameter("major"); // e.g., "Computer Science"

        List<Student> students = studentDAO.getStudentsByMajor(major);

        request.setAttribute("students", students);
        request.setAttribute("selectedMajor", major);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/views/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
}