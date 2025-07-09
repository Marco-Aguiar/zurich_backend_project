package com.zurich.demo.repository;

import com.zurich.demo.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.doctor.id = :doctorId AND " +
            "((a.appointmentDateTime < :newEndTime AND a.appointmentDateTime > :newStartTimeMinusDuration) " +
            "OR (a.appointmentDateTime = :newStartTime OR a.appointmentDateTime = :newEndTime))") // Added equality checks for exact boundary conflicts
    List<Appointment> findConflictingAppointmentsForDoctor(
            @Param("doctorId") Long doctorId,
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newEndTime") LocalDateTime newEndTime,
            @Param("newStartTimeMinusDuration") LocalDateTime newStartTimeMinusDuration // newStartTime.minusMinutes(30)
    );

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.patient.id = :patientId AND " +
            "((a.appointmentDateTime < :newEndTime AND a.appointmentDateTime > :newStartTimeMinusDuration) " +
            "OR (a.appointmentDateTime = :newStartTime OR a.appointmentDateTime = :newEndTime))") // Added equality checks
    List<Appointment> findConflictingAppointmentsForPatient(
            @Param("patientId") Long patientId,
            @Param("newStartTime") LocalDateTime newStartTime,
            @Param("newEndTime") LocalDateTime newEndTime,
            @Param("newStartTimeMinusDuration") LocalDateTime newStartTimeMinusDuration // newStartTime.minusMinutes(30)
    );

    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByDoctorId(Long doctorId);
}