<template>
  <div id="app">
    <LayoutView v-if="$route.meta.layout !== false">
      <router-view />
    </LayoutView>
    <router-view v-else />
  </div>
</template>

<script>
import { ref, provide, onMounted } from 'vue';
import LayoutView from '@/layouts/LayoutView.vue';

export default {
  name: 'App',
  components: { LayoutView },
  setup() {
    const user = ref(null);

    // ✅ 로컬스토리지에서 로그인 정보 불러오기
    onMounted(() => {
      const storedUser = localStorage.getItem('user');
      if (storedUser) {
        user.value = JSON.parse(storedUser);
      }

      // ✅ 다른 탭에서 로그인/로그아웃 감지
      window.addEventListener('storage', () => {
        const updatedUser = localStorage.getItem('user');
        user.value = updatedUser ? JSON.parse(updatedUser) : null;
      });
    });

    // ✅ 로그아웃 함수
    const logout = () => {
      localStorage.removeItem('user');
      user.value = null;
    };

    // ✅ 전역 상태 공유
    provide('user', user);
    provide('logout', logout);

    return { user, logout };
  },
};
</script>