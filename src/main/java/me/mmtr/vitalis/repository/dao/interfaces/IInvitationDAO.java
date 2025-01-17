package me.mmtr.vitalis.repository.dao.interfaces;


import me.mmtr.vitalis.data.Invitation;
import java.util.List;
import java.util.Optional;

public interface IInvitationDAO {
    Optional<Invitation> getById(Long id);
    List<Invitation> getAll();
    void saveOrUpdate(Invitation invitation);
    void delete(Long id);
}
