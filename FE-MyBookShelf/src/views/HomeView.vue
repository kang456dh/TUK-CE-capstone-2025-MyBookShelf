<template>
  <div class="random-books">
    <!-- 랜덤 책 검색 버튼 -->
    <button @click="fetchRandomBooks" class="random-button">랜덤 5권</button>
    
    <!-- 그리드 -->
    <div class="book-grid">
      <div
        v-for="(book, index) in randomBooks"
        :key="index"
        class="book-cover-item"
      >
        <img :src="book.cover" alt="책 표지" />
      </div>
    </div>

    <!-- 광고 슬라이드 -->
    <div 
      class="ad-slide-container"
      @mouseenter="pauseSlide"
      @mouseleave="resumeSlide"
    >
      <div
        class="ad-slide"
        :style="{
          transform: 'translateX(' + (-100 * slideIndex) + '%)'
        }"
      >
        <!-- 광고 이미지 -->
        <div
          v-for="(ad, index) in ads"
          :key="'ad-' + index"
          class="ad-slide-item"
          @click="handleAdClick(index)"
        >
          <img :src="ad" alt="광고 이미지" class="ad-img" />
        </div>
      </div>

      <!-- 슬라이드 인디케이터 (동그라미 버튼들) -->
      <div class="slide-indicators">
        <div
          v-for="(ad, index) in ads"
          :key="'indicator-' + index"
          class="indicator"
          :class="{ active: index === slideIndex }"
          @click="goToSlide(index)"
        ></div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import ad1 from '@/assets/ad1.png';
import ad2 from '@/assets/ad2.png';
import ad3 from '@/assets/ad3.png';
import ad4 from '@/assets/ad4.png';
import ad5 from '@/assets/ad5.png';

export default {
  name: "RandomBooksView",
  data() {
    return {
      randomBooks: [],
      slideIndex: 0,
      ads: [ad1, ad2, ad3, ad4, ad5],
      slideInterval: null,
    };
  },
  created() {
    this.fetchRandomBooks();
    this.startSlide();  // 슬라이드 시작
  },
  beforeUnmount() {
    if (this.slideInterval) {
      clearInterval(this.slideInterval);
    }
  },
  methods: {
    fetchRandomBooks() {
      axios.get('http://localhost:8081/api/books/random?count=5')
        .then(response => {
          this.randomBooks = response.data.books;
        })
        .catch(error => {
          console.error("랜덤 책 검색 오류:", error);
        });
    },
    nextSlide() {
      this.slideIndex = (this.slideIndex + 1) % this.ads.length;
    },
    startSlide() {
      this.slideInterval = setInterval(this.nextSlide, 5000);
    },
    pauseSlide() {
      if (this.slideInterval) {
        clearInterval(this.slideInterval);
      }
    },
    resumeSlide() {
      this.startSlide();
    },
    goToSlide(index) {
      // 슬라이드 인디케이터 클릭 시 해당 슬라이드로 이동
      this.slideIndex = index;
    },
    handleAdClick(index) {
      if (index === 0) {
        // ad1 클릭 시 'MyBooks' 경로로 이동
        this.$router.push({ name: 'MyBooks' });
      } else if (index === 1) {
        // ad2 클릭 시 'Category' 경로로 이동
        this.$router.push({ name: 'Category' });
      } else if (index === 2) {
        // ad3 클릭 시 'Community' 경로로 이동
        this.$router.push({ name: 'Community' });
      } else if (index === 3) {
        // ad4 클릭 시 'Guidelines' 경로로 이동
        this.$router.push({ name: 'Guidelines' });
      }
    },
  },
};
</script>

<style scoped>
/* 랜덤 책 5개 버튼 스타일 */
.random-button {
  padding: 10px 20px;
  background-color: #ff2b2b;  /* 연한 주황색 */
  border: none;
  color: white;
  cursor: pointer;
  margin-bottom: 15px;
  font-size: 16px;
}

.random-button:hover {
  background-color: #f0a3a3;  /* 버튼 색상 hover 시 */
}

/* 그리드 스타일 */
.book-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);  /* 5개의 컬럼으로 설정 */
  gap: 20px;
  background-color: #fef2e3;  /* 연한 주황색 배경 */
  padding: 20px;
  border-radius: 10px;
}

.book-cover-item {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
}

.book-cover-item img {
  max-width: 100%;
  height: auto;
  border-radius: 6px;
}

/* 광고 이미지에 마우스를 올렸을 때 효과 */
.ad-img {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer; /* 마우스 커서를 손 모양으로 바꿔줌 */
}

.ad-img:hover {
  transform: scale(1.05); /* 크기 확대 */
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3); /* 그림자 효과 */
}

/* 광고 슬라이드 컨테이너 */
.ad-slide-container {
  position: relative;
  width: 100%;
  overflow: hidden;
  margin-top: 30px;
}

/* 광고 슬라이드 스타일 */
.ad-slide {
  display: flex;
  transition: transform 0.5s ease-in-out;
}

.ad-slide-item {
  width: 100%;
  flex-shrink: 0;
  display: flex;
  justify-content: center;
  align-items: center;
}

.ad-slide-item img {
  max-width: 80%;
  height: auto;
  border-radius: 10px;
}

/* 슬라이드 인디케이터 (동그라미 버튼들) */
.slide-indicators {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  gap: 10px;
}

.indicator {
  width: 10px;
  height: 10px;
  background-color: rgba(255, 255, 255, 0.5);
  border-radius: 50%;
  cursor: pointer;
}

.indicator.active {
  background-color: #ff2b2b;  /* 선택된 인디케이터 색상 */
}
</style>