package com.web.back.repositories;

import com.web.back.model.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByNumEmployee(String numEmployee);

    List<Employee> findAllByNumEmployeeIn(Set<String> numEmployee);
}
