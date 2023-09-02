package com.jegan.blogapplication.controller;

import com.jegan.blogapplication.entity.Comment;
import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.service.CommentService;
import com.jegan.blogapplication.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private CommentService commentService;
    private PostService postService;


    @Autowired
    public CommentController(CommentService commentService , PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/save")
    public String saveComment(@ModelAttribute("comment") Comment comment, @RequestParam("postId") int postId) {
        Post post=postService.findById(postId);
        comment.setPost(post);
        commentService.save(comment);
        return "redirect:/blogs/showFormForViewPost?postId="+postId;
    }

    @GetMapping("/showFormForUpdate")
    public String viewComment(@RequestParam("commentId") int commentId,@RequestParam("postId") int postId ,Model model) {
        Comment comment=commentService.findById(commentId);
        model.addAttribute("comment",comment);
        model.addAttribute("post",postService.findById(postId));
        return "blogs/comments/update-comment";
    }

    @PostMapping("/update")
    public String updateComment(@ModelAttribute("comment") Comment comment, @RequestParam("postId") int postId) {
        Comment originalComment = commentService.findById(comment.getId());
        if (originalComment != null) {
            originalComment.setName(comment.getName());
            originalComment.setEmail(comment.getEmail());
            originalComment.setTheComment(comment.getTheComment());
            commentService.save(originalComment);
        }
        return "redirect:/blogs/showFormForViewPost?postId=" + postId;
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("commentId") int deleteId,@RequestParam("postId") int postId) {
        commentService.deleteById(deleteId);
        return "redirect:/blogs/showFormForViewPost?postId="+postId;
    }
}
