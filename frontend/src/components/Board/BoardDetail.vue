<template>
  <div id="boarddetail" class="container my-4">
    <br />
    <div class="center">
      <!-- 1. 게시글 상세 및 수정 테이블 -->
      <table class="table table-bordered">
        <colgroup>
          <col width="200" />
          <col width="500" />
        </colgroup>
        <tbody>
          <tr>
            <th>작성자</th>
            <td>{{ writerId }}</td>
          </tr>
          <tr>
            <th>작성일</th>
            <td>{{ formatDate(wdate) }}</td>
          </tr>
          <tr>
            <th>조회수</th>
            <td>{{ readcount }}</td>
          </tr>
          <tr>
            <th>제목</th>
            <td>
              <input 
                v-if="isEditMode" 
                type="text" 
                v-model="editTitle" 
                class="form-control" 
              />
              <span v-else>{{ title }}</span>
            </td>
          </tr>
          <tr>
            <th>내용</th>
            <td>
              <textarea
                v-if="isEditMode"
                rows="15"
                cols="50"
                v-model="editContent"
                class="form-control"
              ></textarea>
              <textarea
                v-else
                rows="15"
                cols="50"
                v-model="content"
                readonly
                class="form-control"
              ></textarea>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 2. 게시글 버튼 영역 -->
      <div class="d-flex justify-content-end gap-2 my-3">
        <template v-if="!isEditMode">
          <button v-if="isMyBoard" @click="enableEdit" class="btn btn-warning me-2">수정</button>
          <button v-if="isMyBoard" @click="deleteBoard" class="btn btn-danger me-2">삭제</button>
          <router-link to="/boardlist" class="btn btn-secondary">목록으로</router-link>
        </template>

        <template v-else>
          <button @click="updateBoard" class="btn btn-success me-2">수정 완료</button>
          <button @click="cancelEdit" class="btn btn-secondary me-2">취소</button>
        </template>
      </div>

      <hr class="my-4" />

      <!-- 3. 댓글 영역 -->
      <div style="width: 700px; margin: 0 auto; text-align: left !important; display: block;">
        <h5 class="mb-3 fw-bold" style="text-align: left;">댓글 ({{ commentList.length }})</h5>

        <!-- 댓글 작성 입력창 -->
        <div class="input-group mb-4">
          <input 
            type="text" 
            v-model="newComment" 
            class="form-control" 
            placeholder="댓글을 남겨보세요." 
            @keyup.enter="addComment"
          />
          <button @click="addComment" class="btn btn-primary px-4">등록</button>
        </div>

        <!-- 댓글 목록 리스트 -->
        <ul class="list-group list-group-flush border-top" style="text-align: left;">
          <li 
            v-for="comment in commentList" 
            :key="comment.seq" 
            class="list-group-item py-3 px-0 d-flex justify-content-between align-items-start"
            style="text-align: left;"
          >
            <!-- 왼쪽: 작성자 아이디, 작성 시간, 내용 -->
            <div style="text-align: left; width: 100%;">
              <div class="d-flex align-items-center mb-1">
                <span class="fw-bold text-dark me-2" style="font-size: 0.95rem;">
                  {{ comment.writerId || comment.writerid || comment.id }}
                </span>
                <span class="text-muted" style="font-size: 0.8rem;">
                  {{ formatDate(comment.wdate || comment.regdate) }}
                </span>
              </div>
              <div class="text-break" style="color: #495057; white-space: pre-wrap; line-height: 1.4; font-size: 0.95rem;">
                {{ comment.content }}
              </div>
            </div>

            <!-- 오른쪽: 삭제 버튼 -->
            <div class="ms-3 flex-shrink-0">
              <button 
                v-if="isMyComment(comment.writerId || comment.writerid || comment.id)" 
                @click="deleteComment(comment.seq)" 
                class="btn btn-sm btn-outline-secondary"
              >
                삭제
              </button>
            </div>
          </li>
          
          <li v-if="commentList.length === 0" class="list-group-item text-center text-muted py-5 px-0">
            첫 번째 댓글을 남겨보세요!
          </li>
        </ul>
      </div>

      <br />
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    let currentUserId = "";
    let currentUserAuth = 3; // 기본값 일반 유저 (3)
    const savedLogin = sessionStorage.getItem("login");

    if (savedLogin) {
      try {
        const loginObj = JSON.parse(savedLogin);
        currentUserId = loginObj.id || "";
        currentUserAuth = Number(loginObj.auth) || 3; // DB auth 컬럼 값 매핑 (1: 관리자)
      } catch (e) {
        console.error("세션 로그인 정보 파싱 실패:", e);
      }
    }

    return {
      loginUser: currentUserId,
      loginAuth: currentUserAuth, // 관리자 권한 구분을 위한 값

      seq: this.$route.query.seq,
      writerId: "",
      title: "",
      content: "",
      wdate: "",
      readcount: 0,

      isEditMode: false,
      editTitle: "",
      editContent: "",

      newComment: "",
      commentList: []
    };
  },
  computed: {
    // 관리자(auth === 1) 여부 확인
    isAdmin() {
      return this.loginAuth === 1;
    },

    // 작성자 본인이거나 관리자이면 수정/삭제 버튼 허용
    isMyBoard() {
      if (!this.loginUser) return false;
      if (this.isAdmin) return true; // 관리자는 프리패스
      return String(this.loginUser).trim() === String(this.writerId).trim();
    }
  },
  mounted() {
    this.getBbsDetail();
    this.getCommentList();
  },
  methods: {
    // 작성자 본인이거나 관리자이면 댓글 삭제 버튼 허용
    isMyComment(commentWriterId) {
      if (!this.loginUser) return false;
      if (this.isAdmin) return true; // 관리자는 프리패스
      if (!commentWriterId) return false;
      return String(this.loginUser).trim() === String(commentWriterId).trim();
    },

    getBbsDetail() {
      axios
        .post(`http://localhost:8282/detailboard?seq=${this.seq}`)
        .then((resp) => {
          let bbs = resp.data;
          this.writerId = bbs.writerId || bbs.writerid || bbs.id || "";
          this.title = bbs.title;
          this.content = bbs.content;
          this.wdate = bbs.wdate;
          this.readcount = bbs.readcount;
        })
        .catch((err) => {
          alert("상세조회 실패: " + err);
        });
    },

    enableEdit() {
      if (!this.isMyBoard) {
        alert("본인 글만 수정할 수 있습니다.");
        return;
      }
      this.editTitle = this.title;
      this.editContent = this.content;
      this.isEditMode = true;
    },

    cancelEdit() {
      this.isEditMode = false;
    },

    updateBoard() {
      if (!this.editTitle.trim()) {
        alert("제목을 입력해주세요.");
        return;
      }
      if (!this.editContent.trim()) {
        alert("내용을 입력해주세요.");
        return;
      }

      axios
        .post("http://localhost:8282/updateboard", {
          seq: String(this.seq),
          writerId: String(this.loginUser),
          title: this.editTitle,
          content: this.editContent
        })
        .then((resp) => {
          if (resp.data === true) {
            alert("성공적으로 수정되었습니다.");
            this.title = this.editTitle;
            this.content = this.editContent;
            this.isEditMode = false;
          } else {
            alert("수정 실패: 권한이 없거나 오류가 발생했습니다.");
          }
        })
        .catch((err) => alert("수정 통신 실패: " + err));
    },

    deleteBoard() {
      if (!this.isMyBoard) {
        alert("삭제 권한이 없습니다.");
        return;
      }

      if (confirm("정말 이 게시글을 삭제하시겠습니까?")) {
        axios
          .post("http://localhost:8282/deleteboard", {
            seq: String(this.seq),
            writerId: String(this.loginUser)
          })
          .then((resp) => {
            if (resp.data === true) {
              alert("게시글이 삭제되었습니다.");
              this.$router.push("/boardlist");
            } else {
              alert("삭제 실패: 권한이 없거나 오류가 발생했습니다.");
            }
          })
          .catch((err) => alert("삭제 통신 실패: " + err));
      }
    },

    getCommentList() {
      axios
        .get("http://localhost:8282/commentlist", { params: { boardSeq: this.seq } })
        .then((resp) => {
          this.commentList = resp.data;
        })
        .catch((err) => {
          console.error("댓글 목록 조회 실패:", err);
        });
    },

    addComment() {
      if (!this.newComment.trim()) {
        alert("댓글 내용을 입력해주세요.");
        return;
      }

      axios
        .post("http://localhost:8282/addcomment", {
          boardSeq: String(this.seq),
          writerId: String(this.loginUser),
          content: this.newComment
        })
        .then((resp) => {
          if (resp.data === true) {
            this.newComment = "";
            this.getCommentList();
          } else {
            alert("댓글 등록에 실패했습니다.");
          }
        })
        .catch((err) => alert("댓글 등록 통신 실패: " + err));
    },

    deleteComment(commentSeq) {
      if (confirm("댓글을 삭제하시겠습니까?")) {
        axios
          .post("http://localhost:8282/deletecomment", {
            seq: String(commentSeq),
            writerId: String(this.loginUser)
          })
          .then((resp) => {
            if (resp.data === true) {
              this.getCommentList();
            } else {
              alert("댓글 삭제 실패: 권한이 없거나 오류가 발생했습니다.");
            }
          })
          .catch((err) => alert("댓글 삭제 통신 실패: " + err));
      }
    },

    formatDate(dateStr) {
      if (!dateStr) return "-";
      const d = new Date(dateStr);
      if (isNaN(d.getTime())) return dateStr.substring(0, 10);
      return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, "0")}-${String(d.getDate()).padStart(2, "0")} ${String(d.getHours()).padStart(2, "0")}:${String(d.getMinutes()).padStart(2, "0")}`;
    }
  },
};
</script>

<style scoped src="@/components/Css/BoardDetail.styles.css"></style>