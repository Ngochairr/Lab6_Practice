<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>
        <c:choose>
            <c:when test="${student != null}">Edit Student</c:when>
            <c:otherwise>Add New Student</c:otherwise>
        </c:choose>
    </title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 20px;
        }
        .error {
            color: red;
            font-size: 14px;
            display: block;
            margin-top: 5px;
        }
        .container {
            background: white;
            border-radius: 10px;
            padding: 40px;
            box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            width: 100%;
            max-width: 600px;
        }
        
        h1 {
            color: #333;
            margin-bottom: 30px;
            font-size: 28px;
            text-align: center;
        }
        
        .form-group {
            margin-bottom: 25px;
        }
        
        label {
            display: block;
            margin-bottom: 8px;
            color: #555;
            font-weight: 500;
            font-size: 14px;
        }
        
        input[type="text"],
        input[type="email"],
        select {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #ddd;
            border-radius: 5px;
            font-size: 14px;
            transition: border-color 0.3s;
        }
        
        input:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .required {
            color: #dc3545;
        }
        
        .button-group {
            display: flex;
            gap: 15px;
            margin-top: 30px;
        }
        
        .btn {
            flex: 1;
            padding: 14px;
            border: none;
            border-radius: 5px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s;
            text-decoration: none;
            text-align: center;
            display: inline-block;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
        }
        
        .btn-primary:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
        }
        
        .btn-secondary {
            background-color: #6c757d;
            color: white;
        }
        
        .btn-secondary:hover {
            background-color: #5a6268;
        }
        
        .info-text {
            font-size: 12px;
            color: #666;
            margin-top: 5px;
        }
    </style>
</head>
<body>
    <div class="container">
        <h1>
            <c:choose>
                <c:when test="${student != null}">
                    ✏️ Edit Student
                </c:when>
                <c:otherwise>
                    ➕ Add New Student
                </c:otherwise>
            </c:choose>
        </h1>
        
        <form action="student" method="POST">
            <!-- Hidden field for action -->
            <c:choose>
                <c:when test="${student != null}">
                    <input type="hidden" name="action" value="update">
                    <input type="hidden" name="id" value="${student.id}">
                </c:when>
                <c:otherwise>
                    <input type="hidden" name="action" value="insert">
                </c:otherwise>
            </c:choose>

            <!-- Student Code -->
            <div class="form-group">
                <label for="studentCode">
                    Student Code <span class="required">*</span>
                </label>
                <input type="text" 
                       id="studentCode" 
                       name="studentCode" 
                       value="${student.studentCode}" 
                       <c:if test="${student != null}">readonly</c:if> 
                       placeholder="e.g., SV001, IT123">
                <p class="info-text">Format: 2 letters + 3+ digits</p>
                <c:if test="${not empty errorCode}">
                    <span class="error">${errorCode}</span>
                </c:if>
            </div>

            <!-- Full Name -->
            <div class="form-group">
                <label for="fullName">
                    Full Name <span class="required">*</span>
                </label>
                <input type="text" 
                       id="fullName" 
                       name="fullName" 
                       value="${student.fullName}" 
                       required 
                       placeholder="Enter full name">
                <c:if test="${not empty errorName}">
                    <span class="error">${errorName}</span>
                </c:if>
            </div>

            <!-- Email -->
            <div class="form-group">
                <label for="email">
                    Email <span class="required">*</span>
                </label>
                <input type="email" 
                       id="email" 
                       name="email" 
                       value="${student.email}" 
                       required 
                       placeholder="student@example.com">
                <c:if test="${not empty errorEmail}">
                    <span class="error">${errorEmail}</span>
                </c:if>
            </div>

            <!-- Major -->
            <div class="form-group">
                <label for="major">
                    Major <span class="required">*</span>
                </label>
                <select id="major" name="major" required>
                    <option value="">-- Select Major --</option>
                    <option value="Computer Science" <c:if test="${student.major == 'Computer Science'}">selected</c:if>>Computer Science</option>
                    <option value="Information Technology" <c:if test="${student.major == 'Information Technology'}">selected</c:if>>Information Technology</option>
                    <option value="Software Engineering" <c:if test="${student.major == 'Software Engineering'}">selected</c:if>>Software Engineering</option>
                    <option value="Business Administration" <c:if test="${student.major == 'Business Administration'}">selected</c:if>>Business Administration</option>
                </select>
                <c:if test="${not empty errorMajor}">
                    <span class="error">${errorMajor}</span>
                </c:if>
            </div>

            <!-- Buttons -->
            <div class="button-group">
                <button type="submit" class="btn btn-primary">
                    <c:choose>
                        <c:when test="${student != null}">💾 Update Student</c:when>
                        <c:otherwise>➕ Add Student</c:otherwise>
                    </c:choose>
                </button>
                <a href="student?action=list" class="btn btn-secondary">❌ Cancel</a>
            </div>
        </form>
    </div>
</body>
</html>
