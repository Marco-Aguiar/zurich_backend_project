package com.zurich.demo.service;

import com.zurich.demo.model.Appointment;
import com.zurich.demo.model.Doctor;
import com.zurich.demo.model.Patient;
import com.zurich.demo.repository.AppointmentRepository;
import com.zurich.demo.repository.DoctorRepository;
import com.zurich.demo.repository.PatientRepository;
import com.zurich.demo.exception.ResourceNotFoundException;
import com.zurich.demo.exception.ScheduleConflictException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;

    private static final int APPOINTMENT_DURATION_MINUTES = 30;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
    }

    public Appointment createAppointment(Appointment newAppointment) {
        Long doctorId = newAppointment.getDoctor().getId();
        Long patientId = newAppointment.getPatient().getId();
        LocalDateTime appointmentDateTime = newAppointment.getAppointmentDateTime();

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        LocalDateTime newAppointmentEnd = appointmentDateTime.plusMinutes(APPOINTMENT_DURATION_MINUTES);

        LocalDateTime newAppointmentStartMinusDuration = appointmentDateTime.minusMinutes(APPOINTMENT_DURATION_MINUTES);

        List<Appointment> doctorConflicts = appointmentRepository.findConflictingAppointmentsForDoctor(
                doctorId, appointmentDateTime, newAppointmentEnd, newAppointmentStartMinusDuration);
        if (!doctorConflicts.isEmpty()) {
            throw new ScheduleConflictException("Doctor is not available at the requested time due to a scheduling conflict.");
        }

        List<Appointment> patientConflicts = appointmentRepository.findConflictingAppointmentsForPatient(
                patientId, appointmentDateTime, newAppointmentEnd, newAppointmentStartMinusDuration);
        if (!patientConflicts.isEmpty()) {
            throw new ScheduleConflictException("Patient already has an appointment at the requested time.");
        }

        newAppointment.setDoctor(doctor);
        newAppointment.setPatient(patient);

        return appointmentRepository.save(newAppointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id).map(appointment -> {
            if (appointmentDetails.getPatient() != null && appointmentDetails.getPatient().getId() != null) {
                patientRepository.findById(appointmentDetails.getPatient().getId())
                        .ifPresentOrElse(
                                appointment::setPatient,
                                () -> { throw new ResourceNotFoundException("Patient not found with ID: " + appointmentDetails.getPatient().getId()); }
                        );
            }
            if (appointmentDetails.getDoctor() != null && appointmentDetails.getDoctor().getId() != null) {
                doctorRepository.findById(appointmentDetails.getDoctor().getId())
                        .ifPresentOrElse(
                                appointment::setDoctor,
                                () -> { throw new ResourceNotFoundException("Doctor not found with ID: " + appointmentDetails.getDoctor().getId()); }
                        );
            }
            if (appointmentDetails.getAppointmentDateTime() != null) {
                appointment.setAppointmentDateTime(appointmentDetails.getAppointmentDateTime());
            }
            if (appointmentDetails.getReason() != null) {
                appointment.setReason(appointmentDetails.getReason());
            }
            if (appointmentDetails.getStatus() != null) {
                appointment.setStatus(appointmentDetails.getStatus());
            }
            if (appointmentDetails.getNotes() != null) {
                appointment.setNotes(appointmentDetails.getNotes());
            }
            return appointmentRepository.save(appointment);
        }).orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
    }


    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found with ID: " + id));
    }

    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Appointment not found with ID: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    public List<Appointment> getAppointmentsByPatientId(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}