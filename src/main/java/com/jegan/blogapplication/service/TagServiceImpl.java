package com.jegan.blogapplication.service;

import com.jegan.blogapplication.dao.TagRepository;
import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagServiceImpl implements TagService{

    private TagRepository tagRepository;

    public TagServiceImpl(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Override
    public Set<Tag> findOrCreateTag(String tagInput) {
        String[] tagNames = tagInput.split(" ");
        Set<Tag> tags = new HashSet<>();
        Tag tag;
        for (String tagName : tagNames) {
            tagName = tagName.trim();
            if (!tagName.isEmpty()) {
                Optional<Tag> existingTag = tagRepository.findByName(tagName);
                if (existingTag.isPresent()) {
                    tag = existingTag.get();
                } else {
                    tag = new Tag();
                    tag.setName(tagName);
                    tagRepository.save(tag);
                }
                tags.add(tag);
            }
        }
        return tags;
    }
    @Override
    public List<String> findAllTags() {
        return tagRepository.findAllTagNames();
    }

    @Override
    public Page<Post> findPostsByTags(List<String> filterByTags, Pageable pageable) {
        return tagRepository.findPostsByTags(filterByTags,pageable);
    }

    @Override
    public Page<Post> findPostsByTagsList(List<String> filterByTags, Pageable pageable, List<Post> searchedPosts) {
        return tagRepository.findPostsByTagsList(filterByTags,pageable,searchedPosts);
    }

    @Override
    public List<String> findTagsFromSearched(List<Post> searchedPosts) {
        return tagRepository.findTagsFromSearchedList(searchedPosts);
    }

    public void deleteTagIfNotUsed(Tag tag) {
        if (!tag.getPosts().isEmpty()) {
            return;
        }
        tagRepository.delete(tag);
    }
}
