package com.nowcoder.dao;

import com.nowcoder.model.Message;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Mapper
@Repository
public interface MessageDAO {

    String TABLE_NAME = " message ";
    String INSERT_FIELD = " from_id, content, to_id, created_date, has_read, conversation_id ";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Insert({"insert into ", TABLE_NAME, "(", INSERT_FIELD,
            ") values (#{fromId},#{content},#{toId},#{createdDate},#{hasRead},#{conversationId})"})
    int addMessage(Message message);


    @Select({"select ", INSERT_FIELD, " ,count(id) as id from ( select * from ", TABLE_NAME, " where from_id=#{userId} or to_id=#{userId} order by id desc limit 999999) tt group by conversation_id  order by created_date desc limit #{offset}, #{limit}"})
    List<Message> getConversationList(@Param("userId") int userId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select ", SELECT_FIELD, " from ", TABLE_NAME, " where conversation_id=#{conversationId} order by id desc limit #{offset}, #{limit}"})
    List<Message> getConversationDetail(@Param("conversationId") String conversationId,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({"select count(id) from ", TABLE_NAME,
            "where has_read=0 and conversation_id=#{conversationId} and to_id=#{userId} " })
    int getConversationUnreadCount(@Param("userId") int userId,
                                        @Param("conversationId") String conversationId);
}
