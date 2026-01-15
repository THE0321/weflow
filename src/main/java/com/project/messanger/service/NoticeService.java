package com.project.messanger.service;

import com.project.messanger.dto.NoticeDto;
import com.project.messanger.dto.NoticeWithUserDto;
import com.project.messanger.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class NoticeService {
    private final NoticeMapper noticeMapper;

    public NoticeService(NoticeMapper noticeMapper) {
        this.noticeMapper = noticeMapper;
    }

    /*
     * get notice count
     * @param Map<String, Object>
     * return long
     */
    @Transactional(readOnly = true)
    public long getNoticeCount(Map<String, Object> param) {
        return noticeMapper.getNoticeCount(param);
    }

    /*
     * get notice list
     * @param Map<String, Object>
     * return List<NoticeWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<NoticeWithUserDto> getNoticeList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return noticeMapper.getNoticeList(param);
    }

    /*
     * get notice by notice idx
     * @param long
     * return NoticeWithUserDto
     */
    @Transactional(readOnly = true)
    public NoticeWithUserDto getNoticeByIdx(Map<String, Object> param) {
        return noticeMapper.getNoticeByIdx(param);
    }

    /*
     * insert notice
     * @param NoticeDto
     * return int
     */
    @Transactional
    public int insertNotice(NoticeDto noticeDto) {
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
