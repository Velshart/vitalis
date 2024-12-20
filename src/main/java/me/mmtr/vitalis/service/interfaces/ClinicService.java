package me.mmtr.vitalis.service.interfaces;

import me.mmtr.vitalis.data.Clinic;

import java.util.List;
import java.util.Optional;

public interface ClinicService {
    void saveOrUpdate(Clinic clinic);

    Optional<Clinic> getById(Long id);

    void addEmployeeToClinic(Long clinicId, Long employeeId);

    void removeEmployeeFromClinic(Long clinicId, Long employeeId);

    List<Clinic> getAll();

    void delete(Long id);
}
