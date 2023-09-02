package com.jegan.blogapplication.service;

import com.jegan.blogapplication.dao.PostRepository;
import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public void save(Post post) {
        postRepository.save(post);
    }

    @Override
    public Post findById(int id) {
        Optional<Post> result=postRepository.findById(id);
        Post post=null;
        if (result.isPresent()) {
            post=result.get();
        } else {
            throw new RuntimeException("Invalid Id");
        }
        return post;
    }

    @Override
    public Post deleteById(int deleteId) {
        Post post = postRepository.findById(deleteId).orElse(null);
        if (post != null) {
            postRepository.delete(post);
        }
        return post;
    }

    @Override
    public Page<Post> findAll(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public List<Post> searchPosts(String searchQuery) {
        return postRepository.searchPosts(searchQuery);
    }

    @Override
    public List<String> findAllAuthors() {
        return postRepository.findAllAuthors();
    }

    @Override
    public Page<Post> findPostsByAuthors(List<String> filterByAuthors, Pageable pageable) {
        return postRepository.findPostsByAuthors(filterByAuthors,pageable);
    }

    @Override
    public Page<Post> findPostsByPublishedDate(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return postRepository.findPostsByPublishedDate(from,to,pageable);
    }

    @Override
    public Page<Post> findPostsByAuthorsList(List<String> filterByAuthors, Pageable pageable, List<Post> searchedPosts) {
        return postRepository.findPostsByAuthorsList(filterByAuthors,pageable,searchedPosts);
    }

    @Override
    public Page<Post> findPostsByPublishedDateFromList(LocalDateTime filterByPublishedAtFrom, LocalDateTime filterByPublishedAtTo, Pageable pageable, List<Post> searchedPosts) {
        return postRepository.findPostsByPublishedDateFromList(filterByPublishedAtFrom,filterByPublishedAtTo,pageable,searchedPosts);
    }

    @Override
    public List<String> findAllAuthorsFromSearched(List<Post> searchedPosts) {
        return postRepository.findAuthorsFromSearched(searchedPosts);
    }

    @Override
    public List<Post> findPublishedPostsByAuthorId(User user) {
        return postRepository.findPublishedPostsByAuthorId(user);
    }

    @Override
    public List<Post> findDraftPostsByAuthorId(User user) {
        return postRepository.findDraftPostsByAuthorId(user);
    }

}
