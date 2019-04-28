package com.sunlands.deskmate.mapper;

import com.sunlands.deskmate.dto.RequestDTO;
import com.sunlands.deskmate.entity.TzChatRecord;
import com.sunlands.deskmate.entity.TzChatRecordExample;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TzChatRecordMapper {
    long countByExample(TzChatRecordExample example);

    int deleteByExample(TzChatRecordExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TzChatRecord record);

    int insertSelective(TzChatRecord record);

    List<TzChatRecord> selectByExample(TzChatRecordExample example);

    TzChatRecord selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TzChatRecord record, @Param("example") TzChatRecordExample example);

    int updateByExample(@Param("record") TzChatRecord record, @Param("example") TzChatRecordExample example);

    int updateByPrimaryKeySelective(TzChatRecord record);

    int updateByPrimaryKey(TzChatRecord record);

    List<TzChatRecord> selectPrivateChatRecord(RequestDTO requestDTO);
}