package com.zurich.demo.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "doctors")
@ToString
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@NoArgsConstructor
public class Doctor extends BaseEntity {


    @NotBlank(message = "First name is mandatory")
    @Size(max = 100)
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    @Size(max = 100)
    private String lastName;

    @NotBlank(message = "Specialization is mandatory")
    @Size(max = 100)
    private String specialization;

    @NotBlank(message = "Contact number is mandatory")
    @Size(max = 20)
    private String contactNumber;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    @Column(unique = true)
    private String email;

    @NotBlank(message = "License number is mandatory")
    @Size(max = 50)
    @Column(unique = true)
    private String licenseNumber;

    @NotBlank(message = "Department is mandatory")
    @Size(max = 100)
    private String department;
}