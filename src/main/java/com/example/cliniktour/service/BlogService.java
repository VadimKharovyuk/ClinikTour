package com.example.cliniktour.service;

import com.example.cliniktour.dto.blog.BlogPostCreateDto;
import com.example.cliniktour.dto.blog.BlogPostDetailDto;
import com.example.cliniktour.dto.blog.BlogPostListDto;
import com.example.cliniktour.dto.blog.BlogPostPageDto;
import com.example.cliniktour.mapper.BlogMapper;
import com.example.cliniktour.model.BlogPost;
import com.example.cliniktour.repository.BlogPostRepository;
import com.example.cliniktour.util.ImgurService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для работы с блогом
 */
@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogPostRepository blogPostRepository;
    private final BlogMapper blogMapper;
    private final ImgurService imgurService;

    /**
     * Получить все посты с пагинацией
     *
     * @param pageable объект пагинации
     * @return страница с DTO блог-постов для списка
     */
    public Page<BlogPostListDto> getAllPosts(Pageable pageable) {
        return blogPostRepository.findAll(pageable)
                .map(blogMapper::toListDto);
    }

    /**
     * Получить пост по ID
     *
     * @param id идентификатор поста
     * @return DTO блог-поста для детального просмотра
     */
    public BlogPostDetailDto getPostById(Long id) {
        BlogPost post = findPostById(id);
        return blogMapper.toDetailDto(post);
    }

    /**
     * Получить DTO для редактирования поста
     *
     * @param id идентификатор поста
     * @return DTO блог-поста для создания/обновления
     */
    public BlogPostCreateDto getPostForEdit(Long id) {
        BlogPost post = findPostById(id);
        return blogMapper.toCreateDto(post);
    }

    /**
     * Создать новый пост
     *
     * @param dto DTO для создания блог-поста
     * @param imageFile загружаемое изображение (может быть null)
     * @return идентификатор созданного поста
     * @throws IOException если произошла ошибка при загрузке изображения
     */
    @Transactional
    public Long createPost(BlogPostCreateDto dto, MultipartFile imageFile) throws IOException {
        BlogPost post = blogMapper.toEntity(dto, imageFile);
        BlogPost savedPost = blogPostRepository.save(post);
        return savedPost.getId();
    }

    /**
     * Обновить существующий пост
     *
     * @param id идентификатор поста
     * @param dto DTO с новыми данными
     * @param imageFile новое изображение (может быть null)
     * @throws IOException если произошла ошибка при загрузке изображения
     */
    @Transactional
    public void updatePost(Long id, BlogPostCreateDto dto, MultipartFile imageFile) throws IOException {
        BlogPost post = findPostById(id);
        blogMapper.updateEntity(post, dto, imageFile);
        blogPostRepository.save(post);
    }

    /**
     * Удалить пост
     *
     * @param id идентификатор поста
     */
    @Transactional
    public void deletePost(Long id) {
        BlogPost post = findPostById(id);

        // Удаляем изображение из Imgur, если оно существует
        if (post.getImageDeleteHash() != null && !post.getImageDeleteHash().isEmpty()) {
            imgurService.deleteImage(post.getImageDeleteHash());
        }

        blogPostRepository.delete(post);
    }

    /**
     * Загрузить изображение для использования в редакторе
     *
     * @param file загружаемое изображение
     * @return URL загруженного изображения
     * @throws IOException если произошла ошибка при загрузке изображения
     */
    public String uploadImage(MultipartFile file) throws IOException {
        ImgurService.UploadResult uploadResult = imgurService.uploadImage(file);
        return uploadResult.getUrl();
    }

    /**
     * Получить блог-посты для публичной части сайта с пагинацией
     *
     * @param pageable объект пагинации
     * @return DTO с пагинированным списком блог-постов
     */
    public BlogPostPageDto getPublicPosts(Pageable pageable) {
        Page<BlogPost> postsPage = blogPostRepository.findAll(pageable);

        List<BlogPostListDto> posts = postsPage.getContent().stream()
                .map(blogMapper::toListDto)
                .collect(Collectors.toList());

        return new BlogPostPageDto(
                posts,
                postsPage.getNumber(),
                postsPage.getTotalPages(),
                postsPage.getTotalElements()
        );
    }

    /**
     * Поиск блог-постов по заголовку
     *
     * @param title часть заголовка для поиска
     * @param pageable объект пагинации
     * @return страница с DTO блог-постов для списка
     */
    public Page<BlogPostListDto> searchPostsByTitle(String title, Pageable pageable) {
        return blogPostRepository.findByTitleContainingIgnoreCase(title, pageable)
                .map(blogMapper::toListDto);
    }

    /**
     * Поиск блог-постов по содержимому
     *
     * @param content часть содержимого для поиска
     * @param pageable объект пагинации
     * @return страница с DTO блог-постов для списка
     */
    public Page<BlogPostListDto> searchPostsByContent(String content, Pageable pageable) {
        return blogPostRepository.findByContentContainingIgnoreCase(content, pageable)
                .map(blogMapper::toListDto);
    }

    /**
     * Вспомогательный метод для поиска поста по ID с выбросом исключения, если пост не найден
     *
     * @param id идентификатор поста
     * @return найденная сущность BlogPost
     * @throws RuntimeException если пост не найден
     */
    private BlogPost findPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Блог-пост не найден с ID: " + id));
    }
}