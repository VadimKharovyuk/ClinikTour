package com.example.cliniktour.controller;
import com.example.cliniktour.model.Admin;
import com.example.cliniktour.service.AdminService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminAuthController {

    private final AdminService adminService;

    @Value("${admin.username:admin}")
    private String ADMIN_USERNAME;

    @Value("${admin.password:secure_password}")
    private String ADMIN_PASSWORD;

    private static final String ADMIN_AUTH_KEY = "adminAuthenticated";

    @Autowired
    public AdminAuthController(AdminService adminService) {
        this.adminService = adminService;
    }

    /**
     * Страница входа в админ-панель
     */
    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // Если администратор уже авторизован, перенаправляем на дашборд
        if (Boolean.TRUE.equals(session.getAttribute(ADMIN_AUTH_KEY))) {
            return "redirect:/admin/dashboard";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

        // Метод 1: Проверка учетных данных из property-файла
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            // Успешная аутентификация - устанавливаем флаг в сессии
            session.setAttribute(ADMIN_AUTH_KEY, true);
            return "redirect:/admin/dashboard";
        }

        // Метод 2: Проверка учетных данных из базы данных
        if (adminService.authenticate(username, password)) {
            session.setAttribute(ADMIN_AUTH_KEY, true);
            return "redirect:/admin/dashboard";
        }

        // Неудачная аутентификация
        redirectAttributes.addFlashAttribute("error", "Неверное имя пользователя или пароль");
        return "redirect:/admin/login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Удаляем флаг аутентификации из сессии
        session.removeAttribute(ADMIN_AUTH_KEY);
        return "redirect:/admin/login";
    }

    // Дополнительные методы для управления администраторами

    @GetMapping("/admins")
    public String listAdmins(Model model) {
        model.addAttribute("admins", adminService.findAll());
        return "admin/admins-list";
    }

    @GetMapping("/admins/add")
    public String addAdminForm(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/admin-form";
    }

    @PostMapping("/admins/save")
    public String saveAdmin(@ModelAttribute Admin admin, RedirectAttributes redirectAttributes) {
        adminService.save(admin);
        redirectAttributes.addFlashAttribute("success", "Администратор успешно сохранен");
        return "redirect:/admin/admins";
    }

    @GetMapping("/admins/edit/{id}")
    public String editAdminForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        adminService.findById(id).ifPresentOrElse(
                admin -> model.addAttribute("admin", admin),
                () -> {
                    redirectAttributes.addFlashAttribute("error", "Администратор не найден");
                    model.addAttribute("admin", new Admin());
                }
        );
        return "admin/admin-form";
    }

    @GetMapping("/admins/delete/{id}")
    public String deleteAdmin(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Администратор успешно удален");
        return "redirect:/admin/admins";
    }
}