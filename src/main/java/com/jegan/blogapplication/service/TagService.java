package com.jegan.blogapplication.service;

import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface TagService {

    Set<Tag> findOrCreateTag(String tagName);

    List<String> findAllTags();

    Page<Post> findPostsByTags(List<String> filterByTags, Pageable pageable);

    Page<Post> findPostsByTagsList(List<String> filterByTags, Pageable pageable, List<Post> searchedPosts);

    List<String> findTagsFromSearched(List<Post> searchedPosts);

    void deleteTagIfNotUsed(Tag tag);
}
