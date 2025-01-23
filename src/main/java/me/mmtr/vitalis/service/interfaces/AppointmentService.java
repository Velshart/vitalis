package me.mmtr.vitalis.service.interfaces;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.enums.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment saveAppointment(Appointment appointment);

    boolean existsByDoctorAndDateAndTime(Long doctorId, LocalDate date, LocalTime time);

    Optional<Appointment>findById(Long id);

    void updateStatus(Long id, AppointmentStatus status);

    List<Appointment> getAll();

    void deleteByClinicId(Long clinicId);

    void delete(Long id);
}
