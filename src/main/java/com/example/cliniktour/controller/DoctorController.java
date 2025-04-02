//package com.example.cliniktour.controller;
//
//import com.example.cliniktour.dto.DoctorDto;
//import com.example.cliniktour.model.Doctor;
//import com.example.cliniktour.service.ClinicService;
//import com.example.cliniktour.service.DepartmentService;
//import com.example.cliniktour.service.DoctorService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@Controller
//@RequestMapping("/doctors")
//@RequiredArgsConstructor
//public class DoctorController {
//
//    private final DoctorService doctorService;
//    private final ClinicService clinicService;
//    private final DepartmentService departmentService;
//
//    /**
//     * Отображение списка всех докторов с пагинацией и фильтрацией
//     */
//    @GetMapping
//    public String listDoctors(
//            @RequestParam(required = false) String search,
//            @RequestParam(required = false) Long clinicId,
//            @RequestParam(required = false) Long departmentId,
//            @RequestParam(required = false) String specialization,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            @RequestParam(defaultValue = "fullName") String sort,
//            Model model) {
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
//
//        // Если указан поисковый запрос, используем поиск по имени или специализации
//        if (search != null && !search.trim().isEmpty()) {
//            List<DoctorDto> doctorsByName = doctorService.searchDoctorsByName(search);
//            List<DoctorDto> doctorsBySpec = doctorService.searchDoctorsBySpecialization(search);
//
//            // Объединяем результаты поиска (с удалением дубликатов)
//            doctorsByName.removeAll(doctorsBySpec);
//            doctorsByName.addAll(doctorsBySpec);
//
//            model.addAttribute("doctors", doctorsByName);
//            model.addAttribute("search", search);
//        }
//        // Если указаны фильтры, применяем их
//        else if (clinicId != null || departmentId != null || (specialization != null && !specialization.isEmpty())) {
//            // Здесь должна быть логика фильтрации
//            // Пока используем стандартную пагинацию
//            Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);
//            model.addAttribute("doctorPage", doctorPage);
//
//            if (clinicId != null) model.addAttribute("clinicId", clinicId);
//            if (departmentId != null) model.addAttribute("departmentId", departmentId);
//            if (specialization != null) model.addAttribute("specialization", specialization);
//        }
//        // В остальных случаях показываем все записи с пагинацией
//        else {
//            Page<DoctorDto> doctorPage = doctorService.getDoctorPage(pageable);
//            model.addAttribute("doctorPage", doctorPage);
//        }
//
//        // Общие параметры для всех вариантов
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//        model.addAttribute("sortField", sort);
//
//        // Данные для фильтров
//        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
//        model.addAttribute("departments", departmentService.getAllDepartments());
//
//        return "doctors/list";
//    }
//
//    /**
//     * Отображение детальной информации о докторе
//     */
//    @GetMapping("/{id}")
//    public String viewDoctor(@PathVariable Long id, Model model) {
//        doctorService.getDoctorById(id).ifPresent(doctor -> {
//            model.addAttribute("doctor", doctor);
//
//            // Добавляем информацию о клинике и отделении
//            if (doctor.getClinic() != null) {
//                model.addAttribute("clinic", doctor.getClinic());
//            }
//
//            if (doctor.getDepartment() != null) {
//                model.addAttribute("department", doctor.getDepartment());
//
//                // Можно также добавить других докторов этой же специализации
//                List<Doctor> similarDoctors = doctorService.getDoctorsByDepartmentId(doctor.getDepartment().getId());
//                similarDoctors.removeIf(d -> d.getId().equals(doctor.getId())); // Удаляем текущего доктора
//                model.addAttribute("similarDoctors", similarDoctors.stream().limit(3).toList()); // Ограничиваем количество
//            }
//        });
//
//        return "doctors/view";
//    }
//
//    /**
//     * Фильтрация докторов по клинике
//     */
//    @GetMapping("/by-clinic/{clinicId}")
//    public String doctorsByClinic(
//            @PathVariable Long clinicId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            Model model) {
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        // Получаем список докторов по клинике
//        List<Doctor> doctors = doctorService.getDoctorsByClinicId(clinicId);
//        model.addAttribute("doctors", doctors);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//
//        // Получаем информацию о клинике
//        clinicService.getClinicById(clinicId).ifPresent(clinic -> {
//            model.addAttribute("clinic", clinic);
//        });
//
//        return "doctors/by-clinic";
//    }
//
//    /**
//     * Фильтрация докторов по отделению
//     */
//    @GetMapping("/by-department/{departmentId}")
//    public String doctorsByDepartment(
//            @PathVariable Long departmentId,
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "9") int size,
//            Model model) {
//
//        Pageable pageable = PageRequest.of(page, size);
//
//        // Получаем список докторов по отделению
//        List<Doctor> doctors = doctorService.getDoctorsByDepartmentId(departmentId);
//        model.addAttribute("doctors", doctors);
//        model.addAttribute("currentPage", page);
//        model.addAttribute("pageSize", size);
//
//        // Получаем информацию об отделении
//        departmentService.getDepartmentById(departmentId).ifPresent(department -> {
//            model.addAttribute("department", department);
//        });
//
//        return "doctors/by-department";
//    }
//}

package com.example.cliniktour.controller;

import com.example.cliniktour.dto.CreateDoctorDto;
import com.example.cliniktour.dto.DoctorDetailDto;
import com.example.cliniktour.dto.DoctorListDto;
import com.example.cliniktour.model.Doctor;
import com.example.cliniktour.service.ClinicService;
import com.example.cliniktour.service.DepartmentService;
import com.example.cliniktour.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {

    private final DoctorService doctorService;
    private final ClinicService clinicService;
    private final DepartmentService departmentService;

    /**
     * Отображение списка всех докторов с пагинацией и фильтрацией
     */
    @GetMapping
    public String listDoctors(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Long clinicId,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(required = false) String specialization,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "fullName") String sort,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));

        // Если указан поисковый запрос, используем поиск по имени или специализации
        if (search != null && !search.trim().isEmpty()) {
            List<DoctorListDto> doctorsByName = doctorService.searchDoctorsByName(search);
            List<DoctorListDto> doctorsBySpec = doctorService.searchDoctorsBySpecialization(search);

            // Объединяем результаты поиска (с удалением дубликатов)
            doctorsByName.removeAll(doctorsBySpec);
            doctorsByName.addAll(doctorsBySpec);

            model.addAttribute("doctors", doctorsByName);
            model.addAttribute("search", search);
        }
        // Если указаны фильтры, применяем их
        else if (clinicId != null || departmentId != null || (specialization != null && !specialization.isEmpty())) {
            // Здесь должна быть логика фильтрации
            // Пока используем стандартную пагинацию
            Page<DoctorListDto> doctorPage = doctorService.getDoctorListPage(pageable);
            model.addAttribute("doctorPage", doctorPage);

            if (clinicId != null) model.addAttribute("clinicId", clinicId);
            if (departmentId != null) model.addAttribute("departmentId", departmentId);
            if (specialization != null) model.addAttribute("specialization", specialization);
        }
        // В остальных случаях показываем все записи с пагинацией
        else {
            Page<DoctorListDto> doctorPage = doctorService.getDoctorListPage(pageable);
            model.addAttribute("doctorPage", doctorPage);
        }

        // Общие параметры для всех вариантов
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortField", sort);

        // Данные для фильтров
        model.addAttribute("clinics", clinicService.getAllClinics(PageRequest.of(0, 100)).getContent());
        model.addAttribute("departments", departmentService.getAllDepartments());

        return "doctors/list";
    }

    /**
     * Отображение детальной информации о докторе
     */
    @GetMapping("/{id}")
    public String viewDoctor(@PathVariable Long id, Model model) {
        doctorService.getDoctorDetailById(id).ifPresent(doctorDetail -> {
            model.addAttribute("doctor", doctorDetail);

            // Добавляем информацию о клинике
            if (doctorDetail.getClinicId() != null) {
                clinicService.getClinicById(doctorDetail.getClinicId())
                        .ifPresent(clinic -> model.addAttribute("clinic", clinic));
            }

            // Добавляем информацию об отделении
            if (doctorDetail.getDepartmentId() != null) {
                departmentService.getDepartmentById(doctorDetail.getDepartmentId())
                        .ifPresent(department -> {
                            model.addAttribute("department", department);

                            // Добавляем других докторов этой же специализации
                            List<DoctorListDto> similarDoctors = doctorService.getDoctorListDtosByDepartmentId(doctorDetail.getDepartmentId());
                            // Удаляем текущего доктора
                            similarDoctors.removeIf(d -> d.getId().equals(doctorDetail.getId()));
                            // Ограничиваем количество
                            model.addAttribute("similarDoctors", similarDoctors.stream().limit(3).toList());
                        });
            }
        });

        return "doctors/view";
    }

    /**
     * Фильтрация докторов по клинике
     */
    @GetMapping("/by-clinic/{clinicId}")
    public String doctorsByClinic(
            @PathVariable Long clinicId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Получаем список докторов по клинике
        List<DoctorListDto> doctors = doctorService.getDoctorListDtosByClinicId(clinicId);
        model.addAttribute("doctors", doctors);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        // Получаем информацию о клинике
        clinicService.getClinicById(clinicId).ifPresent(clinic -> {
            model.addAttribute("clinic", clinic);
        });

        return "doctors/by-clinic";
    }

    /**
     * Фильтрация докторов по отделению
     */
    @GetMapping("/by-department/{departmentId}")
    public String doctorsByDepartment(
            @PathVariable Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size);

        // Получаем список докторов по отделению
        List<DoctorListDto> doctors = doctorService.getDoctorListDtosByDepartmentId(departmentId);
        model.addAttribute("doctors", doctors);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        // Получаем информацию об отделении
        departmentService.getDepartmentById(departmentId).ifPresent(department -> {
            model.addAttribute("department", department);
        });

        return "doctors/by-department";
    }
}