package com.jegan.blogapplication.service;

import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PostService {
    List<Post> findAll();

    void save(Post post);

    Post findById(int id);

    void deleteById(int deleteId);

    Page<Post> findAll(Pageable pageable);

    List<Post> searchPosts(String searchQuery);

    List<String> findAllAuthors();

    Page<Post> findPostsByAuthors(List<String> filterByAuthors, Pageable pageable);

    Page<Post> findPostsByPublishedDate(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<Post> findPostsByAuthorsList(List<String> filterByAuthors, Pageable pageable, List<Post> searchedPosts);

    Page<Post> findPostsByPublishedDateFromList(LocalDateTime filterByPublishedAtFrom, LocalDateTime filterByPublishedAtTo, Pageable pageable, List<Post> searchedPosts);

    List<Post> findAllDrafts();

    List<String> findAllAuthorsFromSearched(List<Post> searchedPosts);

    List<Post> findPostsByAuthorId(User user);
}
