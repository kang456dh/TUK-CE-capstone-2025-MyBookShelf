<template>
  <div class="my-books">
    <!-- 책장 헤더 -->
    <div class="bookshelf-header">
      <div class="bookshelf-controls">
        <div v-if="isRenaming">
          <input v-model="newBookshelfName" @keyup.enter="renameBookshelf"
          class="rename-input" type="text" placeholder="새 책장 이름 입력"/>
        </div>

        <!-- 책장 리스트 -->
        <select v-else v-model="currentBookshelf" @change="selectBookshelf" class="bookshelf-select">
          <option value="null" disabled> ------- 책장을 추가해주세요 ------- </option>
          <option v-for="shelf in bookshelves" :key="shelf.bookshelfId" :value="shelf.bookshelfId">
            {{ [ shelf.bookshelfName ] }}
          </option>
        </select>

        <button @click="toggleRenameMode" class="rename-button" :disabled="isNoBookshelf">
          {{ isRenaming ? "저장" : "이름 변경" }}
        </button>
        <button @click="openAddBookshelfModal" class="add-bookshelf-button">+ 책장 생성</button>
        <button @click="deleteBookshelf" class="delete-bookshelf-button" :disabled="isNoBookshelf">🗑 책장 삭제</button>
        <button @click="openSidebar" class="add-book-button" :disabled="!currentBookshelf">책 등록</button>

        <button @click="openRecommendationModal" class="recommend-button">책 추천 받기</button>
        <!-- <button @click="deleteBooksFromShelf" class="edit-button">책장 편집</button> -->
        <button @click="toggleEditMode" class="edit-button">책장 편집</button>
      </div>
    </div>

    <!-- 네모난 책장 폼 -->
    <div class="bookshelf">
      <div class="book-grid">
        <div v-for="(book, index) in currentBookshelfBooks" 
          :key="index" class="book-placeholder" @contextmenu.prevent="showContextMenu($event, book)">
          <div v-if="book.cover" class="bookshelfbook-cover">
            <img :src="book.cover || 'default-cover.jpg'" alt="책 표지" />
          </div>
          <div class="bookshelf-info">
            <div class="bookshelf-title">{{ truncateTitleBeforeSpecialChar(book.title) }}</div>
            <div class="bookshelf-author">{{ book.author.length > 20 ? book.author.slice(0, 20) + '...' : book.author }}</div>
            <button v-if="isEditing" @click="removeBook(book)" class="remove-book-button">-</button>
          </div>
        </div>
      </div>

      <!-- 컨텍스트 메뉴 -->
      <div v-if="contextMenuVisible" class="context-menu" :style="{ top: `${contextMenuY}px`, left: `${contextMenuX}px` }">
        <button @click="viewBookInfo()">책 정보</button>
      </div>
    </div>


    <!-- 책장 추가 모달 -->
    <div v-if="isAddBookshelfModalOpen" class="add-bookshelf-modal">
      <div class="add-bookshelf-modal-content">
        <label for="new-bookshelf-name">책장 이름</label>
        <input type="text" id="new-bookshelf-name" v-model="newBookshelfNameForModal" placeholder="책장 이름 입력" />
        <button @click="addBookshelf" class="create-bookshelf-button">책장 생성하기</button>
        <button @click="closeAddBookshelfModal" class="close-modal-button">취소</button>
      </div>
    </div>

    <!-- 추천받기 모달 -->
    <div v-if="isRecommendationModalOpen" class="recommendation-modal">
      <div class="recommendation-modal-content">
        <h3>추천받기</h3>
        <div class="recommendation-options">
          <button @click="setRecommendationType('age')" :class="{ active: recommendationType === 'age' }">연령별 대출순 추천</button>
          <button @click="setRecommendationType('rating')" :class="{ active: recommendationType === 'rating' }">평점 추천</button>
          <button @click="setRecommendationType('keyword')" :class="{ active: recommendationType === 'keyword' }">키워드 추천</button>
        </div>
        <div class="recommendation-options">
          <button @click="fetchRecommendations" class="recommend-books-button">책 5권 추천받기</button>
          <button @click="closeRecommendationModal" class="close-modal-button">취소</button>
        </div>
      </div>
    </div>

    <!-- 추가 평점 모달 -->
    <div v-if="isAdditionalRatingModalOpen" class="additional-rating-modal">
      <div class="additional-rating-modal-content">
        <div class="additional-rating-header">도서 추천 결과</div>
        <div class="rating-list">
          <div class="rating-item" v-for="book in recommendations" :key="book.isbn">
            <div class="rating-cover">
              <img :src="book.cover" alt="책 표지" />
            </div>
            <div class="rating-info">
              <div class="rating-title">{{ book.title }}</div>
              <div class="rating-author">저자: {{ book.author.length > 10 ? book.author.slice(0, 10) + '...' : book.author }}</div>
              <div class="rating-category">카테고리: {{ book.categoryName }}</div>
              <div class="rating-score">가중평점: {{ book.weightedRatingScore.toFixed(2) }}</div>
            </div>
          </div>
        </div>
        <button @click="closeAdditionalRatingModal" class="close-rating-modal-button">닫기</button>
      </div>
    </div>
    <!-- 확인 모달 -->
    <div v-if="showConfirmModal" class="confirm-modal">
      <div class="confirm-modal-content">
        <p>'{{ selectedBook.title }}' 을 저장하시겠습니까?</p>
        <div class="confirm-modal-button-container">
          <button @click="confirmAddBook">예</button>
          <button @click="cancelAddBook">아니요</button>
        </div>
      </div>
    </div>

    <!-- 사이드바 -->
    <div v-if="isSidebarOpen" class="sidebar">
      <div class="sidebar-content">
        <button class="close-button" @click="closeSidebar">✖</button>
        <div class="registration-options">
          <button @click="setRegisterType('manual')" :class="{ active: registerType === 'manual' }">직접 등록</button>
          <button @click="setRegisterType('isbn')" :class="{ active: registerType === 'isbn' }">ISBN 등록</button>
          <button @click="setRegisterType('photo')" :class="{ active: registerType === 'photo' }">사진 등록</button>
        </div>

        <!-- 직접 등록 폼 -->
        <div v-if="registerType === 'manual'" class="manual-form">
          <input type="text" id="title" v-model="manualTitle" placeholder="책 제목 입력" />
          <button @click="searchManual">검색</button>
        </div>

        <!-- ISBN 등록 폼 -->
        <div v-if="registerType === 'isbn'" class="isbn-form">
          <input type="text" id="isbn" v-model="isbn" placeholder="ISBN 입력" />
          <button @click="searchISBN">검색</button>
        </div>

        <!-- 사진 등록 폼 -->
        <div v-if="registerType === 'photo'" class="photo-options">
          <button @click="openFileInput" class="file-upload-button">첨부파일</button>
          <button @click="openCamera" class="camera-button">사진 촬영</button>
        </div>

        <!-- 검색된 책들 -->
        <div v-if="searchResults.length" class="search-results">
          <h4>검색된 책들:</h4>
          <ul>
            <li v-for="(book, index) in paginatedResults" :key="index">
              <div class="search-book-item">
                <div class="sidebook-cover">
                  <img :src="book.cover" alt="책 표지" />
                </div>
                <div class="sidebook-info">
                  <p class="sidebook-title" :title="book.title">
                    {{ book.title.length > 25 ? book.title.slice(0, 25) + '...' : book.title }}
                  </p>
                  <p class="sidebook-author">{{ book.author }}</p>
                  <button @click="selectBook(book)" class="sideselect-book-button">선택</button>
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

        <!-- 확인 모달 -->
        <div v-if="showConfirmModal" class="confirm-modal">
          <div class="confirm-modal-content">
            <p>'{{ selectedBook.title }}' 을 저장하시겠습니까?</p>
            <div class="confirm-modal-button-container">
              <button @click="confirmAddBook">예</button>
              <button @click="cancelAddBook">아니요</button>
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
      newBookshelfName: "",  // 입력 필드에 사용할 새 책장 이름
      isNoBookshelf: false, // 선택된 책장이 없을 때 처리할 상태
      newBookshelfNameForModal: "", // 모달에 입력할 새 책장 이름
      isSidebarOpen: false,
      registerType: "manual",
      manualTitle: "",
      isbn: "",
      isAddBookshelfModalOpen: false, // 책장 추가 모달 열기 여부
      isRecommendationModalOpen: false, // 추천받기 모달 열기 여부
      isAdditionalRatingModalOpen: false,
      recommendationType: "", // 추천 타입
      searchResults: [], // 검색된 책 정보
      books: [], // 책 배열 초기화
      currentPage: 1, // 현재 페이지
      booksPerPage: 6, // 페이지당 책 개수
      showConfirmModal: false, // 책 선택버튼 누르면 뜨는 창
      isEditing: false,  // 책장 편집 모드 상태 추가
      contextMenuVisible: false, // 컨텍스트 메뉴 표시 여부
      contextMenuX: 0, // 컨텍스트 메뉴 X 좌표
      contextMenuY: 0, // 컨텍스트 메뉴 Y 좌표
      selectedBook: null, // 선택된 책
      recommendations: [], // 추천받은 책 목록
      isBooksModalOpen: false, // 책 추천 결과 모달 상태
    };
  },

  created() {
    this.fetchBookshelves();
  },

  // 마우스 클릭 시 메뉴를 닫기 위해 이벤트 추가
  mounted() {
    document.addEventListener('click', this.closeContextMenu);
  },
  beforeUmount() {
    document.removeEventListener('click', this.closeContextMenu);
  },

  computed: {
    currentBookshelfBooks() {
      const shelf = this.bookshelves.find(
        // (shelf) => shelf.name === this.currentBookshelf
        (shelf) => shelf.bookshelfId === this.currentBookshelf // ID로 비교
      );
      return shelf ? shelf.book : []; // 책장에 등록된 책 목록 반환
    },

    // 사이드바 결과 페이지 쪽수
    paginatedResults() {
      const start = (this.currentPage - 1) * this.booksPerPage;
      const end = this.currentPage * this.booksPerPage;
      return this.searchResults.slice(start, end);
    },
    totalPages() {
      return Math.ceil(this.searchResults.length / this.booksPerPage); // 총 페이지 수
    },
  },

  methods: {
    closeBooksModal() {
      this.isBooksModalOpen = false; // 책 추천 결과 모달 닫기
    },
    
    truncateTitleBeforeSpecialChar(title) {
    // 특정 특수 기호가 나타나는 위치를 찾음
    const index = title.search(/[-:/]/); // '-', ':', '/' 중 첫 번째 문자의 인덱스

    // 특수 기호가 없으면 전체 제목을 반환하고, 있으면 그 이전까지 반환
    return index === -1 ? title : title.slice(0, index);
    },

    // 오른쪽 클릭 시 컨텍스트 메뉴 표시
    showContextMenu(event, book) {
      this.selectedBook = book; // 선택된 책 저장
      this.contextMenuX = event.clientX; // 클릭한 위치의 X 좌표
      this.contextMenuY = event.clientY; // 클릭한 위치의 Y 좌표
      this.contextMenuVisible = true; // 메뉴 표시
    },

    // 책 정보 뷰로 이동
    viewBookInfo() {
      if (this.selectedBook) {
        this.$router.push({ name: 'BookResultView', params: { bookId: this.selectedBook.isbn } }); // 책 ID를 파라미터로 전달
      }
      this.contextMenuVisible = false; // 메뉴 닫기
    },

    // 클릭 외부 시 메뉴 닫기
    closeContextMenu() {
      this.contextMenuVisible = false;
    },

    // 특정 사용자의 책장 불러오기 (책장 목록 조회 API)
    async fetchBookshelves() {
      const user = JSON.parse(localStorage.getItem('user'));
      const userId = user ? user.userId : null; // userId를 가져옵니다.

      try {
        const response = await axios.get(`/api/bookshelf/${userId}`); // userId를 URL에 포함
        this.bookshelves = response.data.result; // result 필드를 사용하여 책장과 책 정보를 포함한 배열로 설정
      } catch (error) {
        console.error('책장 목록 조회 실패:', error);
      }
    },

    // 책장 생성 API
    async addBookshelf() {
      const user = JSON.parse(localStorage.getItem('user'));
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

        if (response.data.isSuccess) { // 응답 상태를 isSuccess로 확인
          alert(`${this.newBookshelfNameForModal} 책장이 추가되었습니다!`); // 알림 메시지
          
          // 책장 목록을 다시 불러옵니다.
          await this.fetchBookshelves(); 

          this.newBookshelfNameForModal = "";
          this.isAddBookshelfModalOpen = false; // 모달 닫rl
        } else {
          alert("책장 추가 실패: " + response.data.message);
        }
      } catch (error) {
        console.error("책장 추가 중 오류 발생:", error);
      }
    },

    // 책장 이름 변경 API
    async renameBookshelf() {
      if (!this.currentBookshelf) return; // 선택된 책장이 없을 경우 처리
      if (!this.newBookshelfName.trim()) {
        alert("새 이름을 입력해 주세요.");
        return; // 새 이름이 비어있을 경우 처리
      }

      try {
        const response = await axios.patch("/api/bookshelf/edit", {
          bookshelfId: this.currentBookshelf, // 수정할 책장 ID
          bookshelfName: this.newBookshelfName, // 새 책장 이름
        });

        if (response.data.isSuccess) {
          alert("책장 이름이 수정되었습니다!");
          await this.fetchBookshelves(); // 변경된 데이터 다시 가져오기
          this.isRenaming = false; // 이름 변경 모드 종료
          this.newBookshelfName = ""; // 입력 필드 초기화
        } else {
          alert("책장 이름 수정 실패: " + response.data.message);
        }
      } catch (error) {
        console.error('책장 이름 수정 실패:', error);
      }
    },

    toggleRenameMode() {
      if (this.currentBookshelf === null) {
        alert("책장을 먼저 선택해주세요.");
        return;
      }

      if (this.isRenaming) {
        this.renameBookshelf();  // 이름 변경 모드에서 저장 진행
      } else {
        // 이름 변경 모드 시작
        const shelf = this.bookshelves.find(shelf => shelf.bookshelfId === this.currentBookshelf);
        if (shelf) {
          this.newBookshelfName = shelf.bookshelfName; // 현재 선택된 책장 이름으로 초기화
        }
      }
      this.isRenaming = !this.isRenaming; // 모드 토글
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

    // 추천받기 모달 열기
    openRecommendationModal() {
      this.isRecommendationModalOpen = true;
    },

    // 추천받기 모달 닫기
    closeRecommendationModal() {
      this.isRecommendationModalOpen = false;
    },

    closeAdditionalRatingModal(){
      this.isAdditionalRatingModalOpen = false;
    },

    setRecommendationType(type) {
      this.recommendationType = type;
    },

    async fetchRecommendations() {
      try {
        const response = await axios.get(`/api/recommend/rating/${this.selectedBookshelf}`);
        
        // 서버 응답에서 result 배열을 recommendations에 저장
        if (response.data.isSuccess) {
          this.recommendations = response.data.result; // API에서 받은 책 목록 저장
          this.isAdditionalRatingModalOpen = true; // 추가 평점 모달 열기
          this.closeRecommendationModal(); // 추천받기 모달 닫기
        } else {
          console.error("추천받기 실패:", response.data.message);
          alert("추천받기 실패: " + response.data.message);
        }
      } catch (error) {
        console.error("추천받기 오류:", error);
      }
    },

    // 검색된 책을 책장에 넣는 작업
    async selectBook(book) {
      this.selectedBook = book; // 선택한 책 정보를 저장
      this.showConfirmModal = true; // 모달 표시
    },

    async selectRating(book) {
      this.selectedRating = book;
      this.showConfirmModal = true;
    },

    confirmAddBook() {
      if (!this.currentBookshelf) {
        alert("책장을 먼저 선택해주세요.");
        return;
      }

      // 선택된 책장의 책 개수 확인
      const currentBookshelfBooks = this.currentBookshelfBooks; // 현재 책장에 있는 책 목록
      if (currentBookshelfBooks.length >= 10) {
        alert("한 책장에는 최대 10권의 책만 추가할 수 있습니다.");
        return; // 10권 이상일 경우 추가하지 않음
      }

      try {
        // API 요청: 선택한 책의 ISBN을 이용해 책장에 추가
        axios.post(`/api/bookshelf/${this.currentBookshelf}/register`, null, {
          params: { isbn13: this.selectedBook.isbn } // this.selectedBook 사용
        }).then(() => {
          this.fetchBookshelves(); 
          alert(`'${this.selectedBook.title}' 책이 책장에 추가되었습니다.`);
          this.showConfirmModal = false; // 모달을 닫습니다.
        });
      } catch (error) {
        console.error('책 추가 실패:', error);
        alert("책 추가 중 오류가 발생했습니다.");
      }
    },

    cancelAddBook() {
      this.showConfirmModal = false; // 모달 닫기
    },

    // 책장 검색 쪽수수
    changePage(page) {
      if (page < 1 || page > this.totalPages) return;
      this.currentPage = page;
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

        if (response.data.books.length === 0) {
          alert("검색 결과가 없습니다.");
        }

        this.searchResults = response.data.books.map(book => ({
          title: book.title,
          author: book.author,
          publisher: book.publisher,
          isbn: book.isbn,
          cover: book.cover,
        }));
        this.currentPage = 1; // 검색 후 페이지를 1로 초기화
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

        if (response.data.books.length === 0) {
          alert("검색 결과가 없습니다.");
        }

        const book = response.data.books[0];
        this.searchResults = [{
          title: book.title,
          author: book.author,
          publisher: book.publisher,
          isbn: book.isbn,
          cover: book.cover,
        }];
        this.currentPage = 1; // 검색 후 페이지를 1로 초기화
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

    // 책장 편집 모드 토글
    toggleEditMode() {
      this.isEditing = !this.isEditing;
    },

    // 책 삭제 메서드
    async removeBook(book) {
      if (confirm(`'${book.title}' 책을 삭제하시겠습니까?`)) {
        try {
          const response = await axios.delete(`/api/bookshelf/delete/book/${this.selectedBookshelf}/${book.bookId}`, {
          });

          if (response.data.isSuccess) {
            alert(`${book.title}이(가) 삭제되었습니다.`);
            // 책장 목록 업데이트
            this.fetchBookshelves();
          } else {
            alert("책 삭제 실패: " + response.data.message);
          }
        } catch (error) {
          console.error("책 삭제 중 오류 발생:", error);
        }
      }
    },
  }
};
</script>

<style scoped>
/* ---------- 필수 스타일 ---------- */
/* ------------------------------- */
.my-books {
  padding: 20px;
}

.bookshelf-header {
  margin-bottom: 20px;
}

.bookshelf-controls {
  display: flex;
  align-items: center;
}

.rename-input {
  padding: 5px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

/* ---------- 책장 리스트박스 ---------- */
/* ---------------------------------- */
.bookshelf-select {
  padding: 5px;
  margin-right: 10px;
  width: 300px;
  text-align: center;
}

/* 책장 폼 위에 있는 버튼들 공통 스타일 */
.rename-button,
.add-bookshelf-button,
.delete-bookshelf-button,
.add-book-button {
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 10px;
  cursor: pointer;
  margin-right: 5px;
  text-align: center;
}

/* 이름 변경 */
.rename-button {
  background-color: #ffa500;
}
.rename-button:hover {
  background-color: #e68900;
}

/* 책장 생성 */
.add-bookshelf-button {
  background-color: #28a745;
}
.add-bookshelf-button:hover {
  background-color: #218838;
}

/* 책장 삭제 */
.delete-bookshelf-button {
  background-color: #dc3545;
}
.delete-bookshelf-button:hover {
  background-color: #c82333;
}

/* 책 등록 */
.add-book-button {
  background-color: #007bff;
  padding: 5px 15px;
  margin-left: 30px;
}
.add-book-button:hover {
  background-color: #0056b3;
}

/* 추천 받기 */
.recommend-button {
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 30px;
  cursor: pointer;
  margin-right: 5px;
  text-align: center;
}
.recommend-button:hover {
  background-color: #0056b3;
}

/* 편집 */
.edit-button {
  background-color: #dc3545;
  color: white;
  border: none;
  border-radius: 4px;
  padding: 5px 15px;
  cursor: pointer;
  text-align: center;
}
.edit-button:hover {
  background-color: #c82333;
}

/* ---------- 네모난 책장 폼 ---------- */
/* --------------------------------- */
.bookshelf {
  padding: 20px;
  border: 7px solid #ddd;
  border-radius: 8px;
  width: 1500px;
  height: auto;
  position: relative; /* 자식 요소의 절대 위치 지정 가능 */
}

/* 책장 내부 책 표지 */
.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 40px;
  position: relative; /* 책 그리드의 위치 설정 */
  z-index: 1; /* 그리드가 이미지 위에 오도록 설정 */
}

/* 책 플레이스홀더 */
.book-placeholder {
  width: 100%; /* 너비를 100%로 설정하여 그리드에 맞게 조정 */
  height: auto;
  background-color: #e9ecef;
  border: 1px solid #ddd;
  border-radius: 8px;
  position: relative;
  display: flex;
  flex-direction: column; /* 세로로 정렬 */
  justify-content: center; /* 세로 중앙 정렬 */
  /* justify-content: center; */
  align-items: center; /* 세로 중앙 정렬 */
  overflow: hidden; /* 자식 요소가 넘칠 경우 숨기기 */
}

/* 책장 책 표지 */
.bookshelfbook-cover {
  margin-top: 10px;
  width: 240px; 
  height: 310px;
  /* height: 100%; */
  display: flex; /* 플렉스 박스 사용 */
  justify-content: center; /* 가로 중앙 정렬 */
  align-items: center; /* 세로 중앙 정렬 */
}
.bookshelfbook-cover img {
  width: 85%;
  height: auto;
  object-fit: cover; /* 비율을 유지하면서 요소를 가득 채우기 */
  border-radius: 8px; /* 모서리 둥글게 처리 (선택 사항) */
}
/* 책 정보 스타일 */
.bookshelf-info {
  text-align: center; /* 중앙 정렬 */
  padding: 10px 0; /* 위아래 여백 추가 */
  margin-top: 10px;
}

.bookshelf-title {
  font-weight: bold; /* 제목을 굵게 */
}

.bookshelf-author, .bookshelf-category {
  font-size: 0.9em; /* 글자 크기를 약간 작게 */
}

/* 책 제거 버튼 */
.remove-book-button {
  position: absolute; /* 절대 위치 지정 */
  top: 5px; /* 위치 조정 */
  right: 5px; /* 위치 조정 */
  background-color: red; /* 배경색 */
  color: white; /* 글자색 */
  border: none; /* 테두리 제거 */
  border-radius: 50%; /* 동그란 버튼 */
  width: 25px; /* 너비 */
  height: 25px; /* 높이 */
  cursor: pointer; /* 커서 변경 */
}

/* 추가 메뉴 */
.context-menu {
  position: absolute;
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 4px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1002; /* 모달보다 위에 표시 */
}

.context-menu button {
  display: block;
  padding: 10px;
  border: none;
  background: none;
  cursor: pointer;
}

.context-menu button:hover {
  background-color: #f0f0f0; /* 마우스 오버 시 배경색 변경 */
}

/* ---------- 책 등록 추가 모달 ---------- */
/* ------------------------------------ */
.add-bookshelf-modal {
  position: fixed; /* 화면 중앙 고정 */
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

.create-bookshelf-button {
  background-color: #28a745; /* 버튼 색상 */
  color: white; /* 글자 색상 */
  border: none; /* 기본 테두리 제거 */
  padding: 8px; /* 내부 여백 */
  border-radius: 4px; /* 모서리 둥글게 */
  cursor: pointer; /* 마우스 커서 변경 */
  margin-top: 10px;
  margin-bottom: 5px; /* 아래쪽 여백 */
}

.close-modal-button {
  background-color: #dc3545;
  color: white;
  border: none;
  padding: 8px;
  border-radius: 4px;
  cursor: pointer;
}

/* 추천 받기 모달 */
.recommendation-modal {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1001; /* 다른 요소 위에 표시 */
}
.recommendation-modal-content {
  background-color: rgb(168, 211, 255);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 10px 10px rgba(0, 0, 0, 0.2);
}
.recommendation-options {
  display: flex;
  justify-content: center; /* 가운데 정렬 */
  gap: 10px; /* 버튼 간의 간격 조정 */
  margin-bottom: 20px; /* 아래쪽 여백 */
}
.recommend-books-button {
  background-color: #28a745; /* 추천받기 버튼 색상 */
  color: white;
  border: none;
  border-radius: 4px;
  padding: 10px;
  cursor: pointer;
}

/* ---------- 추가 평점 모달 ---------- */
.additional-rating-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1002; /* 사이드바보다 위에 표시 */
}

.additional-rating-modal-content {
  background-color: rgb(168, 211, 255);
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 10px 10px rgba(0, 0, 0, 0.2);
}

.additional-rating-header {
  font-size: 18px; /* 헤더 크기 */
  font-weight: bold;
  margin-bottom: 15px; /* 아래쪽 여백 */
}

.rating-list {
  display: flex;
  flex-direction: column; /* 세로 방향 정렬 */
  gap: 10px; /* 항목 간의 간격 */
}

.rating-item {
  display: flex; /* 가로 방향 정렬 */
  align-items: center; /* 수직 정렬 */
  gap: 10px; /* 책 표지와 정보 간의 간격 */
  border: 1px solid #ddd; /* 테두리 */
  padding: 10px; /* 내부 여백 */
  border-radius: 5px; /* 모서리 둥글게 */
  background-color: #f9f9f9; /* 배경색 */
}

.rating-cover {
  width: 50px; /* 책 표지 너비 */
  height: auto; /* 자동 높이 */
}

.rating-info {
  flex-grow: 1; /* 남은 공간을 차지 */
  margin-left: 40px; /* 표지와의 간격 조정 */
}

.rating-title {
  font-weight: bold; /* 제목 두껍게 */
  font-size: 17px; /* 제목 크기 */
  margin-bottom: 7px;
}

.rating-author {
  font-size: 12px; /* 저자 크기 */
  color: gray; /* 저자 색상 */
  margin-bottom: 5px;

}

.rating-category {
  font-size: 12px;
  color: #28a745;
  margin-bottom: 5px;

}

.rating-score {
  font-size: 15px; /* 평점 크기 */
  color: #007bff; /* 평점 색상 */
}

.select-rating-button {
  padding: 5px 10px;
  background-color: #007bff; /* 선택 버튼 색상 */
  color: white; /* 글자 색상 */
  border: none; /* 테두리 제거 */
  border-radius: 4px; /* 모서리 둥글게 */
  cursor: pointer; /* 커서 변경 */
}

.select-rating-button:hover {
  background-color: #0056b3; /* 호버 시 색상 변경 */
}

/* 모달 닫기 버튼 */
.close-rating-modal-button {
  margin-top: 15px; /* 위쪽 마진 */
  padding: 8px;
  background-color: #dc3545; /* 닫기 버튼 색상 */
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}



/* ---------- 사이드바 ---------- */
/* ---------------------------- */
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
.sidebar .registration-options button.active {
  background-color: #4caf50;
  color: white;
  border-color: #45a049;
}
.sidebar .registration-options button:not(.active):not(:disabled) {
  background-color: #f0f0f0;
  border-color: #ccc;
  color: #888; /* 연한 회색 */
}
.sidebar .registration-options button:disabled {
  background-color: #ddd;
  cursor: not-allowed;
  border-color: #bbb;
  color: #bbb; /* 연한 회색으로 글자 색상 */
}

/* input 및 버튼 스타일일 */
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

/* 사이드바 검색된 책들 */
.search-results ul {
  display: grid; /* 그리드 레이아웃 사용 */
  grid-template-columns: repeat(2, 1fr); /* 2개씩 배치 */
  gap: 10px; /* 항목 간의 간격 */
  padding: 0; /* 기본 패딩 제거 */
  list-style-type: none; /* 리스트 스타일 제거 */
}

.search-book-item {
  display: flex; /* 가로 정렬 */
  align-items: center; /* 수직 정렬 */
  gap: 10px; /* 표지와 정보 사이 여백 */
  border: 1px solid #ddd; /* 테두리 */
  padding: 10px; /* 내부 여백 */
  background: white; /* 배경색 */
  border-radius: 5px; /* 모서리 둥글게 */
}

/* 왼쪽: 책 표지 */
.sidebook-cover {
  flex-shrink: 0; /* 크기 고정 */
  width: 140px; /* 표지 너비 */
  height: 160px; /* 표지 높이 */
  display: flex;
  align-items: center;
  justify-content: center;
}

.sidebook-cover img {
  max-width: 100%;
  max-height: 100%;
  object-fit: cover;
  border-radius: 3px;
}

/* 오른쪽: 제목, 저자, 버튼 */
.sidebook-info {
  display: flex;
  flex-direction: column; /* 세로 정렬 */
  flex-grow: 1; /* 남은 공간 차지 */
  justify-content: center;
  gap: 4px; /* 요소 간 간격 */
}

.sidebook-title {
  font-weight: bold;
  font-size: 13px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 140px; /* 제목 최대 너비 */
}

.sidebook-author {
  font-size: 12px;
  color: gray;
}

/* 선택 버튼 */
.sideselect-book-button {
  align-self: flex-start; /* 왼쪽 정렬 */
  padding: 4px 8px;
  font-size: 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.sideselect-book-button:hover {
  background-color: #0056b3;
}




/* 사이드바 결과 책 표지 */
.book-cover {
  display: flex;
  align-items: stretch;
  width: 50px;
  height: auto;
  flex-shrink: 0; /* 표지 크기 고정 */
}
.book-info {
  display: flex;
  flex-direction: column;
  justify-content: space-between; /* 제목과 버튼 간의 공간을 조절 */
  /* justify-content: center; */
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
  margin-top: auto; /* 버튼을 아래쪽으로 이동 */
  /* margin-top: 5px; */
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

/* 페이지네이션 */
.pagination {
  display: flex;
  justify-content: center;
  margin-top: 10px; /* 페이지네이션과 위쪽 요소 간의 간격을 조절 */
}
.pagination button {
  margin: 0 5px; /* 버튼 간의 간격을 조절 */
}


.confirm-modal {
  position: fixed;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  background-color: white;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  z-index: 1001; /* 사이드바보다 위에 표시 */
}
.confirm-modal-content {
  display: flex;
  flex-direction: column;
  align-items: center;
}
.confirm-modal button {
  margin-top: 10px;
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}
.confirm-modal-button-container {
  display: flex; /* 수평 정렬을 위해 flex 사용 */
  justify-content: space-between; /* 버튼 사이에 공간을 균등하게 배치 */
  width: 100%; /* 컨테이너의 너비를 100%로 설정 */
  margin-top: 20px; /* 버튼 위에 간격 추가 */
}
.confirm-modal button:hover {
  background-color: #0056b3;
}
</style>