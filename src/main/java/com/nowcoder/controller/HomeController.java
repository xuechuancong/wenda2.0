package com.nowcoder.controller;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @RequestMapping(path = {"/user/{userId}", "/index"}, method = {RequestMethod.GET})
    public String userIndex(Model model, @PathVariable("userId") int userId) {

        model.addAttribute("vos", getQuestions(userId, 0, 10));
        return "index";
    }

    @RequestMapping(path = {"/", "/index"}, method = {RequestMethod.GET})
    public String index(Model model) {

        model.addAttribute("vos", getQuestions(0, 0, 10));

        return "index";
    }


    private List<ViewObject> getQuestions(int userId, int offset, int limit) {
        List<Question> queueList = questionService.getLatestQuestions(userId, offset, limit);
        List<ViewObject> vos = new ArrayList<>();

        for (Question question: queueList) {

            ViewObject vo = new ViewObject();

            vo.set("question", question);
            vo.set("user", userService.getUser( question.getUserId() ));

            vos.add(vo);
        }
        return vos;
    }

//    @RequestMapping(path = {"/letter"}, method = {RequestMethod.GET})
//    public String letterIndex(Model model) {
//
//        return "letter";
//    }




}
