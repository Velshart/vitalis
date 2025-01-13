package me.mmtr.vitalis.service;


import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Invitation;
import me.mmtr.vitalis.data.enums.InvitationStatus;
import me.mmtr.vitalis.repository.dao.interfaces.IInvitationDAO;
import me.mmtr.vitalis.service.interfaces.InvitationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {

    private final IInvitationDAO INVITATION_DAO;

    public InvitationServiceImpl(IInvitationDAO invitationDAO) {
        this.INVITATION_DAO = invitationDAO;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Invitation invitation) {
        INVITATION_DAO.saveOrUpdate(invitation);
    }

    @Override
    @Transactional
    public Optional<Invitation> getById(Long id) {
        return INVITATION_DAO.getById(id);
    }

    @Override
    @Transactional
    public List<Invitation> getAll() {
        return INVITATION_DAO.getAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        INVITATION_DAO.delete(id);
    }

    @Override
    @Transactional
    public void updateStatus(Long id, InvitationStatus status) {
        Invitation invitation = INVITATION_DAO.getById(id).orElseThrow();
        invitation.setStatus(status);

        saveOrUpdate(invitation);
    }

    @Override
    @Transactional
    public void saveInvitation(Invitation invitation) {
        INVITATION_DAO.saveOrUpdate(invitation);
    }
}
