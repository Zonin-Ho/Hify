<template>
  <div class="text-blue-500">
    <div class="flex items-center justify-center h-[50px]">
      <!-- <div class="flex pl-8">Hify</div> -->
      <a-menu
        style="border-bottom: none"
        v-model:selectedKeys="current"
        mode="horizontal"
        :items="items"
      />
    </div>

    <router-view></router-view>
  </div>
</template>

<script lang="ts" setup>
import { h, ref } from "vue";
import { RouterLink, useRoute } from "vue-router";
import type { MenuProps } from "ant-design-vue";
import { MailOutlined, AppstoreOutlined } from "@ant-design/icons-vue";
// import { useUserStore } from "@/stores/userStore";

// const useStore = useUserStore();
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
  // {
  //   key: "setting",
  //   icon: () => h(SettingOutlined),
  //   label: h(RouterLink, { to: "/setting" }, () => "设置"),
  //   title: "设置",
  // },
]);
</script>
