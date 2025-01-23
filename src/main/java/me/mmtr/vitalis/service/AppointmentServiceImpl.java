package me.mmtr.vitalis.service;

import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.dao.interfaces.IAppointmentDAO;
import me.mmtr.vitalis.repository.dao.interfaces.IClinicDAO;
import me.mmtr.vitalis.service.interfaces.AppointmentService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final IAppointmentDAO appointmentDAO;
    private final IClinicDAO clinicDAO;

    public AppointmentServiceImpl(IAppointmentDAO appointmentDAO, IClinicDAO clinicDAO) {
        this.appointmentDAO = appointmentDAO;
        this.clinicDAO = clinicDAO;
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

    @Override
    public void deleteByClinicId(Long clinicId) {
        List<Appointment> relatedAppointments = appointmentDAO.getAll()
                .stream()
                .filter(appointment -> appointment.getClinic().getId().equals(clinicId))
                .toList();

        relatedAppointments.forEach(appointment -> delete(appointment.getId()));
    }
}
