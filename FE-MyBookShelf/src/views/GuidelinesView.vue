<template>
  <div class="guidelines-view">
    <h1>이용 안내</h1>

    <section class="guideline-section">
      <h2>1. 나만의 책장 만들기</h2>
      <p>
        '나만의 책장'은 나만의 도서 목록을 관리할 수 있는 공간입니다. 책을 추가하고,
        원하는 책장에 책을 분류하여 보관할 수 있습니다. 이 책장에서는 내가 읽고 싶은
        책을 모아두고, 책장을 이름대로 나누어 관리할 수 있어요.
      </p>
      <ul>
        <li>책장을 추가하려면 화면 상단의 '+ 책장 추가' 버튼을 클릭하세요.</li>
        <li>책을 추가하려면 책 제목을 입력하고, 원하는 책을 선택해 책장에 추가하세요.</li>
        <li>책장의 이름을 변경하거나 삭제하려면 해당 책장을 선택한 후 옵션을 클릭하세요.</li>
      </ul>
    </section>

    <section class="guideline-section">
      <h2>2. 도서 추천 서비스</h2>
      <p>
        이 사이트는 당신의 독서 취향을 바탕으로 책을 추천해주는 서비스를 제공합니다.
        다양한 장르와 주제에 맞춰 추천된 책을 살펴보세요.
      </p>
      <ul>
        <li>도서 목록에서 '추천 도서' 탭을 클릭하면, 취향에 맞는 책을 추천받을 수 있습니다.</li>
        <li>책을 선택하면, 해당 책의 상세 정보와 책장에 추가하는 옵션이 나타납니다.</li>
        <li>내 취향에 맞는 책을 추가하고, 나만의 책장에서 책을 자유롭게 관리하세요.</li>
      </ul>
    </section>

    <section class="guideline-section">
      <h2>3. 커뮤니티 참여하기</h2>
      <p>
        사이트의 커뮤니티 기능을 통해 다른 사용자들과 책에 대한 의견을 나누고,
        추천 도서를 공유할 수 있습니다. 나만의 책장에 추가한 책에 대한 리뷰를 작성하거나,
        다른 사람들의 리뷰를 읽어보세요.
      </p>
      <ul>
        <li>게시물 검색 기능을 통해 다양한 독서 주제에 대한 이야기를 찾아보세요.</li>
        <li>글쓰기를 통해 독서 경험을 공유하고, 다른 사용자들과 소통할 수 있습니다.</li>
        <li>특정 도서에 대한 리뷰를 작성하여, 다른 사람들에게 책에 대한 의견을 전하세요.</li>
      </ul>
    </section>

    <section class="guideline-section">
      <h2>4. 추가 기능 및 FAQ</h2>
      <p>
        더 많은 기능을 탐색하고 싶다면, FAQ 섹션에서 자주 묻는 질문들을 확인하세요.
        또한, 책장과 관련된 다양한 팁과 도움말을 통해 더욱 풍성한 독서 경험을 즐길 수 있습니다.
      </p>
    </section>
  </div>
</template>

  
<script>
export default {
  name: "GuidelinesView",
  data() {
    return {};
  },
  methods: {},
};
</script>

<style scoped>
.guidelines-view {
  padding: 20px;
}

.guideline-section {
  margin-bottom: 30px;
}

.guideline-section h2 {
  font-size: 24px;
  margin-bottom: 10px;
}

.guideline-section p {
  font-size: 16px;
  margin-bottom: 10px;
}

.guideline-section ul {
  margin-left: 20px;
}

.guideline-section ul li {
  font-size: 16px;
  margin-bottom: 5px;
}

.guideline-section ul li::before {
  content: "• ";
  color: #4caf50;
}
</style>