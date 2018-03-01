package com.nowcoder.controller;

import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventProducer;
import com.nowcoder.async.EventType;
import com.nowcoder.service.UserService;
import com.sun.deploy.net.HttpResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Controller
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    EventProducer eventProducer;

    @RequestMapping(path = {"/reg/"}, method = {RequestMethod.POST})
    public String register(Model model,
                             @RequestParam("username") String username,
                             @RequestParam("password") String password,
                      @RequestParam(value = "next", required = false) String next,
                      @RequestParam(value = "rememberme", defaultValue = "false") Boolean rememberme,
                      HttpServletResponse response) {

        try {

            Map<String, String> map = userService.register(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket"));
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*6);
                }

                response.addCookie(cookie);



                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";


            }
            else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }

        }catch (Exception e) {
            Logger.getLogger("注册问题:", e.getMessage());
            return "login";
        }

    }

    @RequestMapping(path = {"/login/"}, method = {RequestMethod.POST})
    public String login(Model model,
                        @RequestParam("username") String username,
                        @RequestParam("password") String password,
                        @RequestParam(value = "next", required = false) String next,
                        @RequestParam(value = "rememberme", defaultValue = "false") Boolean rememberme,
                        HttpServletResponse response) {

        try {

            Map<String, Object> map = userService.login(username, password);

            if (map.containsKey("ticket")) {
                Cookie cookie = new Cookie("ticket", map.get("ticket").toString());
                cookie.setPath("/");
                if (rememberme) {
                    cookie.setMaxAge(3600*24*5);
                }
                response.addCookie(cookie);

                eventProducer.fireEvent(
                        new EventModel(EventType.LOGIN)
                        .setExt("username", username)
                        .setExt("email", "chuancongxue@163.com")
                        .setActorId((int)map.get("userId")));


                if (StringUtils.isNotBlank(next)) {
                    return "redirect:" + next;
                }
                return "redirect:/";


            }
            else {
                model.addAttribute("msg", map.get("msg"));
                return "login";
            }



        }catch (Exception e) {
            Logger.getLogger("登陆问题:", e.getMessage());
            return "login";
        }

    }

    @RequestMapping(path = {"/logout"}, method = {RequestMethod.GET})
    public String logout(@CookieValue("ticket") String ticket) {

        userService.logout(ticket);
        return "redirect:/";
    }



    @RequestMapping(path = {"/reglogin"}, method = {RequestMethod.GET})
    public String regLogin(Model model,
                           @RequestParam(value = "next", defaultValue = "",
                                   required = false) String next) {
        model.addAttribute("next", next);
        return "login";
    }
}
