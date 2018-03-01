package com.nowcoder.controller;


import com.nowcoder.async.EventType;
import com.nowcoder.model.EntityType;
import com.nowcoder.model.Feed;
import com.nowcoder.model.HostHolder;
import com.nowcoder.model.User;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.util.JedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class FeedController {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    FollowService followService;

    @Autowired
    FeedService feedService;

    @Autowired
    JedisAdaptor jedisAdaptor;

    @RequestMapping(path = {"/pushFeed"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String pushFeed(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0: hostHolder.getUser().getId();
        List<String> feedIds = jedisAdaptor.lrange(RedisKeyUtil.getTimelineKey(localUserId), 0, 10);
        List<Feed> feeds = new ArrayList<>();
        for(String feedId : feedIds) {
            Feed feed = feedService.getById(Integer.parseInt(feedId));
            if (feed != null) {
                feeds.add(feed);
            }
        }
        model.addAttribute("feeds", feeds);
        return "feeds";

    }



    @RequestMapping(path = {"/pullFeed"}, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public String pullFeed(Model model) {
        int localUserId = hostHolder.getUser() == null ? 0: hostHolder.getUser().getId();
        List<Integer> followees = new ArrayList<>();
        if (localUserId != 0) {
            //关注的人
            followees = followService.getFollowees(localUserId, EntityType.ENTITY_USER, Integer.MAX_VALUE);
        }

        List<Feed> feeds = feedService.getUserFeed(Integer.MAX_VALUE, followees, 10);
        model.addAttribute("feeds", feeds);
        return "feeds";

    }
}
