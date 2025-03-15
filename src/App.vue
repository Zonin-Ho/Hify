<script setup lang="ts">
import zhCN from "ant-design-vue/es/locale/zh_CN";
</script>

<template>
  <a-config-provider :local="zhCN">
    <keep-alive>
      <router-view></router-view>
    </keep-alive>
  </a-config-provider>
</template>
