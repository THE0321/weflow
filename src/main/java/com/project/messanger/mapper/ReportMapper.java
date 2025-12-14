package com.project.messanger.mapper;

import com.project.messanger.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface ReportMapper {
    List<ReportDto> getReportList(Map<String, Object> param);

    ReportDto getReportByIdx(long reportIdx);

    long insertReport(ReportDto reportDto);

    int updateReport(ReportDto reportDto);

    int deleteReport(long reportIdx);
}
