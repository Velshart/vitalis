package me.mmtr.vitalis.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.mmtr.vitalis.data.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne
    @JoinColumn(name = "clinic_id")
    private Clinic clinic;

    @Column
    private LocalDate date;

    @Column
    private LocalTime time;

    @Column(length = 100)
    private String comment;

    @Column(length = 100)
    private String reasonForChange;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;
}
