package me.mmtr.vitalis.service.interfaces;
import me.mmtr.vitalis.data.Invitation;

import java.util.List;
import java.util.Optional;

public interface InvitationService {
    void saveOrUpdate(Invitation invitation);

    Optional<Invitation> getById(Long id);

    List<Invitation> getAll();

    void delete(Long id);

    void saveInvitation(Invitation invitation);
}
