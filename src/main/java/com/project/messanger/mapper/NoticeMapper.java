package com.project.messanger.mapper;

import com.project.messanger.dto.NoticeWithUserDto;
import com.project.messanger.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeMapper {
    long getNoticeCount(Map<String, Object> param);

    List<NoticeWithUserDto> getNoticeList(Map<String, Object> param);

    NoticeWithUserDto getNoticeByIdx(Map<String, Object> param);

    int insertNotice(NoticeDto noticeDto);

    int updateNotice(NoticeDto noticeDto);

    int deleteNotice(long noticeIdx);
}
