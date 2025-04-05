package com.example.cliniktour.controller.admin;

import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.ServiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {


    private final ClinicService clinicService;
    private final ServiceService serviceService;
    private final DepartmentService departmentService;

    public MaintenanceController(ClinicService clinicService , ServiceService serviceService, DepartmentService departmentService) {
        this.clinicService = clinicService;
        this.serviceService= serviceService;
        this.departmentService = departmentService;
    }


    @GetMapping("/clean-descriptions")
    public ResponseEntity<String> cleanDescriptions() {
        clinicService.cleanAllClinicDescriptions();
        return ResponseEntity.ok("Все описания клиник очищены от лишнего форматирования");
    }

    @GetMapping("/clean-services-descriptions")
    public ResponseEntity<String> cleanServicesDescriptions() {
        serviceService.cleanAllServicesDescriptions();
        return ResponseEntity.ok("Все описания сервисов очищены от лишнего форматирования");
    }

    @GetMapping("/clean-department-descriptions")
    public ResponseEntity<String> cleanServicesDepartment() {
       departmentService .cleanAllServicesDescriptions();
        return ResponseEntity.ok("Все описания Department очищены от лишнего форматирования");
    }

}