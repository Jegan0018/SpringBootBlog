package com.jegan.blogapplication.controller;

import com.jegan.blogapplication.entity.Comment;
import com.jegan.blogapplication.entity.Post;
import com.jegan.blogapplication.entity.Tag;
import com.jegan.blogapplication.entity.User;
import com.jegan.blogapplication.service.CommentService;
import com.jegan.blogapplication.service.PostService;
import com.jegan.blogapplication.service.TagService;
import com.jegan.blogapplication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping("/blogs")
public class PostController {

    private PostService postService;

    private CommentService commentService;

    private TagService tagService;

    private UserService userService;


    @Autowired
    public PostController(PostService postService, CommentService commentService, TagService tagService, UserService userService) {
        this.postService = postService;
        this.commentService = commentService;
        this.tagService = tagService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listBlogs(Model model,
                            @RequestParam(defaultValue = "1") int start,
                            @RequestParam(defaultValue = "10") int limit,
                            @RequestParam(required = false, value = "search") String searchQuery,
                            @RequestParam(defaultValue = "desc") String sort,
                            @RequestParam(value = "selectedAuthors", required = false) List<String> filterByAuthors,
                            @RequestParam(value = "selectedTags", required = false) List<String> filterByTags,
                            @RequestParam(value = "publishedAtFrom", required = false) LocalDateTime filterByPublishedAtFrom,
                            @RequestParam(value = "publishedAtTo", required = false) LocalDateTime filterByPublishedAtTo
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        User user = userService.findUserByName(name);
        Sort sorting = Sort.by(Sort.Order.desc("publishedAt"));
        if (sort.equals("asc")) {
            sorting = Sort.by(Sort.Order.asc("publishedAt"));
        }
        Page<Post> postsPage;
        int pageNumber = (start - 1) / limit;
        Pageable pageable = PageRequest.of(pageNumber, limit, sorting);

        postsPage = postService.findAll(pageable);
        boolean isSearched = false;
        List<Post> searchedPosts = new ArrayList<>();

        if (searchQuery != null && !searchQuery.isEmpty()) {
            searchedPosts = postService.searchPosts(searchQuery.toLowerCase());
            isSearched = true;
        } else {
            postsPage = postService.findAll(pageable);
        }

        if (sort.equals("asc")) {
            searchedPosts.sort(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Date::compareTo)));
        } else {
            searchedPosts.sort(Comparator.comparing(Post::getPublishedAt, Comparator.nullsLast(Date::compareTo)).reversed());
        }

        if (isSearched) {
            int startIndex = (pageNumber * limit);
            int endIndex = Math.min(startIndex + limit, searchedPosts.size());
            List<Post> paginatedSearchedPosts = searchedPosts.subList(startIndex, endIndex);
            postsPage = new PageImpl<>(paginatedSearchedPosts, pageable, searchedPosts.size());
        }

        if (filterByAuthors != null && !filterByAuthors.isEmpty()) {
            if (isSearched) {
                postsPage = postService.findPostsByAuthorsList(filterByAuthors, pageable, searchedPosts);
            } else {
                postsPage = postService.findPostsByAuthors(filterByAuthors, pageable);
            }
        }

        if (filterByTags != null && !filterByTags.isEmpty()) {
            if (isSearched) {
                postsPage = tagService.findPostsByTagsList(filterByTags, pageable, searchedPosts);
            } else {
                postsPage = tagService.findPostsByTags(filterByTags, pageable);
            }
        }

        if (filterByPublishedAtTo == null) {
            filterByPublishedAtTo = LocalDateTime.now();
        }

        if (filterByPublishedAtFrom != null) {
            if (isSearched) {
                postsPage = postService.findPostsByPublishedDateFromList(filterByPublishedAtFrom, filterByPublishedAtTo, pageable, searchedPosts);
            } else {
                postsPage = postService.findPostsByPublishedDate(filterByPublishedAtFrom, filterByPublishedAtTo, pageable);
            }
        }

        List<String> authors;
        List<String> tags;
        if (isSearched) {
            authors = postService.findAllAuthorsFromSearched(searchedPosts);
        } else {
            authors = postService.findAllAuthors();
        }

        if (isSearched) {
            tags = tagService.findTagsFromSearched(searchedPosts);
        } else {
            tags = tagService.findAllTags();
        }
        model.addAttribute("postsPage", postsPage);
        model.addAttribute("currentPage", postsPage.getNumber() + 1);
        model.addAttribute("pageSize", limit);
        model.addAttribute("search", searchQuery);
        model.addAttribute("allAuthors", authors);
        model.addAttribute("allTags", tags);
        model.addAttribute("filterByAuthors", filterByAuthors);
        model.addAttribute("filterByTags", filterByTags);
        model.addAttribute("publishedAtFrom", filterByPublishedAtFrom);
        model.addAttribute("publishedAtTo", filterByPublishedAtTo);
        model.addAttribute("user", user);
        return "blogs/list-posts";
    }

    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model model
            , @RequestParam("authorId") int authorId
    ) {
        model.addAttribute("post", new Post());
        model.addAttribute("authorId", authorId);
        return "blogs/post-form";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post, @RequestParam("tagInput") String tagInput, @RequestParam("authorId") int authorId) {
        if (post.getIsPublished()) {
            post.setPublishedAt(Timestamp.valueOf(LocalDateTime.now()));
        }
        Set<Tag> tags = tagService.findOrCreateTag(tagInput);
        post.setTags(tags);
        User user = userService.findUserById(authorId);
        post.setUser(user);
        postService.save(post);
        return "redirect:/blogs/list";
    }

    @GetMapping("/showFormForViewPost")
    public String viewPost(@RequestParam("postId") int postId, Model model, @RequestParam(value = "authorId", required = false) Integer authorId) {
        Post posts = postService.findById(postId);
        List<Comment> comments = commentService.findAllByPostId(postId);
        model.addAttribute("post", posts);
        model.addAttribute("comments", comments);
        model.addAttribute("comment", new Comment());
        if (authorId == null) {
            authorId = 0;
        }
        model.addAttribute("authorId", authorId);
        model.addAttribute("sourcePage", "view");
        return "blogs/view-form";
    }

    @GetMapping("/showFormForViewUser")
    public String showFormForViewUser(@RequestParam("authorId") int authorId, Model model) {
        User user = userService.findUserById(authorId);
        List<Post> publishedPostsByAuthorId = postService.findPublishedPostsByAuthorId(user);
        List<Post> draftPostsByAuthorId = postService.findDraftPostsByAuthorId(user);
        Timestamp publishedAt = Timestamp.valueOf(LocalDateTime.now());
        model.addAttribute("publishedAt", publishedAt);
        model.addAttribute("publishedPosts", publishedPostsByAuthorId);
        model.addAttribute("draftPosts", draftPostsByAuthorId);
        model.addAttribute("user", user);
        model.addAttribute("authorId", authorId);
        model.addAttribute("sourcePage", "drafts");
        return "blogs/view-user";
    }

    @GetMapping("/showFormForEditPost")
    public String editPost(@RequestParam("postId") int postId, @RequestParam("authorId") int authorId, Model model, @RequestParam("sourcePage") String sourcePage) {
        Post posts = postService.findById(postId);
        Set<Tag> tags = posts.getTags();
        StringBuilder tagNamesBuilder = new StringBuilder();
        for (Tag tag : tags) {
            tagNamesBuilder.append(tag.getName()).append(" ");
        }
        String tagNames = tagNamesBuilder.toString().trim();
        model.addAttribute("post", posts);
        model.addAttribute("authorId", authorId);
        model.addAttribute("tagNames", tagNames);
        model.addAttribute("sourcePage", sourcePage);
        return "blogs/edit-form";
    }

    @PostMapping("/update")
    public String updatePost(@ModelAttribute("post") Post post, @RequestParam("tagInput") String tagInput, @RequestParam("sourcePage") String sourcePage, @RequestParam("authorId") int authorId, @RequestParam("postId") int postId) {
        Set<Tag> tags = tagService.findOrCreateTag(tagInput);
        User user = userService.findUserById(authorId);
        post.setId(postId);
        post.setUser(user);
        post.setPublishedAt(post.getPublishedAt());
        post.setTags(tags);
        postService.save(post);
        if (sourcePage.equals("drafts")) {
            return "redirect:/blogs/showFormForViewUser?authorId=" + authorId;
        } else {
            return "redirect:/blogs/showFormForViewPost?postId=" + postId + "&authorId=" + authorId;
        }
    }

    @GetMapping("/showFormForUpdateDraftPost")
    public String updateDraftPost(@RequestParam("postId") int postId, Model model) {
        Post post = postService.findById(postId);
        post.setIsPublished(true);
        post.setPublishedAt(Timestamp.valueOf(LocalDateTime.now()));
        postService.save(post);
        model.addAttribute("post", post);
        return "redirect:/blogs/list";
    }

    @GetMapping("/delete")
    public String delete(@RequestParam("postId") int deleteId) {
        Post post = postService.deleteById(deleteId);
        for (Tag tag : post.getTags()) {
            tagService.deleteTagIfNotUsed(tag);
        }
        return "redirect:/blogs/list";
    }
}