package me.mmtr.vitalis.service;

import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.dao.interfaces.IAppointmentDAO;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final IAppointmentDAO appointmentDAO;

    public AppointmentServiceImpl(IAppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        this.appointmentDAO.saveOrUpdate(appointment);
        return appointment;
    }

    @Override
    public boolean existsByDoctorAndDateAndTime(Long doctorId, LocalDate date, LocalTime time) {
        return appointmentDAO.existsByDoctorAndDateAndTime(doctorId, date, time);
    }

    @Override
    public Optional<Appointment> findById(Long id) {
        return appointmentDAO.getById(id);
    }

    @Override
    public void updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = findById(id).orElseThrow();
        appointment.setStatus(status);

        saveAppointment(appointment);
    }

    @Override
    public List<Appointment> getAll() {
        return appointmentDAO.getAll();
    }

    @Override
    public void delete(Long id) {
        appointmentDAO.delete(id);
    }
}
