package com.sunlands.deskmate.mapper;

import com.sunlands.deskmate.entity.InputKeywords;
import com.sunlands.deskmate.entity.InputKeywordsExample;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InputKeywordsMapper {
    int countByExample(InputKeywordsExample example);

    int deleteByExample(InputKeywordsExample example);

    @Delete({
        "delete from input_keywords",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    @Insert({
        "insert into input_keywords (scope, type, ",
        "keyword, create_time, ",
        "create_user)",
        "values (#{scope,jdbcType=INTEGER}, #{type,jdbcType=VARCHAR}, ",
        "#{keyword,jdbcType=VARCHAR}, #{createTime,jdbcType=TIMESTAMP}, ",
        "#{createUser,jdbcType=VARCHAR})"
    })
    @SelectKey(statement="SELECT LAST_INSERT_ID()", keyProperty="id", before=false, resultType=Integer.class)
    int insert(InputKeywords record);

    int insertSelective(InputKeywords record);

    List<InputKeywords> selectByExample(InputKeywordsExample example);

    @Select({
        "select",
        "id, scope, type, keyword, create_time, create_user",
        "from input_keywords",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @ResultMap("BaseResultMap")
    InputKeywords selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") InputKeywords record, @Param("example") InputKeywordsExample example);

    int updateByExample(@Param("record") InputKeywords record, @Param("example") InputKeywordsExample example);

    int updateByPrimaryKeySelective(InputKeywords record);

    @Update({
        "update input_keywords",
        "set scope = #{scope,jdbcType=INTEGER},",
          "type = #{type,jdbcType=VARCHAR},",
          "keyword = #{keyword,jdbcType=VARCHAR},",
          "create_time = #{createTime,jdbcType=TIMESTAMP},",
          "create_user = #{createUser,jdbcType=VARCHAR}",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(InputKeywords record);
}