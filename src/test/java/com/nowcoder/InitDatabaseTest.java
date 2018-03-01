package com.nowcoder;

import com.nowcoder.dao.QuestionDAO;
import com.nowcoder.dao.UserDAO;
import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Random;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = WendaApplication.class)
@Sql("/init-schema.sql")
public class InitDatabaseTest {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private QuestionDAO questionDAO;

    @Test
    public void contextLoads() {
        Random random = new Random();

        for (int i = 0; i < 12; i++) {
            User user = new User();

            user.setHeadUrl(String.format("http://images.nowcoder.com/head/%dt.png",
                    random.nextInt(1000)));
            user.setSalt("");
            user.setName(String.format("USER%d", i));
            user.setPassword("");

            userDAO.addUsers(user);

            user.setPassword("newpassword");
            userDAO.updatePassword(user);

            Question question = new Question();

            question.setCommentCount(i);
            Date date = new Date();
            date.setTime(date.getDate() + 1000 * 3600 * 5 * i);
            question.setCreatedDate(date);
            question.setContent(String.format("papapapapap content %d", i));
            question.setTitle(String.format("title %d", i));
            question.setUserId(i + 1);

            questionDAO.addQuestion(question);

        }


        Assert.assertEquals("newpassword", userDAO.selectById(1).getPassword());
        userDAO.deleteById(1);
        Assert.assertNull(userDAO.selectById(1));

        System.out.println( questionDAO.selectLatestQuestions(0, 0, 10));
    }

}
