package com.project.messanger.service;

import com.project.messanger.dto.NotificationDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.mapper.NotificationMapper;
import com.project.messanger.mapper.UserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private final NotificationMapper notificationMapper;
    private final UserMapper userMapper;

    public NotificationService(NotificationMapper notificationMapper, UserMapper userMapper) {
        this.notificationMapper = notificationMapper;
        this.userMapper = userMapper;
    }

    /*
     * get notification list
     * @param Map<String, Object>
     * return List<NotificationDto>
     */
    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return notificationMapper.getNotificationList(param);
    }

    /*
     * get notification by idx
     * @param long
     * return NotificationDto
     */
    @Transactional(readOnly = true)
    public NotificationDto getNotificationByIdx(long notificationIdx) {
        return notificationMapper.getNotificationByIdx(notificationIdx);
    }

    /*
     * insert notification
     * @param List<NotificationDto>
     * return int
     */
    @Transactional
    public int insertNotification(List<NotificationDto> notificationDtoList) {
        return notificationMapper.insertNotification(notificationDtoList);
    }

    /*
     * update notification
     * @param long
     * return int
     */
    @Transactional
    public int updateNotification(long notificationIdx) {
        return notificationMapper.updateNotification(notificationIdx);
    }

    /*
     * insert notification by user idx
     * @param NotificationDto notificationDto
     * @param List<Long> userIdxList
     * return int
     */
    @Transactional
    public int insertNotificationByUserIdx(NotificationDto notificationDto, List<Long> userIdxList) {
        List<NotificationDto> insertList = new ArrayList<>();

        for (long userIdx : userIdxList) {
            insertList.add(NotificationDto.builder()
                            .type(notificationDto.getType())
                            .content(notificationDto.getContent())
                            .linkUrl(notificationDto.getLinkUrl())
                            .creatorIdx(userIdx)
                            .build());
        }

        return insertNotification(insertList);
    }

    /*
     * insert notification by team idx
     * @param NotificationDto notificationDto
     * @param List<Long> userIdxList
     * return int
     */
    @Transactional
    public int insertNotificationByTeamIdx(NotificationDto notificationDto, List<Long> teamIdxList) {
        List<NotificationDto> insertList = new ArrayList<>();
        List<Long> containList = new ArrayList<>();;

        for (long teamIdx : teamIdxList) {
            List<Long> userIdxList = new ArrayList<>(userMapper.getUserListByTeamIdx(teamIdx).stream()
                    .map(UserDto::getUserIdx)
                    .toList());

            for (long userIdx : userIdxList) {
                if (containList.contains(userIdx)) continue;

                insertList.add(NotificationDto.builder()
                        .type(notificationDto.getType())
                        .content(notificationDto.getContent())
                        .linkUrl(notificationDto.getLinkUrl())
                        .creatorIdx(userIdx)
                        .build());

                containList.add(userIdx);
            }
        }

        if (insertList.isEmpty()) {
            return 0;
        }

        return insertNotification(insertList);
    }
}
