package me.mmtr.vitalis.service.interfaces;

import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.repository.dao.interfaces.IAppointmentDAO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AppointmentServiceImpl implements AppointmentService {

    private final IAppointmentDAO appointmentDAO;
    private final UserRepository userRepository;

    public AppointmentServiceImpl(IAppointmentDAO appointmentDAO, UserRepository userRepository) {
        this.appointmentDAO = appointmentDAO;
        this.userRepository = userRepository;
    }

    @Override
    public Appointment saveAppointment(Appointment appointment) {
        this.appointmentDAO.saveOrUpdate(appointment);
        return appointment;
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
