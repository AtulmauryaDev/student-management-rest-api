package com.example.studentmanagement.controller;

import com.example.studentmanagement.dto.StudentRequest;
import com.example.studentmanagement.dto.StudentResponse;
import com.example.studentmanagement.entity.Student;
import com.example.studentmanagement.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private static final List<String> ALLOWED_SORT_FIELDS =
            List.of("id", "name", "email", "course");

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    private StudentResponse convertToResponse(Student student) {
        return new StudentResponse(
                student.getId(),
                student.getName(),
                student.getEmail(),
                student.getCourse()
        );
    }

    // POST /api/students - create a student.
    @PostMapping
    public ResponseEntity<StudentResponse> createStudent(
            @Valid @RequestBody StudentRequest request) {

        Student student = new Student();
        student.setName(request.getName().trim());
        student.setEmail(request.getEmail().trim());
        student.setCourse(request.getCourse().trim());

        Student savedStudent = studentService.saveStudent(student);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(convertToResponse(savedStudent));
    }

    // GET /api/students - get all students with pagination and sorting.
    @GetMapping
    public ResponseEntity<?> getAllStudents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        ResponseEntity<String> validationError = validatePageAndSort(page, size, sortBy, direction);
        if (validationError != null) {
            return validationError;
        }

        PageRequest pageable = createPageRequest(page, size, sortBy, direction);
        Page<StudentResponse> students = studentService.getStudents(pageable)
                .map(this::convertToResponse);

        return ResponseEntity.ok(students);
    }

    // GET /api/students/search?name=Rahul - partial, case-insensitive name search.
    @GetMapping("/search")
    public ResponseEntity<?> searchStudentsByName(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {

        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body("Student name must not be blank.");
        }

        ResponseEntity<String> validationError = validatePageAndSort(page, size, sortBy, direction);
        if (validationError != null) {
            return validationError;
        }

        PageRequest pageable = createPageRequest(page, size, sortBy, direction);
        Page<StudentResponse> students = studentService
                .searchStudentsByName(name.trim(), pageable)
                .map(this::convertToResponse);

        return ResponseEntity.ok(students);
    }

    // GET /api/students/{id} - get one student.
    @GetMapping("/{id}")
    public ResponseEntity<StudentResponse> getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // PUT /api/students/{id} - update an existing student.
    @PutMapping("/{id}")
    public ResponseEntity<StudentResponse> updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody StudentRequest request) {

        return studentService.getStudentById(id)
                .map(existingStudent -> {
                    existingStudent.setName(request.getName().trim());
                    existingStudent.setEmail(request.getEmail().trim());
                    existingStudent.setCourse(request.getCourse().trim());
                    Student savedStudent = studentService.saveStudent(existingStudent);
                    return ResponseEntity.ok(convertToResponse(savedStudent));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /api/students/{id} - delete an existing student.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        boolean deleted = studentService.deleteStudent(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> validatePageAndSort(
            int page, int size, String sortBy, String direction) {

        if (page < 0 || size < 1 || size > 100) {
            return ResponseEntity.badRequest()
                    .body("Page must be >= 0 and size must be between 1 and 100.");
        }

        if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
            return ResponseEntity.badRequest()
                    .body("Invalid sortBy field. Use id, name, email, or course.");
        }

        if (!"asc".equalsIgnoreCase(direction) && !"desc".equalsIgnoreCase(direction)) {
            return ResponseEntity.badRequest()
                    .body("Direction must be asc or desc.");
        }

        return null;
    }

    private PageRequest createPageRequest(
            int page, int size, String sortBy, String direction) {

        Sort sort = "desc".equalsIgnoreCase(direction)
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        return PageRequest.of(page, size, sort);
    }
}
