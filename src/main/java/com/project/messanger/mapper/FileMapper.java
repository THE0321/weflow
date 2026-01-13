package com.project.messanger.mapper;

import com.project.messanger.dto.FileWithUserDto;
import com.project.messanger.dto.FileDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface FileMapper {
    long getFileCount(Map<String, Object> param);

    List<FileWithUserDto> getFileList(Map<String, Object> param);

    FileDto getFileByIdx(long fileIdx);

    long insertFile(FileDto fileDto);

    int deleteFile(long fileIdx);
}
