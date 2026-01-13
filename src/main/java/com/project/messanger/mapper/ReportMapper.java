package com.project.messanger.mapper;

import com.project.messanger.dto.ReportWithUserDto;
import com.project.messanger.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    long getReportCount(Map<String, Object> param);

    List<ReportWithUserDto> getReportList(Map<String, Object> param);

    ReportWithUserDto getReportByIdx(long reportIdx);

    long insertReport(ReportDto reportDto);

    int updateReport(ReportDto reportDto);

    int deleteReport(long reportIdx);
}
