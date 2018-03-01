package com.nowcoder.async.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nowcoder.async.EventHandler;
import com.nowcoder.async.EventModel;
import com.nowcoder.async.EventType;
import com.nowcoder.model.*;
import com.nowcoder.service.FeedService;
import com.nowcoder.service.FollowService;
import com.nowcoder.service.QuestionService;
import com.nowcoder.service.UserService;
import com.nowcoder.util.JedisAdaptor;
import com.nowcoder.util.RedisKeyUtil;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.*;

public class FeedHandler implements EventHandler {

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    FeedService feedService;

    @Autowired
    FollowService followService;

    @Autowired
    JedisAdaptor jedisAdaptor;

    public String buildFeedData(EventModel model) {
        Map<String, String> map = new HashMap<>();
        //触发用户
        User actor = userService.getUser(model.getActorId());

        if (actor == null) {
            return null;
        }

        map.put("userId", String.valueOf(actor.getId()));
        map.put("userHead", actor.getHeadUrl());
        map.put("userName", actor.getName());

        if (model.getType() == EventType.COMMENT || (model.getType() == EventType.FOLLOW &&
                    model.getEntityType() == EntityType.ENTITY_QUESTION)) {

            Question question = questionService.selectQuestion(model.getEntityId());
            if (question == null) {
                return null;
            }

            map.put("questionId", String.valueOf(question.getId()));
            map.put("questionTitle", question.getTitle());
            return JSONObject.toJSONString(map);

        }
        return null;

    }

    @Override
    public void doHandle(EventModel model) {
        //测试使用
        Random r = new Random();
        model.setActorId(1+r.nextInt(10));


        Feed feed = new Feed();
        feed.setCreatedDate(new Date());
        feed.setType(model.getType().getValue());
        feed.setUserId(model.getActorId());
        feed.setData(buildFeedData(model));

        if (feed.getData() == null)
            return;

        feedService.addFeed(feed);

        //获取所有粉丝ID
        List<Integer> followers = followService.getFollowers(EntityType.ENTITY_USER, model.getActorId(), Integer.MAX_VALUE);

        //系统队列
        followers.add(0);

        //给所有粉丝推事件
        for (int follower: followers) {
            String timeLineKey = RedisKeyUtil.getTimelineKey(follower);
            jedisAdaptor.lpush(timeLineKey, String.valueOf(feed.getId()));
        }

    }

    @Override
    public List<EventType> getSupportEventTypes() {
        return Arrays.asList( new EventType[]{EventType.FOLLOW, EventType.COMMENT });
    }
}
