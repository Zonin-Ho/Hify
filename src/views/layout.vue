<template>
  <div class="text-blue-500">
    <div class="flex items-center justify-between h-[50px]">
      <div class="flex pl-8">Hify</div>
      <a-menu
        style="border-bottom: none"
        v-model:selectedKeys="current"
        mode="horizontal"
        :items="items"
      />
      <div class="flex items-center user-info">
        <img
          src=""
          alt="User Avatar"
          class="w-8 h-8 mr-2 rounded-full user-avatar"
        />
        <span class="pr-8 text-lg text-gray-800 user-name">用户</span>
      </div>
    </div>

    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { h, ref } from "vue";
import { RouterLink, useRoute } from "vue-router";
import type { MenuProps } from "ant-design-vue";
import {
  MailOutlined,
  AppstoreOutlined,
  SettingOutlined,
} from "@ant-design/icons-vue";
const route = useRoute();
const current = ref<string[]>([route.name as string]);
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
