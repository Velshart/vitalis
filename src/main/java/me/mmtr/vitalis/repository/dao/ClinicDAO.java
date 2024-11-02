package me.mmtr.vitalis.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.repository.dao.interfaces.IClinicDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClinicDAO implements IClinicDAO {

    private final String GET_ALL_JPQL = "FROM me.mmtr.vitalis.data.Clinic";
    private final String GET_BY_ID_JPQL = "SELECT c FROM me.mmtr.vitalis.data.Clinic c WHERE c.id = :id";
    @PersistenceContext
    private EntityManager entityManager;

    public ClinicDAO(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Clinic> getById(Long id) {
        TypedQuery<Clinic> query = entityManager.createQuery(GET_BY_ID_JPQL, Clinic.class);
        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Clinic> getAll() {
        TypedQuery<Clinic> query = entityManager.createQuery(GET_ALL_JPQL, Clinic.class);
        return query.getResultList();
    }

    @Override
    public void saveOrUpdate(Clinic clinic) {
        if (getById(clinic.getId()).isEmpty()) {
            entityManager.persist(clinic);
        } else {
            entityManager.merge(clinic);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id).ifPresent(entityManager::remove);
    }
}
