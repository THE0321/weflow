package com.project.messanger.service;

import com.project.messanger.dto.GoalDto;
import com.project.messanger.dto.NoticeDto;
import com.project.messanger.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NoticeService {
    private NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /*
     * insert notice
     * @param NoticeDto
     * return long
     */
    @Transactional
    public long insertNotice(NoticeDto noticeDto) {
        return noticeMapper.insertNotice(noticeDto);
    }

    /*
     * update notice
     * @param NoticeDto
     * return int
     */
    @Transactional
    public int updateNotice(NoticeDto noticeDto) {
        return noticeMapper.updateNotice(noticeDto);
    }

    /*
     * delete goal
     * @param long
     * return int
     */
    @Transactional
    public int deleteNotice(long noticeIdx) {
        return noticeMapper.deleteNotice(noticeIdx);
    }
}
