package com.project.messanger.controller;

import com.project.messanger.dto.FileDto;
import com.project.messanger.dto.UserDto;
import com.project.messanger.service.FileService;
import com.project.messanger.util.AuthUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    private final FileService fileService;
    private final AuthUtil authUtil;

    public FileController(FileService fileService, AuthUtil authUtil) {
        this.fileService = fileService;
        this.authUtil = authUtil;
    }

    @PostMapping("/upload")
    public Map<String, Object> insertFile(HttpServletRequest request,
                                          MultipartHttpServletRequest multipartHttpServletRequest){
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        } else if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "파일을 등록할 권한이 없습니다.");

            return result;
        }

        try {
            // 파일 업로드
            FileDto fileDto = fileService.uploadFile(multipartHttpServletRequest);
            if (fileDto.getFilePath() == null) {
                result.put("success", false);
                result.put("error", "파일을 등록하는데 실패했습니다.");

                return result;
            }
            fileDto.setUserIdx(loginInfo.getUserIdx());

            long fileIdx = fileService.insertFile(fileDto);

            result.put("success", true);
            result.put("idx", fileIdx);
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "파일을 등록하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/delete")
    public Map<String, Object> deleteFile(HttpServletRequest request,
                                          @RequestParam("file_idx") long fileIdx) {
        Map<String, Object> result = new HashMap<>();
        HttpSession session = request.getSession();

        UserDto loginInfo = authUtil.getLoginInfo(session);
        if (loginInfo == null) {
            result.put("success", false);
            result.put("error", "로그인 해주세요.");

            return result;
        } else if (loginInfo.getAdminYn().equals("N") && loginInfo.getLeaderYn().equals("N")) {
            result.put("success", false);
            result.put("error", "파일을 삭제할 권한이 없습니다.");

            return result;
        }

        try {
            // 파일 삭제
            int success = fileService.deleteFile(fileIdx);

            result.put("success", success == 1);
            if (success == 0) {
                result.put("error", "파일을 삭제하는데 실패했습니다.");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", "파일을 삭제하는데 실패했습니다.");
        }

        return result;
    }

    @PostMapping("/download")
    public void downloadFile(HttpServletRequest request,
                             HttpServletResponse response,
                             @RequestParam("file_idx") long fileIdx) throws ServletException, IOException {
        HttpSession session = request.getSession();

        FileDto fileDto = fileService.getFileByIdx(fileIdx);
        String fileName = new String(fileDto.getName().getBytes("UTF-8"), "8859_1");

        File file = new File(fileDto.getFilePath());
        FileInputStream in = new FileInputStream(fileDto.getFilePath());

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
        OutputStream os = response.getOutputStream();

        int length;
        byte[] buffer = new byte[(int)file.length()];
        while ((length = in.read(buffer)) > 0) {
            os.write(buffer, 0, length);
        }

        os.flush();

        os.close();
        in.close();
    }
}
