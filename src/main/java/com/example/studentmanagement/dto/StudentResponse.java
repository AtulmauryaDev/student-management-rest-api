package com.example.studentmanagement.dto;

public class StudentResponse {

    private Long id;
    private String name;
    private String email;
    private String course;

    public StudentResponse() {
    }

    public StudentResponse(
            Long id, String name, String email, String course) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getCourse() {
        return course;
    }
}