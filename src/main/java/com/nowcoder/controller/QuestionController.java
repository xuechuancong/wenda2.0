package com.nowcoder.controller;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.model.*;
import com.nowcoder.service.CommentService;
import com.nowcoder.service.LikeService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class QuestionController {

    @Autowired
    QuestionService questionService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);

    @RequestMapping(path = {"/question/{qid}"}, method = {RequestMethod.GET})
    public String getQuestionDetail(Model model, @PathVariable("qid") int qid) {

        Question question = questionService.selectQuestion(qid);

        model.addAttribute("question", question);

        List<Comment> commentList = commentService.getComment(qid, EntityType.ENTITY_QUESTION);
        List<ViewObject> vos = new ArrayList<>();
        for (Comment comment : commentList) {

            ViewObject vo = new ViewObject();
            vo.set("user", userService.getUser( comment.getUserId() ));
            vo.set("comment", comment);
            if (hostHolder.getUser() != null) {
                vo.set("liked", likeService.getLikeStatus(hostHolder.getUser().getId(),
                        EntityType.ENTITY_COMMENT, comment.getId()));
            }
            else {
                vo.set("liked", 0);
            }

            vo.set("likeCount", likeService.getLikeCount(EntityType.ENTITY_COMMENT, comment.getId()));
            vos.add(vo);

        }
        model.addAttribute("comments", vos);

        return "detail";

    }



    @RequestMapping(path = {"/question/add"}, method = {RequestMethod.POST})
    @ResponseBody
    public String addQuestion(@RequestParam("title")String title,
                              @RequestParam("content") String content) {
        try {
            Question question = new Question();
            question.setTitle(title);
            question.setContent(content);
            question.setCreatedDate(new Date());

            if (hostHolder.getUser() == null) {

                question.setUserId(WendaUtil.ANOYMOUS_USERID );
            } else {

                question.setUserId( hostHolder.getUser().getId() );

            }

            if (questionService.addQuestion(question) > 0) {
                return WendaUtil.getJSONString(0);
            }


        } catch (Exception e) {
            logger.error("添加问题失败！", e.getMessage());
        }

        return WendaUtil.getJSONString(1, "失败！");

    }
}
