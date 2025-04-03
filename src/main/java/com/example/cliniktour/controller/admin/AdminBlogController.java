package com.example.cliniktour.controller.admin;

import com.example.cliniktour.dto.blog.BlogPostCreateDto;
import com.example.cliniktour.dto.blog.BlogPostDetailDto;
import com.example.cliniktour.dto.blog.BlogPostListDto;
import com.example.cliniktour.dto.blog.BlogPostPageDto;
import com.example.cliniktour.enums.BlogPostType;
import com.example.cliniktour.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Контроллер для управления блогом в административной панели
 */
@Controller
@RequestMapping("/admin/blog")
@RequiredArgsConstructor
public class AdminBlogController {

    private final BlogService blogService;

    /**
     * Добавление списка типов постов в модель для форм
     */
    @ModelAttribute("postTypes")
    public BlogPostType[] getPostTypes() {
        return BlogPostType.values();
    }

    /**
     * Страница со списком статей блога
     */
    @GetMapping
    public String listBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) BlogPostType postType,
            Model model) {

        Page<BlogPostListDto> postsPage;

        if (postType != null) {
            // Если указан тип поста, используем фильтрацию
            postsPage = blogService.getPostsByType(postType,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
            model.addAttribute("selectedType", postType);
        } else {
            // Иначе получаем все посты
            postsPage = blogService.getAllPosts(
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }

        BlogPostPageDto pageDto = new BlogPostPageDto(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements()
        );

        model.addAttribute("pageDto", pageDto);
        return "admin/blog/list";
    }


    /**
     * Страница создания новой статьи
     */
    @GetMapping("/create")
    public String createBlogPostForm(Model model) {
        model.addAttribute("blogPost", new BlogPostCreateDto());
        return "admin/blog/create";
    }

    /**
     * Обработка отправки формы создания статьи
     */
    @PostMapping("/create")
    public String createBlogPost(
            @Valid @ModelAttribute("blogPost") BlogPostCreateDto blogPostDto,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            return "admin/blog/create";
        }

        try {
            Long postId = blogService.createPost(blogPostDto, imageFile);
            redirectAttributes.addFlashAttribute("success", "Статья успешно создана");
            return "redirect:/admin/blog/edit/" + postId;
        } catch (IOException e) {
            model.addAttribute("error", "Ошибка загрузки изображения: " + e.getMessage());
            return "admin/blog/create";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка создания статьи: " + e.getMessage());
            return "admin/blog/create";
        }
    }

    /**
     * Страница редактирования статьи
     */
    @GetMapping("/edit/{id}")
    public String editBlogPostForm(@PathVariable Long id, Model model) {
        try {
            BlogPostCreateDto post = blogService.getPostForEdit(id);
            model.addAttribute("blogPost", post);
            model.addAttribute("postId", id);
            return "admin/blog/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки статьи: " + e.getMessage());
            return "redirect:/admin/blog";
        }
    }

    /**
     * Обработка отправки формы редактирования статьи
     */
    @PostMapping("/edit/{id}")
    public String updateBlogPost(
            @PathVariable Long id,
            @Valid @ModelAttribute("blogPost") BlogPostCreateDto blogPostDto,
            BindingResult result,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("postId", id);
            return "admin/blog/edit";
        }

        try {
            blogService.updatePost(id, blogPostDto, imageFile);
            redirectAttributes.addFlashAttribute("success", "Статья успешно обновлена");
            return "redirect:/admin/blog/edit/" + id;
        } catch (IOException e) {
            model.addAttribute("error", "Ошибка загрузки изображения: " + e.getMessage());
            model.addAttribute("postId", id);
            return "admin/blog/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка обновления статьи: " + e.getMessage());
            model.addAttribute("postId", id);
            return "admin/blog/edit";
        }
    }

    /**
     * Удаление статьи
     */
    @PostMapping("/delete/{id}")
    public String deleteBlogPost(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            blogService.deletePost(id);
            redirectAttributes.addFlashAttribute("success", "Статья успешно удалена");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка удаления статьи: " + e.getMessage());
        }

        return "redirect:/admin/blog";
    }

    /**
     * API для загрузки изображения через редактор (например, TinyMCE, CKEditor)
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        try {
            String imageUrl = blogService.uploadImage(file);
            response.put("success", true);
            response.put("url", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("success", false);
            response.put("error", "Ошибка загрузки изображения: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Просмотр деталей статьи
     */
    @GetMapping("/view/{id}")
    public String viewBlogPost(@PathVariable Long id, Model model) {
        try {
            BlogPostDetailDto post = blogService.getPostById(id);
            model.addAttribute("post", post);
            return "admin/blog/view";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка загрузки статьи: " + e.getMessage());
            return "redirect:/admin/blog";
        }
    }

    /**
     * Поиск статей
     */
    @GetMapping("/search")
    public String searchBlogPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String searchBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<BlogPostListDto> postsPage;

        if (searchBy.equals("content")) {
            postsPage = blogService.searchPostsByContent(query,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        } else {
            // По умолчанию поиск по заголовку
            postsPage = blogService.searchPostsByTitle(query,
                    PageRequest.of(page, size, Sort.by("createdAt").descending()));
        }

        BlogPostPageDto pageDto = new BlogPostPageDto(
                postsPage.getContent(),
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements()
        );

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("query", query);
        model.addAttribute("searchBy", searchBy);

        return "admin/blog/list";
    }
}
