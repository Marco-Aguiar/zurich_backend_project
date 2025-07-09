package com.zurich.demo.service;

import com.zurich.demo.exception.ResourceNotFoundException;
import com.zurich.demo.model.Doctor;
import com.zurich.demo.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;

    public DoctorService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Transactional(readOnly = true)
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));
    }

    @Transactional
    public Doctor createDoctor(Doctor doctor) {
        if (doctorRepository.findByEmail(doctor.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Doctor with email " + doctor.getEmail() + " already exists.");
        }
        if (doctorRepository.findByLicenseNumber(doctor.getLicenseNumber()).isPresent()) {
            throw new IllegalArgumentException("Doctor with license number " + doctor.getLicenseNumber() + " already exists.");
        }
        return doctorRepository.save(doctor);
    }

    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with ID: " + id));

        doctor.setFirstName(doctorDetails.getFirstName());
        doctor.setLastName(doctorDetails.getLastName());
        doctor.setSpecialization(doctorDetails.getSpecialization());
        doctor.setContactNumber(doctorDetails.getContactNumber());
        doctor.setDepartment(doctorDetails.getDepartment());

        if (!doctor.getEmail().equals(doctorDetails.getEmail())) {
            if (doctorRepository.findByEmail(doctorDetails.getEmail()).isPresent()) {
                throw new IllegalArgumentException("Email " + doctorDetails.getEmail() + " is already taken by another doctor.");
            }
            doctor.setEmail(doctorDetails.getEmail());
        }

        if (!doctor.getLicenseNumber().equals(doctorDetails.getLicenseNumber())) {
            if (doctorRepository.findByLicenseNumber(doctorDetails.getLicenseNumber()).isPresent()) {
                throw new IllegalArgumentException("License number " + doctorDetails.getLicenseNumber() + " is already taken by another doctor.");
            }
            doctor.setLicenseNumber(doctorDetails.getLicenseNumber());
        }

        return doctorRepository.save(doctor);
    }

    @Transactional
    public void deleteDoctor(Long id) {
        if (!doctorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Doctor not found with ID: " + id);
        }
        doctorRepository.deleteById(id);
    }
}