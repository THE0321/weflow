package com.project.messanger.mapper;

import com.project.messanger.dto.FileDto;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper {
    FileDto getFileByIdx(long fileIdx);

    long insertFile(FileDto fileDto);

    int deleteFile(long fileIdx);
}
