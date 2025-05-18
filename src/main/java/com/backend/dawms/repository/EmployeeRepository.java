package com.backend.dawms.repository;

import com.backend.dawms.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartmentId(Long departmentId);
    boolean existsByEmployeeId(String employeeId);
    boolean existsByEmail(String email);
} 