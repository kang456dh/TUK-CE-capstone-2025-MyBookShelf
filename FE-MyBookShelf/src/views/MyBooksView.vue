<template>
  <div class="my-books">
    <!-- 책장 헤더 -->
    <div class="bookshelf-header">
      <div class="bookshelf-controls">
        <div v-if="isRenaming">
          <input 
            v-model="newBookshelfName" 
            @keyup.enter="toggleRenameMode" 
            class="rename-input" 
            type="text" 
            placeholder="새 책장 이름 입력"
          />
        </div>

        <!-- 책장 리스트 -->
        <select v-else v-model="currentBookshelf" @change="selectBookshelf" class="bookshelf-select">
          <option value="null" disabled>---------- 책장을 추가해주세요 ----------</option>
          <option v-for="shelf in bookshelves" :key="shelf.bookshelfId" :value="shelf.bookshelfId">
            {{ shelf.bookshelfName }}
          </option>
        </select>

        <button @click="renameBookshelf" class="rename-button" :disabled="isNoBookshelf">
          {{ isRenaming ? "저장" : "이름 변경" }}
        </button>
        <button @click="openAddBookshelfModal" class="add-bookshelf-button">+</button>
        <button @click="deleteBookshelf" class="delete-bookshelf-button" :disabled="isNoBookshelf">🗑</button>
        <button @click="openSidebar" class="add-book-button" :disabled="!currentBookshelf">책 등록</button>

        <!-- 책장 추가 모달 -->
        <div v-if="isAddBookshelfModalOpen" class="add-bookshelf-modal">
          <div class="add-bookshelf-modal-content">
            <label for="new-bookshelf-name">책장 이름</label>
            <input
              type="text"
              id="new-bookshelf-name"
              v-model="newBookshelfNameForModal"
              placeholder="책장 이름 입력" />
            <button @click="addBookshelf" class="create-bookshelf-button">생성하기</button>
            <button @click="closeAddBookshelfModal" class="close-modal-button">취소</button>
          </div>
        </div>

        <!-- 네모난 책장 폼 -->
        <div class="bookshelf">
          <div class="book-grid">
            <div
              v-for="(book, index) in currentBookshelfBooks"
              :key="index"
              class="book-placeholder"
            >
              <div v-if="book.cover" class="book-cover">
                <img :src="book.cover || 'default-cover.jpg'" alt="책 표지" />
              </div>
            </div>
          </div>
        </div>

        <!-- 사이드바 -->
        <div v-if="isSidebarOpen" class="sidebar">
          <div class="sidebar-content">
            <button class="close-button" @click="closeSidebar">✖</button>
            <h3>책 등록</h3>
            <div class="registration-options">
              <button @click="setRegisterType('manual')" :class="{ active: registerType === 'manual' }">
                직접 등록
              </button>
              <button @click="setRegisterType('isbn')" :class="{ active: registerType === 'isbn' }">
                ISBN 등록
              </button>
              <button @click="setRegisterType('photo')" :class="{ active: registerType === 'photo' }">
                사진 등록
              </button>
            </div>

            <!-- 직접 등록 폼 -->
            <div v-if="registerType === 'manual'" class="manual-form">
              <label for="title">책 제목</label>
              <input type="text" id="title" v-model="manualTitle" placeholder="책 제목 입력" />
              <button @click="searchManual">검색</button>
            </div>

            <!-- 검색된 책들 -->
            <div v-if="searchResults.length" class="search-results">
              <h4>검색된 책들:</h4>

              <!-- 책 목록 -->
              <ul>
                <li v-for="(book, index) in paginatedResults" :key="index">
                  <div class="search-book-item">
                    <div class="book-cover">
                      <img :src="book.cover" alt="책 표지" />
                    </div>
                    <div class="book-info">
                      <p class="book-title" :title="book.title">
                        {{ book.title.length > 10 ? book.title.slice(0, 10) + '...' : book.title }}
                      </p>
                      <p class="book-author">{{ book.author }}</p>
                      <button @click="selectBook(book)" class="select-book-button">선택</button>
                    </div>
                  </div>
                </li>
              </ul>

              <!-- 페이지네이션 버튼 -->
              <div class="pagination">
                <button @click="changePage(currentPage - 1)" :disabled="currentPage === 1">이전</button>
                <span>{{ currentPage }} / {{ totalPages }}</span>
                <button @click="changePage(currentPage + 1)" :disabled="currentPage === totalPages">다음</button>
              </div>
            </div>

            <!-- ISBN 등록 폼 -->
            <div v-if="registerType === 'isbn'" class="isbn-form">
              <label for="isbn">ISBN</label>
              <input type="text" id="isbn" v-model="isbn" placeholder="ISBN 입력" />
              <button @click="searchISBN">검색</button>
            </div>

            <!-- 사진 등록 폼 -->
            <div v-if="registerType === 'photo'" class="photo-options">
              <button @click="openFileInput" class="file-upload-button">첨부파일</button>
              <button @click="openCamera" class="camera-button">사진 촬영</button>
            </div>

            <div class="sidebook-grid">
              <div
                v-for="book in books"
                :key="book.id"
                class="sidebook-item"
                :class="{ selected: selectedBooks.includes(book) }"
                @click="toggleSelection(book)"
              >
                <img :src="book.cover" alt="book cover" />
                <p>{{ book.title }}</p>
                <p>{{ book.author }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

axios.defaults.baseURL = 'http://localhost:8081'; // 기본 API 주소 설정

export default {
  name: "MyBooksView",
  data() {
    return {
      bookshelves: [], // 기본값은 빈 배열로 설정
      currentBookshelf: null, // 기본값은 'null'로 설정
      isRenaming: false,
      newBookshelfName: "",
      newBookshelfNameForModal: "", // 모달에 입력할 새 책장 이름
      isSidebarOpen: false,
      registerType: "manual",
      manualTitle: "",
      isbn: "",
      isAddBookshelfModalOpen: false, // 책장 추가 모달 열기 여부
      searchResults: [], // 검색된 책 정보
      books: [], // 책 배열 초기화
      currentPage: 1, // 현재 페이지
      booksPerPage: 6, // 페이지당 책 개수
    };
  },
  created() {
    this.fetchBookshelves();
  },

  computed: {
    currentBookshelfBooks() {
      const shelf = this.bookshelves.find(
        (shelf) => shelf.name === this.currentBookshelf
      );
      return shelf ? shelf.books : [];
    },

    // 사이드바 결과 페이지 쪽수
    paginatedResults() {
      const start = (this.currentPage - 1) * this.booksPerPage;
      const end = this.currentPage * this.booksPerPage;
      return this.searchResults.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.searchResults.length / this.booksPerPage);
    },
  },

  methods: {
    changePage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
    },

    toggleRenameMode() {
      if (this.currentBookshelf === null) {
        alert("책장을 먼저 선택해주세요.");
        return;
      }
      if (this.isRenaming) {
        const shelf = this.bookshelves.find(
          (shelf) => shelf.name === this.currentBookshelf
        );
        if (shelf) shelf.name = this.newBookshelfName;
        this.currentBookshelf = this.newBookshelfName;
      } else {
        this.newBookshelfName = this.currentBookshelf;
      }
      this.isRenaming = !this.isRenaming;
    },

    // 특정 사용자의 책장 불러오기 (책장 목록 조회 API)
    async fetchBookshelves() {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user ? user.userId : null; // userId를 가져옵니다.

    

      try {
        const response = await axios.get(`/api/bookshelf/${userId}`); // userId를 URL에 포함
        this.bookshelves = response.data.result || [];
      } catch (error) {
        console.error('책장 목록 조회 실패:', error);
      }
    },

    // 책장 생성 API
    async addBookshelf() {
      const user = JSON.parse(localStorage.getItem('user'));
      //const userId = user ? user.id : null;  // userId를 가져옵니다.
      const userId = user ? user.userId : null;  // userId를 가져옵니다.

      

      if (!this.newBookshelfNameForModal.trim()) {
        alert("책장 이름을 입력해 주세요.");
        return;
      }

      try {
        const response = await axios.post('/api/bookshelf/create', {
          userId: userId,
          bookshelfName: this.newBookshelfNameForModal,
        });

        if (!response.ok) {
          throw new Error(`서버 오류: ${response.status}`); // 404 등 상태 코드 체크
        }

        const data = await response.json();
        if (data.isSuccess) { // 응답 상태를 isSuccess로 확인
          alert("책장이 추가되었습니다!");
          this.bookshelves.push({
            bookshelfId: data.data.id, // 응답에서 책장 ID를 가져옵니다.
            bookshelfName: this.newBookshelfNameForModal,
          });
          this.newBookshelfNameForModal = "";
          this.isAddBookshelfModalOpen = false;
        } else {
          alert("책장 추가 실패: " + data.message);
        }
      } catch (error) {
        console.error("책장 추가 중 오류 발생:", error);
      }
    },

    // 책장 이름 수정 API
    async renameBookshelf() {
      if (!this.selectedBookshelf) return; // 선택된 책장이 없을 경우 처리
      
      const newName = prompt('새 책장 이름을 입력하세요:', '');
      if (!newName) return; // 새 이름이 입력되지 않으면 처리

      try {
        const response = await axios.patch("/api/bookshelf/edit", {
          bookshelfId: this.selectedBookshelf, // 수정할 책장 ID
          bookshelfName: newName, // 새 책장 이름
        });

        if (response.data.isSuccess) {
          alert("책장 이름이 수정되었습니다!");
          this.fetchBookshelves(); // 변경된 데이터 다시 가져오기
        } else {
          alert("책장 이름 수정 실패: " + response.data.message);
        }
      } catch (error) {
        console.error('책장 이름 수정 실패:', error);
      }
    },

    // 책장 삭제 API
    async deleteBookshelf() {
      if (!this.selectedBookshelf) return; // 선택된 책장이 없을 경우 처리
      
      if (!confirm('정말 이 책장을 삭제하시겠습니까?')) return;

      try {
        const response = await axios.delete(`/api/bookshelf/delete/${this.selectedBookshelf}`);

        if (response.data.isSuccess) { // isSuccess로 확인
          alert("책장이 삭제되었습니다!");
          this.fetchBookshelves(); // 변경된 데이터 다시 가져오기
        } else {
          alert("책장 삭제 실패: " + response.data.message);
        }
      } catch (error) {
        console.error('책장 삭제 실패:', error);
      }
    },

    selectBookshelf() {
      this.selectedBookshelf = this.currentBookshelf; // 현재 선택된 책장 ID를 저장
    },

    openAddBookshelfModal() {
      this.isAddBookshelfModalOpen = true;
    },

    closeAddBookshelfModal() {
      this.isAddBookshelfModalOpen = false;
      this.newBookshelfNameForModal = "";
    },

    openSidebar() {
      this.isSidebarOpen = true;
    },
    closeSidebar() {
      this.isSidebarOpen = false;
      this.manualTitle = "";
      this.isbn = "";
    },

    // 검색된 책을 책장에 넣는 작업
    async selectBook(book) {
      if (!this.currentBookshelf) {
        alert("책장을 먼저 선택해주세요.");
        return;
      }
      try {
        await axios.post(`/api/bookshelf/${this.currentBookshelf}/addBook`, {
          bookId: book.book.id,
        });
        
        // 책장 목록을 다시 불러와 최신 상태로 반영
        this.fetchBookshelves(); 

        // 책장에 추가된 책을 화면에 즉시 반영
        alert(`'${book.title}' 책이 책장에 추가되었습니다.`);
      } catch (error) {
        console.error('책 추가 실패:', error);
      }
    },

    setRegisterType(type) {
      this.registerType = type;
    },

    // 알라딘 도서 검색 API (제목 검색)
    async searchManual() {
      try {
        const response = await axios.get(`/api/books/search`, {
          params: { query: this.manualTitle },
        });
        this.searchResults = response.data.books.map(book => ({
          title: book.title,
          author: book.author,
          publisher: book.publisher,
          isbn: book.isbn,
          cover: book.cover,
        }));
        this.currentPage = 1;
      } catch (error) {
        console.error("책 검색 오류:", error);
      }
    },

    // 알라딘 도서 검색(ISBN)
    async searchISBN() {
      try {
        const response = await axios.get(`/api/books/search`, {
          params: { query: this.isbn },
        });
        const book = response.data.books[0];
        this.searchResults = [{
          title: book.title,
          author: book.author,
          publisher: book.publisher,
          isbn: book.isbn,
          cover: book.cover,
        }];
      } catch (error) {
        console.error("ISBN 검색 오류:", error);
      }
    },

    openFileInput() {
      const fileInput = document.createElement('input');
      fileInput.type = 'file';
      fileInput.accept = 'image/*';  // 이미지 파일만 선택
      fileInput.click();
      
      fileInput.addEventListener('change', () => {
        const file = fileInput.files[0];
        if (file) {
          console.log("첨부된 파일:", file);
        }
      });
    },
    openCamera() {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        navigator.mediaDevices.getUserMedia({ video: true })
          .then(() => {
            console.log("카메라가 열렸습니다.");
          })
          .catch((err) => {
            console.error("카메라 연결 실패:", err);
          });
      } else {
        alert("모바일에서만 지원됩니다.");
      }
    },

  },
};
</script>


<style scoped>
.my-books {
  padding: 20px;
}

.bookshelf-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.bookshelf-controls {
  display: flex;
  align-items: center;
}

.bookshelf-select {
  padding: 5px;
  margin-right: 10px;
  width: 300px;
}

.rename-button,
.add-bookshelf-button,
.delete-bookshelf-button,
.add-book-button,
.photo-registration-button {
  background-color: #ffa500;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  margin-right: 5px;
}

.add-bookshelf-button {
  background-color: #28a745;
  font-size: 12.5pt;
}

.delete-bookshelf-button {
  background-color: #dc3545;
  font-size: 10.5pt;
}

.add-book-button {
  background-color: #007bff; /* 책 등록 버튼 색 */
}

.photo-registration-button {
  background-color: #ff5722; /* 사진 등록 버튼 색 */
}

.bookshelf {
  background-color: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
  border: 1px solid #ddd;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 30px;
}

.book-placeholder {
  width: 80%;
  padding-top: 100%;
  background-color: #e9ecef;
  border: 1px solid #ddd;
  border-radius: 8px;
  position: relative;
  display: flex;
  justify-content: center; /* 책 표지를 가로 중앙으로 정렬 */
  align-items: center; /* 책 표지를 세로 중앙으로 정렬 */
}

.search-results ul {
  display: grid;
  grid-template-columns: repeat(2, 1fr); /* 2개씩 배치 */
  gap: 10px;
}

.search-book-item {
  display: flex;
  align-items: stretch;
  gap: 10px;
  border: 1px solid #ddd;
  padding: 10px;
  background: white;
  border-radius: 5px;
}

.book-cover {
  display: flex;
  align-items: stretch;
  width: 100px; /* 원하는 너비 설정 */
}

.book-cover img {
  width: 100%;
  height: auto; /* 자동으로 비율에 맞게 높이를 설정 */
  object-fit: cover;
}

.book-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1;
}

.book-title {
  font-weight: bold;
  font-size: 14px;
}

.book-author {
  font-size: 12px;
  color: gray;
}

.select-book-button {
  margin-top: 5px;
  padding: 5px 10px;
  background-color: #007bff;
  color: white;
  border: none;
  cursor: pointer;
  border-radius: 4px;
}

.select-book-button:hover {
  background-color: #0056b3;
}

/* 사이드바 스타일 */
.sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 50vw;
  height: 100%;
  background-color: #fff;
  box-shadow: -2px 0 4px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 25px;
}

.sidebar button {
  background-color: #FFA500; /* 주황색 */
  color: white; /* 글씨 색은 흰색 */
  border: none; /* 기본 border 제거 */
  border-radius: 4px; /* 버튼 모서리 둥글게 */
  padding: 8px 10px; /* 버튼 크기 조정 */
  cursor: pointer; /* 마우스 커서가 버튼에 올려지면 손가락 모양으로 변경 */
  margin-bottom: 20px; /* 버튼들 간의 간격 */
}

.rename-button:hover {
  background-color: #e68900; /* 어두운 주황색 */
}

.add-bookshelf-button:hover {
  background-color: #218838; /* 어두운 초록색 */
}

.delete-bookshelf-button:hover {
  background-color: #c82333; /* 어두운 빨간색 */
}

.add-book-button:hover {
  background-color: #0056b3; /* 어두운 파란색 */
}

.photo-registration-button:hover {
  background-color: #e64a19; /* 어두운 오렌지색 */
}

/* 파일 입력 스타일 */
.file-input,
.camera-button {
  margin-top: 15px;
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 15px;
  border-radius: 4px;
  cursor: pointer;
}

.file-input:hover,
.camera-button:hover {
  background-color: #0056b3;
}

/* 사이드바 버튼 hover 효과 */
.sidebar button:hover {
  background-color: #e69500; /* 어두운 주황색 */
}

.rename-input {
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.add-bookshelf-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 300px;
  padding: 20px;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1000;
}

.add-bookshelf-modal-content {
  display: flex;
  flex-direction: column;
}

.add-bookshelf-modal input {
  margin-bottom: 30px;
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.create-bookshelf-button {
  background-color: #28a745;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
  margin-bottom: 10px;
}

.create-bookshelf-button:hover {
  background-color: #218838;
}

.close-modal-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
}

.close-modal-button:hover {
  background-color: #c82333;
}

/* 버튼을 나란히 배치 */
.sidebar .registration-options {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-top: 20px;
}

.sidebar .registration-options button {
  padding: 12px 24px;
  border: 1px solid #ddd;
  border-radius: 6px;
  cursor: pointer;
  background-color: #ffffff;
  transition: background-color 0.3s, border-color 0.3s;
  display: flex;
  justify-content: center;
  align-items: center;
  flex-grow: 1; /* 버튼들이 고르게 배치되도록 함 */
}

.sidebar .registration-options button:hover {
  background-color: #f5f5f5;
  border-color: #ccc;
}

/* active 버튼에 스타일 추가 */
.sidebar .registration-options button.active {
  background-color: #4caf50;
  color: white;
  border-color: #45a049;
}

/* 비활성화된 버튼 색상 */
.sidebar .registration-options button:not(.active):not(:disabled) {
  background-color: #f0f0f0;
  border-color: #ccc;
  color: #888; /* 연한 회색 */
}

/* 비활성화된 버튼 상태 */
.sidebar .registration-options button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
  border-color: #bbb;
  color: #bbb; /* 연한 회색으로 글자 색상 */
}

/* input 및 버튼 스타일 */
.sidebar .manual-form input,
.sidebar .isbn-form input {
  width: 100%;
  padding: 12px;
  margin-top: 15px;
  border: 1px solid #ddd;
  border-radius: 6px;
}

.sidebar .file-upload-button,
.sidebar .camera-button {
  padding: 12px 24px;
  background-color: #2196f3;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  transition: background-color 0.3s;
  display: block;
  width: 100%;
}

.sidebar .file-upload-button:hover,
.sidebar .camera-button:hover {
  background-color: #1976d2;
}

.sidebook-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr); /* 한 줄에 3개씩 */
  gap: 16px; /* 책들 간 간격 */
  max-height: 400px; /* 사이드바 최대 높이 */
  overflow-y: auto; /* 스크롤 가능하게 */
  padding: 10px;
}

.sidebook-item {
  background-color: #f5f5f5;
  border: 1px solid #ddd;
  padding: 10px;
  border-radius: 8px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sidebook-item.selected {
  background-color: #87ceeb; /* 선택된 책 색상 */
}

.sidebook-item:hover {
  background-color: #e0e0e0; /* 마우스 hover 시 색상 변화 */
}
</style>