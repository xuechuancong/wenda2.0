package com.nowcoder.service;

import com.nowcoder.dao.UserDAO;
import com.nowcoder.dao.LoginTicketDAO;
import com.nowcoder.model.LoginTicket;
import com.nowcoder.model.User;
import com.nowcoder.util.WendaUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private LoginTicketDAO loginTicketDAO;

    public Map<String, String> register(String name, String password) {
        Map<String, String> map = new HashMap<>();

        if (StringUtils.isBlank(name)) {
            map.put("msg", "用户名不能为空！");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空！");
            return map;
        }

        User user = userDAO.selectByName(name);

        if (user != null) {
            map.put("msg", "用户名已经被注册！");
            return map;
        }

        user = new User();
        user.setName(name);
        user.setSalt(UUID.randomUUID().toString().substring(0,6));
        user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                new Random().nextInt(1000)));
        user.setPassword(WendaUtil.MD5(password+user.getSalt()));
        userDAO.addUsers(user);

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);

        return map;
    }

    public Map<String, Object> login(String name, String password) {
        Map<String, Object> map = new HashMap<>();

        if (StringUtils.isBlank(name)) {
            map.put("msg", "用户名不能为空！");
            return map;
        }

        if (StringUtils.isBlank(password)) {
            map.put("msg", "密码不能为空！");
            return map;
        }

        User user = userDAO.selectByName(name);

        if (user == null) {
            map.put("msg", "用户名不存在！");
            return map;
        }

        if ( !Objects.equals(WendaUtil.MD5(password + user.getSalt()), user.getPassword())) {
            map.put("msg", "密码错误！");
            return map;
        }

        String ticket = addLoginTicket(user.getId());
        map.put("ticket", ticket);
        map.put("userId", user.getId());

        return map;
    }

    public String addLoginTicket(int userId) {
        LoginTicket ticket = new LoginTicket();

        ticket.setUserId(userId);
        Date now = new Date();
        now.setTime(now.getTime() + 3600*24*100);
        ticket.setExpired(now);
        ticket.setStatus(0);
        ticket.setTicket(UUID.randomUUID().toString().replace("-", ""));
        loginTicketDAO.addLoginTicket(ticket);

        return ticket.getTicket();

    }

    public void logout(String ticket) {
        loginTicketDAO.updateStatus(ticket, 1);
    }



    public User getUser(int id) {
        return userDAO.selectById(id);
    }

    public User selectByName(String name) {
        return userDAO.selectByName(name);
    }
}
