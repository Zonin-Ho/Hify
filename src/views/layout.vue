<template>
  <div class="text-blue-500">
    <div class="flex items-center h-[50px]">
      <div class="flex">Hify</div>
      <a-menu v-model:selectedKeys="current" mode="horizontal" :items="items" />
    </div>
    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { h, ref } from "vue";
import { RouterLink } from "vue-router";
import type { MenuProps } from "ant-design-vue";
import {
  MailOutlined,
  AppstoreOutlined,
  SettingOutlined,
} from "@ant-design/icons-vue";
const current = ref<string[]>(["mail"]);
const items = ref<MenuProps["items"]>([
  {
    key: "chat",
    icon: () => h(MailOutlined),
    label: h(RouterLink, { to: "/chat" }, () => "会话"),
    title: "会话",
  },
  {
    key: "datasets",
    icon: () => h(AppstoreOutlined),
    label: h(RouterLink, { to: "/datasets" }, () => "知识库"),
    title: "知识库",
  },
  {
    key: "setting",
    icon: () => h(SettingOutlined),
    label: h(RouterLink, { to: "/setting" }, () => "设置"),
    title: "设置",
  },
]);
</script>
