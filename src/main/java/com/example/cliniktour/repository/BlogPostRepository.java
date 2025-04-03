package com.example.cliniktour.repository;

import com.example.cliniktour.enums.BlogPostType;
import com.example.cliniktour.model.BlogPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {



    /**
     * Получить все блог посты с пагинацией
     */
    Page<BlogPost> findAll(Pageable pageable);

    /**
     * Поиск по заголовку
     */
    Page<BlogPost> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Поиск по содержимому
     */
    Page<BlogPost> findByContentContainingIgnoreCase(String content, Pageable pageable);

    Page<BlogPost> findByPostType(BlogPostType postType, Pageable pageable);

}