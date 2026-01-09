package com.project.messanger.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface AIMapper {
    List<String> getFeatureList();
}
