package com.example.cliniktour.config;

import com.example.cliniktour.model.Admin;
import com.example.cliniktour.repository.AdminRepository;
import com.example.cliniktour.util.SimplePasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    private final AdminRepository adminRepository;
    private final SimplePasswordEncoder passwordEncoder;

    @Autowired
    public DataInitializer(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = new SimplePasswordEncoder();
    }

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // Проверяем, есть ли уже администраторы в базе
            if (adminRepository.count() == 0) {
                // Если нет, создаем первого администратора
                Admin admin = new Admin();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setActive(true);
                adminRepository.save(admin);

                System.out.println("Создан администратор по умолчанию: admin/admin");
            }
        };
    }
}