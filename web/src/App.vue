<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";
</script>

<template>
  <a-config-provider :local="zhCN">
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>
  </a-config-provider>
</template>
