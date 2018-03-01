package com.nowcoder.dao;

import com.nowcoder.model.Question;
import com.nowcoder.model.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface QuestionDAO {
    String TABLE_NAME = " question ";
    String INSERT_FIELD = " title, content, user_id, created_date, comment_count ";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELD, ") values(" +
            "#{title},#{content}, #{userId}, #{createdDate}, #{commentCount})"})
    int addQuestion(Question question);

    @Select({"select ", SELECT_FIELD, "from ", TABLE_NAME, "where id=#{id}"})
    Question selectQuestionByID(int qid);

    List<Question> selectLatestQuestions(@Param("userId") int userId,
                              @Param("offset") int offset,
                              @Param("limit") int limit);

    @Update({" update ", TABLE_NAME, " set comment_count=#{commentCount} where id=#{id}"})
    int updateCommentCount(@Param("id") int id,
                           @Param("commentCount") int commentCount);


}
