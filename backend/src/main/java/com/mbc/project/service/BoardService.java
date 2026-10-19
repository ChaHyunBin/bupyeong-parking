package com.mbc.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.mbc.project.dao.BoardDao;
import com.mbc.project.dto.BoardDto;

@Service
public class BoardService {
    private final BoardDao dao;

    public BoardService(BoardDao dao) { 
        this.dao = dao; 
    }

    public Map<String, Object> list(String choice, String keyword, int page, int size) {
        int offset = (page - 1) * size; 
        
        List<BoardDto> boards = dao.list(choice, keyword, offset, size); 
        int totalCount = dao.count(choice, keyword); 
        int totalPages = (int) Math.ceil((double) totalCount / size); 

        Map<String, Object> response = new HashMap<>();
        response.put("boards", boards);
        response.put("list", boards); 
        response.put("currentPage", page);
        response.put("totalPages", totalPages);
        response.put("totalCount", totalCount);
        return response;
    }

    public boolean write(BoardDto dto) {
        return dao.write(dto) > 0;
    }

    public BoardDto detail(Long seq) {
        dao.updateReadCount(seq); 
        return dao.findById(seq); 
    }

    // 글 수정 (ispinned 파라미터 추가)
    public boolean update(Long seq, String loginUserId, String title, String content, Boolean ispinned) {
        Integer auth = dao.findAuthById(loginUserId);
        boolean isAdmin = (auth != null && auth == 1);

        if (isAdmin) {
            return dao.updateByAdmin(seq, title, content, ispinned) > 0; 
        } else {
            BoardDto dto = new BoardDto();
            dto.setSeq(seq);
            dto.setTitle(title);
            dto.setContent(content);
            dto.setWriterId(loginUserId);
            dto.setIspinned(ispinned);
            return dao.update(dto) > 0;
        }
    }

    public boolean delete(Long seq, String loginUserId) {
        Integer auth = dao.findAuthById(loginUserId);
        boolean isAdmin = (auth != null && auth == 1);

        if (isAdmin) {
            return dao.delete(seq) > 0; 
        } else {
            BoardDto dto = dao.findById(seq); 
            if (dto != null && dto.getWriterId() != null && dto.getWriterId().equals(loginUserId)) {
                return dao.delete(seq) > 0; 
            }
        }
        return false; 
    }

    public List<BoardDto> listComment(Long boardSeq) {
        return dao.listComment(boardSeq);
    }

    public boolean writeComment(BoardDto dto) {
        return dao.writeComment(dto) > 0;
    }

    public boolean updateComment(Long seq, String writerId, String content) {
        BoardDto dto = new BoardDto();
        dto.setSeq(seq);
        dto.setWriterId(writerId); 
        dto.setContent(content);
        return dao.updateComment(dto) > 0;
    }

    public boolean deleteComment(Long seq, String loginUserId) {
        Integer auth = dao.findAuthById(loginUserId);
        boolean isAdmin = (auth != null && auth == 1);	// 여부 0807 공지글 추가

        if (isAdmin) {
            return dao.deleteCommentByAdmin(seq) > 0;
        } else {
            return dao.deleteComment(seq, loginUserId) > 0;
        }
    }
}