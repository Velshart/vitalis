package me.mmtr.vitalis.repository.dao.interfaces;

import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.Clinic;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface IAppointmentDAO {
    Optional<Appointment> getById(Long id);
    List<Appointment> getAll();
    void saveOrUpdate(Appointment appointment);
    void delete(Long id);
    boolean existsByDoctorAndDateAndTime(Long doctorId, LocalDate date, LocalTime time);
}
