package com.nowcoder.service;

import com.nowcoder.dao.CommentDAO;
import com.nowcoder.model.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    CommentDAO commentDAO;

    @Autowired
    private SensitiveService sensitiveService;

    public int addComment(Comment comment) {
        comment.setContent(HtmlUtils.htmlEscape( comment.getContent()));

        comment.setContent(sensitiveService.filter( comment.getContent() ));

        return commentDAO.addComment(comment) > 0?comment.getId() : 0;

    }

    public int getUserCommentCount(int userId) {
        return commentDAO.getUserCommentCount(userId);
    }


    public Comment getCommentById(int id) {
        return commentDAO.getCommentById(id);
    }

    public List<Comment> getComment(int entityId, int entityType) {
        return commentDAO.getCommentsByEntity(entityId, entityType);
    }

    public int getCommentCount(int entityId, int entityType) {
        return commentDAO.getCommentCount(entityId, entityType);
    }

    public void deleteComment(int entityId, int entityType, int status) {
        commentDAO.updateStatus(entityId, entityType, status);
    }
}
