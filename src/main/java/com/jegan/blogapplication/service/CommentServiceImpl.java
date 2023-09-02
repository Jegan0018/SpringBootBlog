package com.jegan.blogapplication.service;

import com.jegan.blogapplication.dao.CommentRepository;
import com.jegan.blogapplication.entity.Comment;
import com.jegan.blogapplication.entity.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentServiceImpl implements CommentService {

    private CommentRepository commentRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    @Override
    public void save(Comment comment) {
        commentRepository.save(comment);
    }

    @Override
    public List<Comment> findAllByPostId(int postId) {
        return commentRepository.findAllByPostId(postId);
    }

    @Override
    public Comment findById(int id) {
        Optional<Comment> result=commentRepository.findById(id);
        Comment comment=null;
        if (result.isPresent()) {
            comment=result.get();
        } else {
            throw new RuntimeException("Invalid Id");
        }
        return comment;
    }

    @Override
    public void deleteById(int deleteId) {
        commentRepository.deleteById(deleteId);
    }

}