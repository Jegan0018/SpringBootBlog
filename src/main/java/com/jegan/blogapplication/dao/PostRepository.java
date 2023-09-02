package com.jegan.blogapplication.dao;

import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post,Integer> {

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN p.tags t " +
            "WHERE (LOWER(p.title) LIKE %:query% " +
            "OR LOWER(p.content) LIKE %:query% " +
            "OR LOWER(p.author) LIKE %:query% " +
            "OR LOWER(t.name) LIKE %:query%) " +
            "AND p.isPublished = true")
    List<Post> searchPosts(@Param("query") String searchQuery);

    @Query("SELECT p FROM Post p WHERE p.isPublished=true")
    Page<Post> findAll(Pageable pageable);

    @Query("SELECT DISTINCT p.author FROM Post p")
    List<String> findAllAuthors();

    @Query("SELECT DISTINCT p FROM Post p WHERE p.author IN :authors")
    Page<Post> findPostsByAuthors(@Param("authors") List<String> authors, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.publishedAt BETWEEN (:publishedAtFrom) AND (:publishedAtTo) AND p.isPublished=true")
    Page<Post> findPostsByPublishedDate(@Param("publishedAtFrom") LocalDateTime from, @Param("publishedAtTo") LocalDateTime to, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p IN :searchedPosts AND p.author IN :filterByAuthors AND p.isPublished=true")
    Page<Post> findPostsByAuthorsList(List<String> filterByAuthors, Pageable pageable, List<Post> searchedPosts);

    @Query("SELECT p FROM Post p WHERE p IN :searchedPosts AND p.publishedAt BETWEEN :filterByPublishedAtFrom AND :filterByPublishedAtTo AND p.isPublished=true")
    Page<Post> findPostsByPublishedDateFromList(LocalDateTime filterByPublishedAtFrom, LocalDateTime filterByPublishedAtTo, Pageable pageable, List<Post> searchedPosts);

    @Query("SELECT DISTINCT p.author FROM Post p WHERE p IN :searchedPosts")
    List<String> findAuthorsFromSearched(List<Post> searchedPosts);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.isPublished=true")
    List<Post> findPublishedPostsByAuthorId(User user);

    @Query("SELECT p FROM Post p WHERE p.user = :user AND p.isPublished=false")
    List<Post> findDraftPostsByAuthorId(User user);
}
