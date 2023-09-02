package com.jegan.blogapplication.service;

import com.jegan.blogapplication.entity.Comment;
import com.jegan.blogapplication.entity.Post;

import java.util.List;

public interface CommentService {

    void save(Comment comment);

    List<Comment> findAllByPostId(int postId);

    Comment findById(int commentId);

    void deleteById(int deleteId);
}
