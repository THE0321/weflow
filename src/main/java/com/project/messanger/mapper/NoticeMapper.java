package com.project.messanger.mapper;

import com.project.messanger.dto.NoticeDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface NoticeMapper {
    List<NoticeDto> getNoticeList(Map<String, Object> param);

    NoticeDto getNoticeDetail(Map<String, Object> param);

    long insertNotice(NoticeDto noticeDto);

    int updateNotice(NoticeDto noticeDto);

    int deleteNotice(long noticeIdx);
}
