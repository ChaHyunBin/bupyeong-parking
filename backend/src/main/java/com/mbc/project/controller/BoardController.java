package com.mbc.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import com.mbc.project.dto.BoardDto;
import com.mbc.project.service.BoardService;

@RestController
public class BoardController {
    private final BoardService service;

    public BoardController(BoardService service) { 
        this.service = service; 
    }

    // 게시글 목록
    @GetMapping("/boardlist")
    public Map<String, Object> list(@RequestParam(defaultValue = "all") String choice,
                                    @RequestParam(required = false) String keyword,
                                    @RequestParam(defaultValue = "1") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        return service.list(choice, keyword, page, size);
    }

    // 게시글 작성 (ispinned 파라미터 수신 가능하도록 Map<String, Object> 변경) 여부 0807 공지글 추가
    @PostMapping("/writeboard")
    public boolean write(@RequestBody Map<String, Object> body) {
        try {
            BoardDto dto = new BoardDto();
            dto.setWriterId((String) body.get("writerId"));
            dto.setTitle((String) body.get("title"));
            dto.setContent((String) body.get("content"));

            // ispinned 전송 여부 판단
            if (body.get("ispinned") != null) {
                dto.setIspinned(Boolean.parseBoolean(body.get("ispinned").toString())); //트루
            } else {
                dto.setIspinned(false); //false
            }

            return service.write(dto);
        } catch (Exception e) { //이거 걸면 다른 건 실행된다 여기 에러 나도 실행 됨 서버 자체는 계속 돌아감
            e.printStackTrace();
            System.out.print("boardController write");
            return false;
        }
    }

    @PostMapping("/detailboard")
    public BoardDto detail(@RequestParam Long seq) {
        return service.detail(seq);
    }

    @PostMapping("/updateboard")
    public boolean update(@RequestBody Map<String, Object> body) {
        try {
            Long seq = Long.parseLong(body.get("seq").toString());
            String writerId = (String) body.get("writerId");
            String title = (String) body.get("title");
            String content = (String) body.get("content");
            
            Boolean ispinned = false;
            if (body.get("ispinned") != null) {
                ispinned = Boolean.parseBoolean(body.get("ispinned").toString());
            }

            return service.update(seq, writerId, title, content, ispinned);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/deleteboard")
    public boolean delete(@RequestBody Map<String, String> body) {
        try {
            Long seq = Long.parseLong(body.get("seq"));
            String writerId = body.get("writerId");
            return service.delete(seq, writerId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @GetMapping("/commentlist")
    public List<BoardDto> listComment(@RequestParam Long boardSeq) {
        return service.listComment(boardSeq);
    }

    @PostMapping("/addcomment")
    public boolean writeComment(@RequestBody Map<String, String> body) {
        try {
            BoardDto dto = new BoardDto();
            dto.setBoardSeq(Long.parseLong(body.get("boardSeq")));
            dto.setWriterId(body.get("writerId"));
            dto.setContent(body.get("content"));
            return service.writeComment(dto);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @PostMapping("/deletecomment")
    public boolean deleteComment(@RequestBody Map<String, String> body) {
        try {
            Long seq = Long.parseLong(body.get("seq"));
            String writerId = body.get("writerId");
            return service.deleteComment(seq, writerId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}