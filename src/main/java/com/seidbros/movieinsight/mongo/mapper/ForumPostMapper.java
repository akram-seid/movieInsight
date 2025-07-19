package com.seidbros.movieinsight.mongo.mapper;


import com.seidbros.movieinsight.mongo.dto.ForumPostInDto;
import com.seidbros.movieinsight.mongo.dto.ForumPostOutDto;
import com.seidbros.movieinsight.mongo.model.entity.ForumPost;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ForumPostMapper {

    ForumPost fromForumPostInDto(ForumPostInDto forumPostInDto);
    ForumPostOutDto toForumPostOutDto(ForumPost forumPost);

    List<ForumPostOutDto> toForumPostOutDtoList(List<ForumPost> content);
}
