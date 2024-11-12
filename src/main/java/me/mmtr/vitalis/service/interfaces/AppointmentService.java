package me.mmtr.vitalis.service.interfaces;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.enums.AppointmentStatus;

import java.util.List;
import java.util.Optional;

public interface AppointmentService {
    Appointment saveAppointment(Appointment appointment);

    Optional<Appointment>findById(Long id);

    void updateStatus(Long id, AppointmentStatus status);

    List<Appointment> getAll();

    void delete(Long id);
}
