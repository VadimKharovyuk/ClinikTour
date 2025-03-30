package com.example.cliniktour.service;

import com.example.cliniktour.model.Admin;
import com.example.cliniktour.repository.AdminRepository;
import com.example.cliniktour.util.SimplePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final SimplePasswordEncoder passwordEncoder;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = new SimplePasswordEncoder();
    }

    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    public Optional<Admin> findById(Long id) {
        return adminRepository.findById(id);
    }

    public Admin save(Admin admin) {
        if (admin.getId() == null || (admin.getPassword() != null && !admin.getPassword().isEmpty())) {
            // Если это новая запись или пароль был изменен
            admin.setPassword(passwordEncoder.encode(admin.getPassword()));
        } else {
            // Если это обновление, и пароль не был изменен (пустое поле)
            adminRepository.findById(admin.getId()).ifPresent(existingAdmin -> {
                admin.setPassword(existingAdmin.getPassword());
            });
        }
        return adminRepository.save(admin);
    }

    public void deleteById(Long id) {
        adminRepository.deleteById(id);
    }

    public boolean authenticate(String username, String password) {
        Optional<Admin> adminOpt = adminRepository.findByUsername(username);
        return adminOpt.isPresent() &&
                adminOpt.get().isActive() &&
                passwordEncoder.matches(password, adminOpt.get().getPassword());
    }
}