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
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

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
     * @param long
     * return ChattingDto
     */
    @Transactional(readOnly = true)
    public ChattingDto getChattingByIdx(long chattingIdx) {
        return chattingMapper.getChattingByIdx(chattingIdx);
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
     * get chatting user link
     * @param long
     * return List<ChattingUserLinkDto>
     */
    @Transactional
    public List<ChattingUserLinkDto> getChattingUserLink(long chattingIdx)
    {
        return chattingMapper.getChattingUserLink(chattingIdx);
    }

    /*
     * get chatting user link by idx
     * @param long
     * return ChattingUserLinkDto
     */
    @Transactional
    public ChattingUserLinkDto getChattingUserLinkByIdx(long linkIdx)
    {
        return chattingMapper.getChattingUserLinkByIdx(linkIdx);
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
        List<ChattingUserLinkDto> valueList = new ArrayList<>();
        List<Long> chattingUserList = new ArrayList<>(getChattingUserLink(chattingIdx).stream()
                .map(ChattingUserLinkDto::getUserIdx)
                .toList());

        // 값 리스트
        for (long userIdx : userIdxList) {
            if (chattingUserList.contains(userIdx)) {
                continue;
            }

            ChattingUserLinkDto chattingUserLinkDto = ChattingUserLinkDto.builder()
                    .chattingIdx(chattingIdx)
                    .userIdx(userIdx)
                    .build();

            valueList.add(chattingUserLinkDto);
            chattingUserList.add(userIdx);
        }

        if (valueList.isEmpty()) {
            return 0;
        }

        return insertChattingUserLink(valueList);
    }

    /*
     * insert chatting user link
     * @param long, List<Long>
     * return int
     */
    @Transactional
    public int insertChattingUserLinkByUserIdx(long chattingIdx, long userIdx) {
        List<ChattingUserLinkDto> insertList = new ArrayList<>();
        List<Long> chattingUserList = getChattingUserLink(chattingIdx).stream()
                .map(ChattingUserLinkDto::getUserIdx)
                .toList();

        if (chattingUserList.contains(userIdx)) {
            return 0;
        }

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
