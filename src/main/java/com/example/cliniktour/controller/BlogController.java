package com.example.cliniktour.controller;
import com.example.cliniktour.dto.blog.BlogPostDetailDto;
import com.example.cliniktour.dto.blog.BlogPostPageDto;
import com.example.cliniktour.enums.BlogPostType;
import com.example.cliniktour.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для отображения блога на клиентской части сайта
 */
@Controller
@RequestMapping("/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    /**
     * Добавление списка типов постов в модель для форм
     */
    @ModelAttribute("postTypes")
    public BlogPostType[] getPostTypes() {
        return BlogPostType.values();
    }

    /**
     * Отображение списка всех статей с пагинацией
     */
    @GetMapping
    public String listBlogPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) BlogPostType postType,
            Model model) {

        BlogPostPageDto pageDto;

        if (postType != null) {
            // Если указан тип поста, используем фильтрацию
            pageDto = blogService.getPublicPostsByType(
                    postType,
                    PageRequest.of(page, size, Sort.by("createdAt").descending())
            );
            model.addAttribute("selectedType", postType);
        } else {
            // Иначе получаем все посты
            pageDto = blogService.getPublicPosts(
                    PageRequest.of(page, size, Sort.by("createdAt").descending())
            );
        }

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("currentPage", pageDto.getCurrentPage());
        model.addAttribute("totalPages", pageDto.getTotalPages());

        return "blog/list";
    }

    /**
     * Отображение статей определенного типа с пагинацией
     */
    @GetMapping("/type/{postType}")
    public String listPostsByType(
            @PathVariable BlogPostType postType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        BlogPostPageDto pageDto = blogService.getPublicPostsByType(
                postType,
                PageRequest.of(page, size, Sort.by("createdAt").descending())
        );

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("currentPage", pageDto.getCurrentPage());
        model.addAttribute("totalPages", pageDto.getTotalPages());
        model.addAttribute("selectedType", postType);
        model.addAttribute("typeName", postType.getDisplayName());

        return "blog/list";
    }

    /**
     * Просмотр отдельной статьи по ID
     */
    @GetMapping("/{id}")
    public String viewBlogPost(@PathVariable Long id, Model model) {
        try {
            BlogPostDetailDto post = blogService.getPostById(id);
            model.addAttribute("post", post);

            // Добавим рекомендованные статьи того же типа, если тип указан
            if (post.getPostType() != null) {
                BlogPostPageDto relatedPosts = blogService.getPublicPostsByType(
                        post.getPostType(),
                        PageRequest.of(0, 3, Sort.by("createdAt").descending())
                );
                model.addAttribute("relatedPosts", relatedPosts.getPosts());
            }

            // Добавим последние статьи
            BlogPostPageDto recentPosts = blogService.getPublicPosts(
                    PageRequest.of(0, 3, Sort.by("createdAt").descending())
            );
            model.addAttribute("recentPosts", recentPosts.getPosts());

            return "blog/view";
        } catch (Exception e) {
            return "redirect:/blog";
        }
    }

    /**
     * Поиск статей
     */
    @GetMapping("/search")
    public String searchBlogPosts(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            Model model) {

        if (query == null || query.isEmpty()) {
            return "redirect:/blog";
        }

        // Поиск по заголовку
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        BlogPostPageDto pageDto = new BlogPostPageDto();

        // Используем поиск по заголовку из сервиса
        pageDto.setPosts(blogService.searchPostsByTitle(query, pageRequest).getContent());
        pageDto.setCurrentPage(page);
        pageDto.setTotalPages(blogService.searchPostsByTitle(query, pageRequest).getTotalPages());
        pageDto.setTotalElements(blogService.searchPostsByTitle(query, pageRequest).getTotalElements());

        model.addAttribute("pageDto", pageDto);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageDto.getTotalPages());
        model.addAttribute("query", query);

        return "blog/list";
    }

    /**
     * Главная страница блога (альтернативный маршрут)
     */
    @GetMapping("/index")
    public String blogIndex(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(required = false) BlogPostType postType,
            Model model) {
        return listBlogPosts(page, size, postType, model);
    }
}