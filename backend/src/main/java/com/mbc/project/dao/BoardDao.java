package com.mbc.project.dao;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.mbc.project.dto.BoardDto;

@Mapper
public interface BoardDao {
    List<BoardDto> list(@Param("choice") String choice,
                        @Param("keyword") String keyword, 
                        @Param("offset") int offset, 
                        @Param("limit") int limit);
    
    int count(@Param("choice") String choice,
              @Param("keyword") String keyword);
    
    int write(BoardDto dto);
    BoardDto findById(Long seq);
    int updateReadCount(Long seq);
    int update(BoardDto dto);
    int delete(Long seq);

    // [댓글]
    List<BoardDto> listComment(Long boardSeq);
    int writeComment(BoardDto dto);
    int updateComment(BoardDto dto);
    int deleteComment(@Param("seq") Long seq, @Param("writerId") String writerId);

    // ==========================================
    // [관리자 및 수정 메서드 파라미터 추가]
    // ==========================================
    Integer findAuthById(@Param("id") String id);
    int updateByAdmin(@Param("seq") Long seq, 
                      @Param("title") String title, 
                      @Param("content") String content,
                      @Param("ispinned") Boolean ispinned); // 0807 공지글 추가
    int deleteCommentByAdmin(@Param("seq") Long seq);
}