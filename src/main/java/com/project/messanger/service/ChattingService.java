package com.project.messanger.service;

import com.project.messanger.dto.*;
import com.project.messanger.mapper.ChattingMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChattingService {
    private final ChattingMapper chattingMapper;

    public ChattingService(ChattingMapper chattingMapper) {
        this.chattingMapper = chattingMapper;
    }

    /*
     * get chatting message list
     * @param Map<String, Object>
     * return List<ChattingDto>
     */
    @Transactional(readOnly = true)
    public List<ChattingMessageDto> getChattingMessageList(Map<String, Object> param) {
        return chattingMapper.getChattingMessageList(param);
    }

    /*
     * get chatting list
     * @param Map<String, Object>
     * return List<ChattingDto>
     */
    @Transactional(readOnly = true)
    public List<ChattingDto> getChattingList(Map<String, Object> param) {
        return chattingMapper.getChattingList(param);
    }

    /*
     * get chatting list
     * @param Map<String, Object>
     * return List<ChattingDto>
     */
    @Transactional(readOnly = true)
    public ChattingDto getChattingByIdx(Map<String, Object> param) {
        return chattingMapper.getChattingByIdx(param);
    }

    /*
     * insert chatting
     * @param ChattingDto
     * return long
     */
    @Transactional
    public long insertChattingMessage(ChattingMessageDto chattingMessageDto) {
        return chattingMapper.insertChattingMessage(chattingMessageDto);
    }

    /*
     * insert chatting
     * @param ChattingDto
     * return long
     */
    @Transactional
    public long insertChatting(ChattingDto chattingDto) {
        return chattingMapper.insertChatting(chattingDto);
    }

    /*
     * update chatting
     * @param ChattingDto
     * return int
     */
    @Transactional
    public int updateChatting(ChattingDto chattingDto) {
        return chattingMapper.updateChatting(chattingDto);
    }

    /*
     * delete chatting
     * @param long
     * return int
     */
    @Transactional
    public int deleteChatting(long chattingIdx) {
        return chattingMapper.deleteChatting(chattingIdx);
    }

    /*
     * insert chatting user link
     * @param List<ChattingUserLinkDto>
     * return int
     */
    @Transactional
    public int insertChattingUserLink(List<ChattingUserLinkDto> list) {
        return chattingMapper.insertChattingUserLink(list);
    }

    /*
     * insert chatting user link by user idx
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChattingUserLinkByUserIdx(long chattingIdx, List<Long> userIdxList) {
        List<ChattingUserLinkDto> insertList = new ArrayList<>();

        // 값 리스트
        for (int i = 0; i < userIdxList.size(); i++) {
            ChattingUserLinkDto chattingUserLinkDto = ChattingUserLinkDto.builder()
                    .chattingIdx(chattingIdx)
                    .userIdx(userIdxList.get(i))
                    .build();

            insertList.add(chattingUserLinkDto);
        }

        return insertChattingUserLink(insertList);
    }

    /*
     * insert chatting user link
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChattingUserLinkByUserIdx(long chattingIdx, long userIdx) {
        List<ChattingUserLinkDto> insertList = new ArrayList<>();

        ChattingUserLinkDto chattingUserLinkDto = ChattingUserLinkDto.builder()
                .chattingIdx(chattingIdx)
                .userIdx(userIdx)
                .build();

        insertList.add(chattingUserLinkDto);

        return insertChattingUserLink(insertList);
    }

    /*
     * delete chatting user link
     * @param long
     * return int
     */
    @Transactional
    public int deleteChattingUserLink(long chattingIdx) {
        return chattingMapper.deleteChattingUserLink(chattingIdx);
    }
}
