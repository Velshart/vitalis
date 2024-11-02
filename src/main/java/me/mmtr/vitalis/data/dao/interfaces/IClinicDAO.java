package me.mmtr.vitalis.data.dao.interfaces;

import me.mmtr.vitalis.data.Clinic;

import java.util.List;
import java.util.Optional;

public interface IClinicDAO {
    Optional<Clinic> getById(Long id);
    List<Clinic> getAll();
    void saveOrUpdate(Clinic clinic);
    void delete(Long id);
}
