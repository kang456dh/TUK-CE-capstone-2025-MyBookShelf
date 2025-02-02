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
        <select
          v-else
          v-model="currentBookshelf"
          @change="selectBookshelf"
          class="bookshelf-select"
        >
          <option
            v-for="(shelf, index) in bookshelves"
            :key="index"
            :value="shelf.name"
          >
            {{ shelf.name }}
          </option>
        </select>
        <button @click="toggleRenameMode" class="rename-button">
          {{ isRenaming ? "저장" : "이름 변경" }}
        </button>
        <button @click="addBookshelf" class="add-bookshelf-button">+</button>
        <button @click="deleteBookshelf" class="delete-bookshelf-button">🗑</button>
        <button @click="openSidebar" class="add-book-button">책 등록</button> <!-- 책 등록 버튼 -->
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
            <img :src="book.cover" alt="책 표지" />
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
      </div>
    </div>
  </div>
</template>


<script>
export default {
  name: "MyBooksView",
  data() {
    return {
      bookshelves: [
        { name: "책장 1", books: Array(20).fill({ cover: null }) },
      ],
      currentBookshelf: "책장 1",
      isRenaming: false,
      newBookshelfName: "",
      isSidebarOpen: false,
      registerType: "manual",
      manualTitle: "",
      isbn: "",
      isPhotoRegistrationOpen: false,  // 사진 등록 버튼 활성화
    };
  },
  computed: {
    currentBookshelfBooks() {
      const shelf = this.bookshelves.find(
        (shelf) => shelf.name === this.currentBookshelf
      );
      return shelf ? shelf.books : [];
    },
  },
  methods: {
    toggleRenameMode() {
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
    selectBookshelf() {
      // 책장 변경 로직
    },
    addBookshelf() {
      const newShelfName = `책장 ${this.bookshelves.length + 1}`;
      this.bookshelves.push({ name: newShelfName, books: Array(20).fill({ cover: null }) });
      this.currentBookshelf = newShelfName;
    },
    deleteBookshelf() {
      if (this.bookshelves.length === 1) {
        alert("최소 1개의 책장이 존재해야 합니다.");
        return; // 책장이 하나일 경우 삭제하지 않음
      }

      this.bookshelves = this.bookshelves.filter(
        (shelf) => shelf.name !== this.currentBookshelf
      );
      if (this.bookshelves.length > 0) {
        this.currentBookshelf = this.bookshelves[0].name;
      } else {
        this.addBookshelf();
      }
    },
    openSidebar() {
      this.isSidebarOpen = true;
    },
    closeSidebar() {
      this.isSidebarOpen = false;
      this.manualTitle = "";
      this.isbn = "";
    },
    setRegisterType(type) {
      this.registerType = type;
    },
    searchManual() {
      console.log("검색된 책 제목:", this.manualTitle);
    },
    searchISBN() {
      console.log("검색된 ISBN:", this.isbn);
    },
    
    // 사진 등록 버튼 클릭 시
    openPhotoRegistration() {
      this.isPhotoRegistrationOpen = !this.isPhotoRegistrationOpen;
    },

    // 파일 첨부 버튼 클릭 시
    openFileInput() {
      const fileInput = document.createElement('input');
      fileInput.type = 'file';
      fileInput.accept = 'image/*';  // 이미지 파일만 선택
      fileInput.click();
      
      fileInput.addEventListener('change', () => {
        const file = fileInput.files[0];
        if (file) {
          console.log("첨부된 파일:", file);
          // 파일 처리 로직 추가
        }
      });
    },

    // 사진 촬영 버튼 클릭 시
    openCamera() {
      if (navigator.mediaDevices && navigator.mediaDevices.getUserMedia) {
        // 모바일 환경에서 카메라 열기
        navigator.mediaDevices.getUserMedia({ video: true })
          .then(() => {
            console.log("카메라가 열렸습니다.");
            // 비디오 스트림을 화면에 표시하거나 처리할 로직을 여기에 추가 가능
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

.book-cover img {
  width: 80%; /* 책 표지 너비를 80%로 설정 */
  height: auto; /* 자동으로 비율에 맞게 높이를 설정 */
  object-fit: cover;
  border-radius: 8px;
}


/* 사이드바 스타일 */
.sidebar {
  position: fixed;
  top: 0;
  right: 0;
  width: 400px;
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
</style>
