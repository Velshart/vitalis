package me.mmtr.vitalis.repository.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Invitation;
import me.mmtr.vitalis.repository.dao.interfaces.IInvitationDAO;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class InvitationDAO implements IInvitationDAO {
    private final String GET_ALL_JPQL = "FROM me.mmtr.vitalis.data.Invitation";
    private final String GET_BY_ID_JPQL = "SELECT a FROM me.mmtr.vitalis.data.Invitation a WHERE a.id = :id";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Invitation> getById(Long id) {
        TypedQuery<Invitation> query = entityManager.createQuery(GET_BY_ID_JPQL, Invitation.class);
        query.setParameter("id", id);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    @Override
    public List<Invitation> getAll() {
        TypedQuery<Invitation> query = entityManager.createQuery(GET_ALL_JPQL, Invitation.class);
        return query.getResultList();
    }

    @Override
    public void saveOrUpdate(Invitation invitation) {
        if (getById(invitation.getId()).isEmpty()) {
            entityManager.persist(invitation);
        } else {
            entityManager.merge(invitation);
        }
    }

    @Override
    public void delete(Long id) {
        getById(id).ifPresent(entityManager::remove);
    }
}
