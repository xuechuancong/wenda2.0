package com.nowcoder.service;

import com.nowcoder.dao.FeedDAO;
import com.nowcoder.model.Feed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    @Autowired
    FeedDAO feedDAO;

    public List<Feed> getUserFeed(int maxId, List<Integer> userId, int count) {
        return feedDAO.selectUserFeeds(maxId, userId, count);
    }

    public Feed getById(int id) {
        return feedDAO.getFeedById(id);
    }

    public Boolean addFeed(Feed feed) {
        feedDAO.addFeed(feed);
        return feed.getId() > 0;
    }
}
