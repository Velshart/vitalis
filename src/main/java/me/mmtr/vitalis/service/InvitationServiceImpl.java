package me.mmtr.vitalis.service;


import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Invitation;
import me.mmtr.vitalis.repository.dao.interfaces.IInvitationDAO;
import me.mmtr.vitalis.service.interfaces.InvitationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InvitationServiceImpl implements InvitationService {

    private final IInvitationDAO invitationDAO;

    public InvitationServiceImpl(IInvitationDAO invitationDAO) {
        this.invitationDAO = invitationDAO;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Invitation invitation) {
        invitationDAO.saveOrUpdate(invitation);
    }

    @Override
    @Transactional
    public Optional<Invitation> getById(Long id) {
        return invitationDAO.getById(id);
    }

    @Override
    @Transactional
    public List<Invitation> getAll() {
        return invitationDAO.getAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        invitationDAO.delete(id);
    }

    @Override
    @Transactional
    public void deleteByClinicId(Long clinicId) {
        List<Invitation> invitationsToDelete = invitationDAO.getAll()
                .stream()
                .filter(invitation -> invitation.getClinic().getId().equals(clinicId))
                .toList();

        invitationsToDelete.forEach(invitation -> delete(invitation.getId()));
    }

    @Override
    @Transactional
    public void saveInvitation(Invitation invitation) {
        invitationDAO.saveOrUpdate(invitation);
    }
}
