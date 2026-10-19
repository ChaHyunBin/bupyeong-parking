package com.mbc.project.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class BoardDto implements Serializable {
    private Long seq;           // 게시글 번호(board_seq) 또는 댓글의 PK(id)
    private Long boardSeq;      // 댓글이 소속된 게시글 번호
    private String writerId;    // 작성자 (board_writer 또는 comment_writer)
    private String title;       // 제목 (게시글)
    private String content;     // 내용 (board_content 또는 comment_content)
    private Integer readcount;  // 조회수
    private LocalDateTime wdate;// 작성 시간
    private Boolean ispinned;   // 상단 고정(공지글) 여부 0807 공지글 추가

    public BoardDto() {}

    public Long getSeq() { return seq; }
    public void setSeq(Long seq) { this.seq = seq; }
    
    public Long getBoardSeq() { return boardSeq; }
    public void setBoardSeq(Long boardSeq) { this.boardSeq = boardSeq; }

    public String getWriterId() { return writerId; }
    public void setWriterId(String writerId) { this.writerId = writerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getReadcount() { return readcount; }
    public void setReadcount(Integer readcount) { this.readcount = readcount; }

    public LocalDateTime getWdate() { return wdate; }
    public void setWdate(LocalDateTime wdate) { this.wdate = wdate; }

    public Boolean getIspinned() { return ispinned; }
    public void setIspinned(Boolean ispinned) { this.ispinned = ispinned; } 	// 0807 공지글 추가
}