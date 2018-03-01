package com.nowcoder.dao;

import com.nowcoder.model.Comment;
import com.nowcoder.model.Question;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface CommentDAO {
    String TABLE_NAME = " comment ";
    String INSERT_FIELD = " user_id, content, created_date, entity_id, entity_type, status";
    String SELECT_FIELD = " id, " + INSERT_FIELD;

    @Select({"select ", SELECT_FIELD, "from ", TABLE_NAME,
            "where id=#{id}" })
    Comment getCommentById(int id);

    @Select({"select count(id) from ", TABLE_NAME, " where user_id=#{userId}"})
    int getUserCommentCount(int userId);

    @Insert({"insert into", TABLE_NAME, "(", INSERT_FIELD, ") values(" +
            "#{userId},#{content}, #{createdDate}, #{entityId}, #{entityType}, #{status})"})
    int addComment(Comment comment);

    @Select({"select ", SELECT_FIELD, "from ", TABLE_NAME,
            "where entity_id=#{entityId} and entity_type=#{entityType} order by id desc" } )
    List<Comment> getCommentsByEntity(@Param("entityId") int entityId,
                                  @Param("entityType") int entityType);

    @Select({"select count(id)", " from ", TABLE_NAME,
            "where entity_id=#{entityId} and entity_type=#{entityType} " } )
    int getCommentCount(@Param("entityId") int entityId,
                                        @Param("entityType") int entityType);

    @Update({"update ", TABLE_NAME, " set status=#{status} " +
            "where entity_id=#{entityId} and entity_type=#{entityType} "})
    void updateStatus(@Param("entityId") int entityId,
                        @Param("entityType") int entityType,
                      @Param("status") int status);


}
