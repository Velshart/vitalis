package me.mmtr.vitalis.scheduler;

import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.data.enums.AppointmentStatus;
import me.mmtr.vitalis.repository.dao.interfaces.IAppointmentDAO;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class AppointmentExpirationScheduler {
    private final long TWENTY_FOUR_HOURS_IN_MS = 86_400_000;

    private final IAppointmentDAO appointmentDAO;

    public AppointmentExpirationScheduler(IAppointmentDAO appointmentDAO) {
        this.appointmentDAO = appointmentDAO;
    }

    @Transactional
    @Scheduled(fixedRate = TWENTY_FOUR_HOURS_IN_MS)
    public void expireAppointments() {
        LocalDate today = LocalDate.now();

        List<Appointment> appointmentsToExpire = appointmentDAO.getAll().stream()
                .filter(appointment -> appointment.getStatus().equals(AppointmentStatus.PENDING) ||
                        appointment.getStatus().equals(AppointmentStatus.SCHEDULED))
                .filter(appointment -> appointment.getDate().isBefore(today))
                .toList();

        appointmentsToExpire.forEach(appointment -> appointment.setStatus(AppointmentStatus.EXPIRED));

        appointmentsToExpire.forEach(appointment -> appointment
                .setReasonForChange("Lekarz przyjmujący nie dokonał zmiany statusu wizyty."));

        appointmentsToExpire.forEach(appointmentDAO::saveOrUpdate);
    }
}
