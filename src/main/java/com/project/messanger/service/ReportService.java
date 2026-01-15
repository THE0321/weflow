package com.project.messanger.service;

import com.project.messanger.dto.ReportDto;
import com.project.messanger.dto.ReportWithUserDto;
import com.project.messanger.mapper.ReportMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ReportService {
    private final ReportMapper reportMapper;

    public ReportService(ReportMapper reportMapper) {
        this.reportMapper = reportMapper;
    }

    /*
     * get report count
     * @param Map<String, Object>
     * return long
     */
    @Transactional(readOnly = true)
    public long getReportCount(Map<String, Object> param) {
        return reportMapper.getReportCount(param);
    }

    /*
     * get report list
     * @param Map<String, Object>
     * return List<ReportWithUserDto>
     */
    @Transactional(readOnly = true)
    public List<ReportWithUserDto> getReportList(Map<String, Object> param) {
        int page = param.get("page") != null ? (int)param.get("page") : 1;

        param.putIfAbsent("limit", 10);
        int limit = (int)param.get("limit");
        param.put("offset", (page-1) * limit);

        return reportMapper.getReportList(param);
    }

    /*
     * get report by idx
     * @param long
     * return ReportWithUserDto
     */
    @Transactional(readOnly = true)
    public ReportWithUserDto getReportByIdx(long reportIdx) {
        return reportMapper.getReportByIdx(reportIdx);
    }

    /*
     * insert report
     * @param ReportDto
     * return int
     */
    @Transactional
    public int insertReport(ReportDto reportDto) {
        return reportMapper.insertReport(reportDto);
    }

    /*
     * update report
     * @param ReportDto
     * return int
     */
    @Transactional
    public int updateReport(ReportDto reportDto) {
        return reportMapper.updateReport(reportDto);
    }

    /*
     * delete report
     * @param long
     * return int
     */
    @Transactional
    public int deleteReport(long reportIdx) {
        return reportMapper.deleteReport(reportIdx);
    }
}
