package com.project.messanger.mapper;

import com.project.messanger.dto.GoalDto;
import com.project.messanger.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper {
    long insertNotice(NoticeDto noticeDto);

    int updateNotice(NoticeDto noticeDto);

    int deleteNotice(long noticeIdx);
}
