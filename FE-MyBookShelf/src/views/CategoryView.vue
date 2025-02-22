<template>
  <div class="category-view">
    <!-- 카테고리 목록 -->
    <div class="category-list">
      <h3>카테고리</h3>
      <ul>
        <li
          v-for="(category, index) in categories"
          :key="index"
          @click="selectCategory(category)"
          :class="{ active: selectedCategory === category }"
        >
          {{ category.name }}
        </li>
      </ul>
    </div>

    <!-- 선택된 카테고리의 도서 목록 -->
    <div class="book-list">
      <h3>{{ selectedCategory ? selectedCategory.name : '카테고리 선택' }} 도서 목록</h3>
      <div class="book-grid">
        <div
          v-for="(book, index) in books"
          :key="index"
          class="book-item"
        >
          <img :src="book.cover" alt="책 표지" />
          <p>{{ book.title }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';

export default {
  name: 'CategoryView',
  data() {
    return {
      categories: [],  // 카테고리 목록
      selectedCategory: null,  // 선택된 카테고리
      books: [],  // 선택된 카테고리의 도서 목록
    };
  },
  created() {
    this.fetchCategories();  // 카테고리 목록을 불러오기
  },
  methods: {
    // 카테고리 목록을 가져오는 API 호출
    fetchCategories() {
      axios.get('http://localhost:8081/api/categories')
        .then(response => {
          this.categories = response.data.categories;
          if (this.categories.length) {
            this.selectCategory(this.categories[0]);  // 첫 번o 카테고리 선택
            this.fetchBooksByCategory(this.selectedCategory.id); // 기본 카테고리 도서 목록 가져오기
          }
        })
        .catch(error => {
          console.error('카테고리 목록 불러오기 오류:', error);
        });
    },

    // 카테고리 선택 시 호출되는 함수
    selectCategory(category) {
      this.selectedCategory = category;
      this.fetchBooksByCategory(category.id);
    },

    // 선택된 카테고리에 해당하는 도서 목록을 가져오는 API 호출
    fetchBooksByCategory(categoryId) {
      axios.get(`http://localhost:8081/api/books?category=${categoryId}`)
        .then(response => {
          this.books = response.data.books;
        })
        .catch(error => {
          console.error('도서 목록 불러오기 오류:', error);
        });
    },
  },
};
</script>

<style scoped>
.category-view {
  display: flex;
  gap: 30px;
}

.category-list {
  width: 200px;
}

.category-list ul {
  list-style: none;
  padding: 0;
}

.category-list li {
  cursor: pointer;
  padding: 10px;
  background-color: #f4f4f4;
  margin-bottom: 10px;
  border-radius: 5px;
}

.category-list li.active {
  background-color: #ff2b2b;
  color: white;
}

.book-list {
  flex: 1;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.book-item {
  text-align: center;
}

.book-item img {
  max-width: 100%;
  border-radius: 8px;
}

.book-item p {
  margin-top: 10px;
  font-size: 16px;
}
</style>