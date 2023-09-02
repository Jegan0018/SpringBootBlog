package com.jegan.blogapplication.dao;

import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag,Integer> {

    Optional<Tag> findByName(String tagName);

    @Query("SELECT DISTINCT t.name FROM Tag t")
    List<String> findAllTagNames();

    @Query("SELECT DISTINCT p FROM Post p " +
            "JOIN p.tags t " +
            "WHERE t.name IN :tags AND p.isPublished=true")
    Page<Post> findPostsByTags(@Param("tags") List<String> tags, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Post p JOIN p.tags t WHERE t.name IN :filterByTags AND p IN :searchedPosts AND p.isPublished=true")
    Page<Post> findPostsByTagsList(List<String> filterByTags, Pageable pageable, List<Post> searchedPosts);

    @Query("SELECT DISTINCT t.name FROM Tag t Join t.posts p WHERE p IN :searchedPosts")
    List<String> findTagsFromSearchedList(List<Post> searchedPosts);
}
