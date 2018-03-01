package com.nowcoder.controller;

import com.nowcoder.dao.MessageDAO;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.Message;
import com.nowcoder.model.User;
import com.nowcoder.model.ViewObject;
import com.nowcoder.service.MessageService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.WendaUtil;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.apache.catalina.Host;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.jws.WebParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class MessageController {

    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);

    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @RequestMapping(path = {"/msg/addMessage"}, method = {RequestMethod.POST})
    @ResponseBody
    public String getMessageDetail(@RequestParam("toName") String toName,
                                    @RequestParam("content") String content) {

        try {

            if (hostHolder.getUser() == null) {
                return WendaUtil.getJSONString(999, "未登录！");
            }

            User user = userService.selectByName(toName);
            if (user == null)
                return WendaUtil.getJSONString(1, "用户不存在！");

            Message message = new Message();
            message.setContent(content);
            message.setCreatedDate(new Date());
            message.setFromId(hostHolder.getUser().getId());
            message.setToId(user.getId());
            message.setConversationId(message.getConversationId());
            messageService.addMessage(message);
            return WendaUtil.getJSONString(0);
        } catch (Exception e) {
            logger.error("私信发送失败！", e.getMessage());
            return WendaUtil.getJSONString(1, "插入站内信失败！");
        }

    }

    @RequestMapping(path = {"/msg/list"}, method = {RequestMethod.GET})
    public String getConversationList(Model model) {

        try {
            User localUser = hostHolder.getUser();
            if (localUser == null ) {
                return "redirect:/reglogin";
            }

            List<Message> messages = messageService.getConversationList(localUser.getId(), 0, 10);
            List<ViewObject> conversations = new ArrayList<>();
            for (Message msg : messages) {
                ViewObject vo = new ViewObject();
                vo.set("message", msg);
                int targetId = localUser.getId() == msg.getFromId()?msg.getToId() : msg.getFromId();
                vo.set("user", userService.getUser(targetId));
                vo.set("unread", messageService.getConversationUnreadCount(localUser.getId(), msg.getConversationId()));
                conversations.add(vo);
            }
            model.addAttribute("conversations", conversations);

        } catch (Exception e) {
            logger.error("获取列表失败！", e.getMessage() );

        }

        return "letter";
    }


    @RequestMapping(path = {"/msg/detail"}, method = {RequestMethod.GET})
    public String getConversationDetail(Model model,
                                    @RequestParam("conversationId") String conversationId) {

        try {
            List<Message> messages = messageService.getConversationDetail(conversationId, 0, 10);
            List<ViewObject> vos = new ArrayList<>();

            for (Message message : messages) {
                ViewObject vo = new ViewObject();
                vo.set("message", message);
                User user = userService.getUser( message.getFromId());
                if (user == null)
                    continue;

                vo.set("headUrl", user.getHeadUrl());
                vo.set("user", user );
                vos.add(vo);
            }
            model.addAttribute("messages", vos);
        } catch (Exception e) {
            logger.error("获取详情消息失败", e.getMessage() );
        }

        return "letterDetail";
    }
}
