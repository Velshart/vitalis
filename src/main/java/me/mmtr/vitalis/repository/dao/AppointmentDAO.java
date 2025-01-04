package me.mmtr.vitalis.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Appointment;
import me.mmtr.vitalis.repository.dao.interfaces.IAppointmentDAO;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class AppointmentDAO implements IAppointmentDAO {
    private final String GET_ALL_JPQL = "FROM me.mmtr.vitalis.data.Appointment";
    private final String GET_BY_ID_JPQL = "SELECT a FROM me.mmtr.vitalis.data.Appointment a WHERE a.id = :id";
    private final String EXISTS_BY_DOCTOR_DATE_TIME_JPQL = "SELECT COUNT(a) > 0 FROM me.mmtr.vitalis.data.Appointment a WHERE a.doctor.id = :doctorId AND a.date = :date AND a.time = :time";

    @PersistenceContext
    private EntityManager entityManager;

    public AppointmentDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Appointment> getById(Long id) {
        TypedQuery<Appointment> query = entityManager.createQuery(GET_BY_ID_JPQL, Appointment.class);
        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Appointment> getAll() {
        TypedQuery<Appointment> query = entityManager.createQuery(GET_ALL_JPQL, Appointment.class);
        return query.getResultList();
    }

    @Override
    public void saveOrUpdate(Appointment appointment) {
        if (getById(appointment.getId()).isEmpty()) {
            entityManager.persist(appointment);
        } else {
            entityManager.merge(appointment);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id).ifPresent(entityManager::remove);
    }

    @Override
    public boolean existsByDoctorAndDateAndTime(Long doctorId, LocalDate date, LocalTime time) {
        TypedQuery<Boolean> query = entityManager.createQuery(EXISTS_BY_DOCTOR_DATE_TIME_JPQL, Boolean.class);
        query.setParameter("doctorId", doctorId);
        query.setParameter("date", date);
        query.setParameter("time", time);

        return query.getSingleResult();
    }
}