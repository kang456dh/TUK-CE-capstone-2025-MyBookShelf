<template>
  <div id="app">
    <LayoutView v-if="$route.meta.layout !== false">
      <router-view />
    </LayoutView>
    <router-view v-else />
  </div>
</template>

<script>
import LayoutView from '@/layouts/LayoutView.vue';

export default {
  name: 'App',
  components: { LayoutView },
};
</script>