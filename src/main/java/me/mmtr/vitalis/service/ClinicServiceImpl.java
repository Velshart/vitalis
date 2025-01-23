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

    private final IClinicDAO clinicDAO;
    private final UserRepository userRepository;

    public ClinicServiceImpl(IClinicDAO clinicDAO, UserRepository USER_REPOSITORY) {
        this.clinicDAO = clinicDAO;
        this.userRepository = USER_REPOSITORY;
    }

    @Override
    @Transactional
    public void saveOrUpdate(Clinic clinic) {
        clinicDAO.saveOrUpdate(clinic);
    }

    @Override
    @Transactional
    public Optional<Clinic> getById(Long id) {
        return clinicDAO.getById(id);
    }

    @Override
    public void addEmployeeToClinic(Long clinicId, Long employeeId) {
        Clinic clinic = clinicDAO.getById(clinicId).orElseThrow(() ->
                new RuntimeException("Clinic not found"));

        User user = userRepository.findById(employeeId).orElseThrow(() ->
                new RuntimeException("User not found"));

        clinic.addEmployee(user);
        clinicDAO.saveOrUpdate(clinic);
        userRepository.save(user);
    }

    @Override
    public void removeEmployeeFromClinic(Long clinicId, Long employeeId) {
        Clinic clinic = clinicDAO.getById(clinicId).orElseThrow(() ->
                new RuntimeException("Clinic not found"));

        User user = userRepository.findById(employeeId).orElseThrow(() ->
                new RuntimeException("User not found"));

        clinic.removeEmployee(user);
        clinicDAO.saveOrUpdate(clinic);
        userRepository.save(user);
    }


    @Override
    @Transactional
    public List<Clinic> getAll() {
        return clinicDAO.getAll();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        clinicDAO.delete(id);
    }
}
