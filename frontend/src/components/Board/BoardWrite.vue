<template>
  <div id="boardwrite" class="container my-4">
    <br />
    <table class="table table-bordered">
      <colgroup>
        <col width="150" />
        <col width="500" />
      </colgroup>
      <tbody>
        <tr>
          <th>아이디</th>
          <td>
            <input v-model="writerId" class="form-control" readonly placeholder="로그인이 필요합니다" />
          </td>
        </tr>

        <!-- 📌 관리자(isAdmin = true)일 때만 상단 고정 옵션 표시 -->
        <tr v-if="isAdmin">
          <th>옵션</th>
          <td>
            <div class="form-check d-flex align-items-center gap-2">
              <input 
                type="checkbox" 
                id="ispinned" 
                v-model="ispinned" 
                class="form-check-input"
              />
              <label for="ispinned" class="form-check-label text-danger fw-bold m-0" style="cursor: pointer;">
                📌 상단 고정 (공지글로 등록)
              </label>
            </div>
          </td>
        </tr>

        <tr>
          <th>제목</th>
          <td>
            <input v-model="title" class="form-control" placeholder="제목을 입력하세요" />
          </td>
        </tr>
        <tr>
          <th>내용</th>
          <td>
            <textarea rows="15" cols="50" v-model="content" class="form-control" placeholder="내용을 입력하세요" />
          </td>
        </tr>
      </tbody>
    </table>
    <div class="text-center">
      <button @click="bbswrite" class="btn btn-primary">글쓰기</button>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      writerId: "",
      title: "",
      content: "",
      ispinned: false,
      isAdmin: false, // 관리자 여부 플래그
    };
  },
  mounted() {
    // 세션 및 로컬스토리지에서 로그인 정보 가져오기
    let loginData = 
      sessionStorage.getItem("login") || 
      sessionStorage.getItem("loginId") || 
      sessionStorage.getItem("userId") ||
      sessionStorage.getItem("user") ||
      localStorage.getItem("login") ||
      localStorage.getItem("loginId") ||
      localStorage.getItem("userId") ||
      localStorage.getItem("user");

    if (loginData) {
      try {
        let userObj = JSON.parse(loginData);
        this.writerId = userObj.id || userObj.writerId || userObj.userId || loginData;
        
        // 관리자 권한 체크 (DB/세션 설계에 따라 조건 추가/수정)
        if (
          userObj.auth === "ADMIN" || 
          userObj.role === "ADMIN" || 
          userObj.auth === 1 || 
          userObj.isAdmin === true || 
          this.writerId === "admin"
        ) {
          this.isAdmin = true;
        }
      } catch (e) {
        this.writerId = loginData;
        if (loginData === "admin") {
          this.isAdmin = true;
        }
      }
    }
  },
  methods: {
    bbswrite() {
      if (!this.writerId) {
        alert("로그인 정보가 없거나 유효하지 않은 아이디입니다. 다시 로그인 해주세요.");
        return;
      }
      if (!this.title.trim()) {
        alert("제목을 입력해주세요.");
        return;
      }
      if (!this.content.trim()) {
        alert("내용을 입력해주세요.");
        return;
      }

      const param = {
        writerId: this.writerId,
        title: this.title,
        content: this.content,
        // 관리자가 아니면 무조건 false로 백엔드 전달
        ispinned: this.isAdmin ? this.ispinned : false,
      };

      axios
        .post("http://localhost:8282/writeboard", param)
        .then((resp) => {
          if (resp.data === true) {
            alert("글이 성공적으로 등록되었습니다.");
            this.$router.push({ name: "boardlist" });
          } else {
            alert("글 등록에 실패했습니다. (작성자 아이디가 DB에 등록되어 있는지 확인해주세요)");
          }
        })
        .catch((err) => {
          console.error(err);
          alert("서버 통신 오류가 발생했습니다. 백엔드 콘솔 로그를 확인해주세요.");
        });
    },
  },
};
</script>

<style scoped src="@/components/Css/BoardWrite.styles.css"></style>