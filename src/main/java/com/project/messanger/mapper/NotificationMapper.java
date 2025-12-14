package com.project.messanger.mapper;

import com.project.messanger.dto.NotificationDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NotificationMapper {
    List<NotificationDto> getNotificationList(Map<String, Object> param);

    NotificationDto getNotificationByIdx(long notificationIdx);

    int insertNotification(List<NotificationDto> list);

    int updateNotification(long notificationIdx);
}
