package com.seidbros.movieinsight.mapper;


import com.seidbros.movieinsight.dto.ForumPostInDto;
import com.seidbros.movieinsight.dto.ForumPostOutDto;
import com.seidbros.movieinsight.model.ForumPost;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ForumPostMapper {

    ForumPost fromForumPostInDto(ForumPostInDto forumPostInDto);
    ForumPostOutDto toForumPostOutDto(ForumPost forumPost);

    List<ForumPostOutDto> toForumPostOutDtoList(List<ForumPost> content);
}
