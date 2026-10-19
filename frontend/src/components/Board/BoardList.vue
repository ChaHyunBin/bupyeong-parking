<template>
  <div id="boardlist" class="container my-4">
    <h2 class="text-center mb-4">자유게시판</h2>

    <!-- 상단: 글쓰기 버튼 (체크 로직 추가) -->
    <div class="d-flex-end mb-3">
      <button @click="goWrite" class="btn btn-primary">글쓰기</button>
    </div>

    <!-- 1. 게시글 목록 테이블 -->
    <table class="custom-table text-center">
      <thead>
        <tr>
          <th style="width: 10%;">번호</th>
          <th style="width: 50%;">제목</th>
          <th style="width: 15%;">작성자</th>
          <th style="width: 15%;">작성일</th>
          <th style="width: 10%;">조회수</th>
        </tr>
      </thead>
      <tbody>
        <!-- 데이터가 없을 때 -->
        <tr v-if="boardList.length === 0">
          <td colspan="5" class="py-4 text-muted">등록된 게시글이 없습니다.</td>
        </tr>

        <!-- 통합 게시글 목록 (ispinned가 true면 공지 행 스타일 적용) -->
        <tr 
          v-for="board in boardList" 
          :key="board.seq"
          :class="{ 'notice-row': board.ispinned }"
        >
          <!-- 번호 또는 공지 배지 -->
          <td>
            <span v-if="board.ispinned" class="badge-notice">공지</span>
            <span v-else>{{ board.seq }}</span>
          </td>

          <!-- 제목 -->
          <td class="text-start">
            <router-link 
              :to="{ name: 'boarddetail', query: { seq: board.seq } }" 
              :class="board.ispinned ? 'notice-title' : 'board-title'"
            >
              {{ board.title }}
            </router-link>
          </td>

          <td><strong>{{ board.writerId || board.writerid }}</strong></td>
          <td>{{ formatDate(board.wdate) }}</td>
          <td>{{ board.readcount }}</td>
        </tr>
      </tbody>
    </table>

    <!-- 2. 페이지네이션 -->
    <nav class="pagination-container my-4" v-if="calculatedTotalPages > 0">
      <ul class="custom-pagination">
        <!-- 이전 버튼 -->
        <li class="page-item" :class="{ disabled: currentPage === 1 }">
          <button class="page-link" @click="getBoardList(currentPage - 1)">이전</button>
        </li>

        <!-- 페이지 번호 버튼 -->
        <li 
          v-for="p in calculatedTotalPages" 
          :key="p" 
          class="page-item" 
          :class="{ active: currentPage === p }"
        >
          <button class="page-link" @click="getBoardList(p)">{{ p }}</button>
        </li>

        <!-- 다음 버튼 -->
        <li class="page-item" :class="{ disabled: currentPage === calculatedTotalPages || calculatedTotalPages === 0 }">
          <button class="page-link" @click="getBoardList(currentPage + 1)">다음</button>
        </li>
      </ul>
    </nav>

    <!-- 3. 🔍 검색 영역 -->
    <div class="search-container my-4">
      <div class="search-box">
        <select v-model="choice" class="custom-select">
          <option value="all">제목+내용</option>
          <option value="writer">작성자</option>
        </select>

        <input 
          v-model="keyword" 
          @keyup.enter="handleSearch" 
          class="custom-input" 
          placeholder="검색어를 입력하세요" 
        />

        <button @click="handleSearch" class="btn btn-primary">검색</button>

        <!-- 검색 상태이거나 키워드가 입력되어 있을 때 노출되는 전체목록 버튼 -->
        <button 
          v-if="isSearched" 
          @click="resetSearch" 
          class="btn btn-secondary"
        >
          전체목록
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      boardList: [],
      choice: "all",
      keyword: "",
      currentPage: 1,
      totalPages: 1,
      totalCount: 0,
      isSearched: false, // 검색 상태를 판별하는 플래그 변수 추가
    };
  },
  computed: {
    calculatedTotalPages() {
      if (this.totalPages && this.totalPages > 1) {
        return this.totalPages;
      }
      return Math.ceil(this.totalCount / 10) || 1;
    }
  },
  mounted() {
    this.getBoardList(1);
  },
  methods: {
    // 글쓰기 버튼 클릭 시 로그인 여부 체크
    goWrite() {
      let loginData = 
        sessionStorage.getItem("login") || 
        sessionStorage.getItem("loginId") || 
        sessionStorage.getItem("userId") ||
        sessionStorage.getItem("user") ||
        localStorage.getItem("login") ||
        localStorage.getItem("loginId") ||
        localStorage.getItem("userId") ||
        localStorage.getItem("user");

      if (!loginData) {
        alert("로그인이 필요합니다");
        return;
      }
      this.$router.push("/boardwrite");
    },
    
    // 검색 실행 버튼 / 엔터 이벤트 핸들러
    handleSearch() {
      if (!this.keyword.trim()) {
        alert("검색어를 입력해주세요.");
        return;
      }
      this.isSearched = true;
      this.getBoardList(1);
    },

    // 전체목록 돌아가기 버튼 클릭 시 초기화
    resetSearch() {
      this.keyword = "";
      this.choice = "all";
      this.isSearched = false;
      this.getBoardList(1);
    },

    getBoardList(page = 1) {
      if (page < 1) page = 1;
      if (this.calculatedTotalPages > 0 && page > this.calculatedTotalPages) {
        page = this.calculatedTotalPages;
      }

      this.currentPage = page;

      axios
        .get("http://localhost:8282/boardlist", {
          params: {
            choice: this.choice,
            keyword: this.keyword,
            page: this.currentPage,
            size: 10,
          },
        })
        .then((resp) => {
          if (resp.data.list) {
            this.boardList = resp.data.list;
          } else if (resp.data.boards) {
            this.boardList = resp.data.boards;
          } else if (Array.isArray(resp.data)) {
            this.boardList = resp.data;
          } else {
            this.boardList = [];
          }

          if (resp.data.totalPages) {
            this.totalPages = resp.data.totalPages;
          }
          if (resp.data.totalCount !== undefined) {
            this.totalCount = resp.data.totalCount;
          }
        })
        .catch((err) => {
          console.error("목록 불러오기 실패:", err);
        });
    },
    formatDate(dateStr) {
      if (!dateStr) return "-";
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr.substring(0, 10);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
    },
  },
};
</script>


<style scoped src="@/components/Css/BoardList.styles.css"></style>