package com.web.back.repositories;

import com.web.back.model.entities.EmployeeTimesheet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface EmployeeTimesheetsRepository extends JpaRepository<EmployeeTimesheet, UUID> {
    List<EmployeeTimesheet> findAllByEmployeeId(UUID employeeId);

    List<EmployeeTimesheet> findAllByEmployeeNumEmployee(String employeeNumber);

    @Query("SELECT e FROM EmployeeTimesheet e WHERE e.employee.numEmployee = :num_employee AND e.fromDate <= :toDate AND e.toDate >= :fromDate")
    List<EmployeeTimesheet> findOverlappingTimesheets(@Param("num_employee") String numEmployee, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);

    @Query("SELECT e FROM EmployeeTimesheet e WHERE e.employee.id IN :employeeIds AND e.fromDate <= :toDate AND e.toDate >= :fromDate")
    List<EmployeeTimesheet> findOverlappingTimesheetsByIds(@Param("employeeIds") List<UUID> employeeIds, @Param("fromDate") Instant fromDate, @Param("toDate") Instant toDate);
}
