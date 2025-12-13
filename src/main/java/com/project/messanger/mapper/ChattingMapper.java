package com.project.messanger.mapper;

import com.project.messanger.dto.ChattingDto;
import com.project.messanger.dto.ChattingMessageDto;
import com.project.messanger.dto.ChattingUserLinkDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ChattingMapper {
    List<ChattingMessageDto> getChattingMessageList(Map<String, Object> param);

    long insertChattingMessage(ChattingMessageDto chattingMessageDto);

    List<ChattingDto> getChattingList(Map<String, Object> param);

    ChattingDto getChattingByIdx(Map<String, Object> param);

    long insertChatting(ChattingDto chattingDto);

    int updateChatting(ChattingDto chattingDto);

    int deleteChatting(long chattingIdx);

    int insertChattingUserLink(List<ChattingUserLinkDto> list);

    int deleteChattingUserLink(long linkIdx);
}
