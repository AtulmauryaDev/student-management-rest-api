package com.example.studentmanagement.repository;

import com.example.studentmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    // Search by a part of the student's name, ignoring letter case.
    Page<Student> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
