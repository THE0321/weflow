package com.project.messanger.service;

import com.project.messanger.dto.FileDto;
import com.project.messanger.dto.GoalAndLogDto;
import com.project.messanger.dto.NoticeDto;
import com.project.messanger.mapper.FileMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class FileService {
    private final FileMapper fileMapper;

    public FileService(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

    /*
     * get goal by goal idx
     * @param long
     * return goalDto
     */
    @Transactional(readOnly = true)
    public FileDto getFileByIdx(long fileIdx) {
        return fileMapper.getFileByIdx(fileIdx);
    }

    /*
     * insert file
     * @param ReservationDto
     * return long
     */
    @Transactional
    public long insertFile(FileDto fileDto) {
        return fileMapper.insertFile(fileDto);
    }

    /*
     * delete reservation
     * @param long
     * return int
     */
    @Transactional
    public int deleteFile(long fileIdx) {
        return fileMapper.deleteFile(fileIdx);
    }

    /*
     * upload file
     * @param MultipartHttpServletRequest
     * return List<FileDto>
     */
    public FileDto uploadFile(MultipartHttpServletRequest multipartHttpServletRequest) throws Exception {
        FileDto fileDto = new FileDto();

        // 오늘 날짜
        Date today = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMdd");
        String date = formater.format(today);

        // 저장 경로
        String savePath = "/file/upload/" + date;

        // 디렉토리 생성
        File dir = new File(savePath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdirs();
        }

        // 파일 업로드
        MultipartFile file = multipartHttpServletRequest.getFile("file");
        if (file != null) {
            if (!file.isEmpty()) {
                String filePath = savePath + "/" + UUID.randomUUID().toString() + "_" + Normalizer.normalize(Objects.requireNonNull(file.getOriginalFilename()), Normalizer.Form.NFD);
                file.transferTo(new File(filePath));

                fileDto = FileDto.builder()
                        .size(file.getSize())
                        .name(file.getOriginalFilename())
                        .filePath("/" + filePath)
                        .isDrive("Y")
                        .build();
            }
        }

        return fileDto;
    }
}
