package me.mmtr.vitalis.service;

import jakarta.transaction.Transactional;
import me.mmtr.vitalis.data.Clinic;
import me.mmtr.vitalis.data.User;
import me.mmtr.vitalis.repository.dao.interfaces.IClinicDAO;
import me.mmtr.vitalis.repository.UserRepository;
import me.mmtr.vitalis.service.interfaces.ClinicService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClinicServiceImpl implements ClinicService {

    private final IClinicDAO CLINIC_DAO;
    private final UserRepository USER_REPOSITORY;

    public ClinicServiceImpl(IClinicDAO clinicDAO, UserRepository USER_REPOSITORY) {
        this.CLINIC_DAO = clinicDAO;
        this.USER_REPOSITORY = USER_REPOSITORY;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Clinic clinic) {
        CLINIC_DAO.saveOrUpdate(clinic);
    }

    @Override
    @Transactional
    public Optional<Clinic> getById(Long id) {
        return CLINIC_DAO.getById(id);
    }


    //TODO: Extract method from add/remove employee to eliminate duplicate code
    @Override
    public void addEmployeeToClinic(Long clinicId, Long employeeId) {
        Clinic clinic = CLINIC_DAO.getById(clinicId).orElseThrow(() ->
                new RuntimeException("Clinic not found"));

        User user = USER_REPOSITORY.findById(employeeId).orElseThrow(() ->
                new RuntimeException("User not found"));

        clinic.addEmployee(user);
        CLINIC_DAO.saveOrUpdate(clinic);
        USER_REPOSITORY.save(user);
    }

    @Override
    public void removeEmployeeFromClinic(Long clinicId, Long employeeId) {
        Clinic clinic = CLINIC_DAO.getById(clinicId).orElseThrow(() ->
                new RuntimeException("Clinic not found"));

        User user = USER_REPOSITORY.findById(employeeId).orElseThrow(() ->
                new RuntimeException("User not found"));

        clinic.removeEmployee(user);
        CLINIC_DAO.saveOrUpdate(clinic);
        USER_REPOSITORY.save(user);
    }


    @Override
    @Transactional
    public List<Clinic> getAll() {
        return CLINIC_DAO.getAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        CLINIC_DAO.delete(id);
    }
}
