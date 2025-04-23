<template>
  <a-modal ref="modalRef" v-model:open="open" width="600px">
    <a-layout>
      <a-layout-header class="!bg-white">
        <a-tabs class="flex items-center" v-model:activeKey="activeKey">
          <a-tab-pane key="account" tab="我的账号" />
          <a-tab-pane key="log" tab="登录日志" /></a-tabs
      ></a-layout-header>
      <a-layout-content
        ><div v-if="activeKey === 'account'">
          <ul class="flex flex-col gap-4 p-2">
            <li class="flex justify-between items-center">
              <div class="flex items-center">
                <span class="w-20">头像：</span>
                <a-avatar
                  shape="square"
                  size="large"
                  src="http://localhost:9000/hify/b_b75df21309c6906e28cbc27f6d781135.jpg"
                  alt="用户头像"
                />
              </div>
              <!-- <div><a-button type="link">修改</a-button></div> -->
            </li>
            <li class="flex justify-between items-center">
              <div class="flex items-center">
                <span class="w-20">用户名：</span>
                <a-input disabled v-model:value="username"></a-input>
              </div>
              <div><a-button type="link">修改</a-button></div>
            </li>
            <li class="flex justify-between items-center">
              <div class="flex items-center">
                <span class="w-20">邮箱：</span>
                <a-input disabled v-model:value="email"></a-input>
              </div>
              <!-- <div><a-button type="link">修改</a-button></div> -->
            </li>
          </ul>
        </div>
        <div v-else-if="activeKey === 'log'">
          <a-table :columns="logColumns" :dataSource="logData" />
        </div>
        <div v-else>内容</div></a-layout-content
      >
    </a-layout>
    <template #title>
      <div ref="modalTitleRef" style="width: 100%">设置</div>
    </template>
    <template #footer></template>
  </a-modal>
</template>

<script setup lang="ts">
import { useUserStore } from "@/stores/userStore";
import { ref } from "vue";
const useStore = useUserStore();
const username = ref(useStore.username);
const email = ref(useStore.email);
// const props = defineProps({
//   modelValue: {
//     type: Boolean,
//     default: false,
//   },
// });
const open = defineModel("open");
const activeKey = ref("account");

const logData = ref([
  { date: "2023-10-01", action: "登录", ip: "192.168.1.1" },
  { date: "2023-10-02", action: "登出", ip: "192.168.1.2" },
]);

const logColumns = ref([
  { title: "日期", dataIndex: "date", key: "date" },
  { title: "IP地址", dataIndex: "ip", key: "ip" },
  { title: "地区", dataIndex: "region", key: "region" },
  { title: "登录设备", dataIndex: "equipment", key: "equipment" },
]);
</script>
