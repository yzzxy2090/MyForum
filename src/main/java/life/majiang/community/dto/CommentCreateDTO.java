package life.majiang.community.dto;

import lombok.Data;

@Data
public class CommentCreateDTO {
    /**
     * parentId
     * 如果当前评论是问题的一级评论，parentId为该问题的id
     * 如果当前评论是二级评论，parentId为其所回复的评论的id
     */
    private Long parentId;
    private String content;
    private Integer type;
}
