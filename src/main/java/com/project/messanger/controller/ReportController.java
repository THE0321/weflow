package com.project.messanger.controller;

import com.project.messanger.dto.ReportDto;
import com.project.messanger.dto.ReportWithUserDto;
import com.project.messanger.dto.ScheduleDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.ReportService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {
    private final ReportService reportService;
    private final AuthUtil authUtil;

    public ReportController(ReportService reportService, AuthUtil authUtil) {
        this.reportService = reportService;
        this.authUtil = authUtil;
    }

    @PostMapping("/list")
    public Map<String, Object> getReportList(HttpServletRequest request,
                                             @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                             @RequestParam(value = "limit", required = false, defaultValue = "10") int limit,
                                             @RequestParam(value = "type", required = false) String type,
                                             @RequestParam(value = "title", required = false) String title) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            Map<String, Object> param = new HashMap<>();
            param.put("page", page);
            param.put("limit", limit);
            param.put("type", type);
            param.put("title", title);

            if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
                param.put("user_idx", loginInfo.getUserIdx());
            }

            // 보고서 목록 조회
            List<ReportWithUserDto> reportList = reportService.getReportList(param);
            result.put("success", true);
            result.put("list", reportList);
            result.put("count", reportService.getReportCount(param));
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/detail")
    public Map<String, Object> getReportDetail(HttpServletRequest request,
                                               @RequestParam("report_idx") long reportIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 보고서 조회
            ReportWithUserDto reportDto = reportService.getReportByIdx(reportIdx);
            if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N") && reportDto.getCreatorIdx() != loginInfo.getUserIdx()) {
                result.put("success", true);
                result.put("detail", reportDto);
            } else {
                System.out.println("1");
                result.put("success", false);
                result.put("error", "보고서 상세를 불러올 수 없습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서 상세를 불러올 수 없습니다.");
        }

        return result;
    }

    @PostMapping("/create")
    public Map<String, Object> insertReport(HttpServletRequest request,
                                            @RequestParam("title") String title,
                                            @RequestParam(value = "description", required = false) String description,
                                            @RequestParam("type") String type) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // ReportDto 객체 생성
            ReportDto reportDto = ReportDto.builder()
                    .title(title)
                    .description(description)
                    .type(type)
                    .creatorIdx(loginInfo.getUserIdx())
                    .build();

            if (loginInfo.getAdminYn().equals("Y") || loginInfo.getLeaderYn().equals("Y")) {
                reportDto.setApproverIdx(loginInfo.getUserIdx());
            }

            // 보고서 등록
            int success = reportService.insertReport(reportDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "보고서를 등록하는데 실패했습니다.");
                return result;
            }

            long reportIdx = reportDto.getReportIdx();
            result.put("idx", reportIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서를 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/modify")
    public Map<String, Object> updateReport(HttpServletRequest request,
                                            @RequestParam("report_idx") long reportIdx,
                                            @RequestParam("title") String title,
                                            @RequestParam(value = "description", required = false) String description) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 관리자 또는 팀장이 확인하지 않은 보고서만 수정 가능
            if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
                ReportWithUserDto reportDto = reportService.getReportByIdx(reportIdx);
                if (reportDto.getApproverIdx() != 0 || reportDto.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "보고서를 수정하는데 실패했습니다.");

                    return result;
                }
            }

            // ReportDto 객체 생성
            ReportDto reportDto = ReportDto.builder()
                    .reportIdx(reportIdx)
                    .title(title)
                    .description(description)
                    .build();

            // 보고서 수정
            int success = reportService.updateReport(reportDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "보고서를 수정하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서를 수정하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteReport(HttpServletRequest request,
                                            @RequestParam("report_idx") long reportIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        }

        try {
            // 관리자 또는 팀장이 확인하지 않은 보고서만 삭제 가능
            if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
                ReportWithUserDto reportDto = reportService.getReportByIdx(reportIdx);
                if (reportDto.getApproverIdx() != 0 || reportDto.getCreatorIdx() != loginInfo.getUserIdx()) {
                    result.put("success", false);
                    result.put("error", "보고서를 삭제하는데 실패했습니다.");

                    return result;
                }
            }

            // 보고서 삭제
            int success = reportService.deleteReport(reportIdx);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "보고서를 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서를 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/approver")
    public Map<String, Object> approverReport(HttpServletRequest request,
                                              @RequestParam("report_idx") long reportIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        } else if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            System.out.println("1"+1);
            result.put("success", false);
            result.put("error", "보고서를 승인하는데 실패했습니다.");

            return result;
        }

        try {
            // 승인할 데이터 확인
            ReportWithUserDto beforeData = reportService.getReportByIdx(reportIdx);
            if(beforeData == null) {
                result.put("success", false);
                result.put("error", "승인할 데이터가 없습니다.");

                return result;
            }

            // ReportDto 객체 생성
            ReportDto reportDto = ReportDto.builder()
                    .reportIdx(reportIdx)
                    .approverIdx(loginInfo.getUserIdx())
                    .build();

            // 보고서 승인
            int success = reportService.updateReport(reportDto);
            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "보고서를 승인하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "보고서를 승인하는데 실패했습니다.");
        }

        return result;
    }
}
