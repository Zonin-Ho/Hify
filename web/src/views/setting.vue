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
          <ul>
            <li>
              头像：<a-avatar
                shape="square"
                src="https://cdn.jsdelivr.net/gh/ant-design/ant-design-icons@4.2.2/packages/icons-svg/svg/outline/user.svg"
                alt="用户头像"
              />
            </li>
            <li>用户名：hzx0901</li>
            <li>邮箱：1225853481@qq.com</li>
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
import { ref } from "vue";

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
