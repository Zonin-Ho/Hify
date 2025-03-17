<template>
  <div class="flex h-[calc(100vh-50px)] bg-gray-100">
    <!-- 左侧会话列表 -->
    <div class="w-[300px] border-r border-gray-200 bg-white overflow-y-auto">
      <div
        class="flex my-4 items-center justify-center h-[36px] bg-slate-200 rounded mx-4 cursor-pointer"
      >
        新建会话
      </div>
      <a-list>
        <a-list-item
          class="cursor-pointer hover:bg-gray-50"
          v-for="(conversation, index) in conversations"
          :key="index"
          @click="selectConversation(index)"
        >
          <a-list-item-meta>
            <template #description>{{
              conversation.messages[0]?.content || "无消息"
            }}</template>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
    </div>

    <!-- 中间聊天区域 -->
    <div class="flex flex-col flex-1 p-5 space-y-4">
      <div class="flex-1 p-6 overflow-y-auto">
        <div
          v-for="(message, idx) in currentConversation?.messages || []"
          :key="idx"
          :class="[
            message.sender === 'user' && 'justify-end',
            message.sender === 'bot' && 'justify-start',
            'flex w-full mb-3',
          ]"
        >
          <span
            :class="[
              message.sender === 'user' && 'bg-blue-500 text-white',
              message.sender === 'bot' && ' bg-white text-blue-500',
              ' p-3 rounded max-w-[70%]',
            ]"
            >{{ message.content }}</span
          >
        </div>
      </div>
      <div class="flex flex-col p-4 space-y-2 bg-white rounded-lg">
        <a-textarea
          v-model:value="newMessage"
          placeholder="请输入消息..."
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allow-clear
        />
        <a-button type="primary" class="self-end" @click="sendMessage"
          >发送</a-button
        >
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";

// 定义会话数据结构
const conversations = ref([
  {
    messages: [
      {
        sender: "user",
        content: "你好",
      },
      {
        sender: "bot",
        content: "你好，有什么问题吗？",
      },
    ],
  },
  {
    messages: [
      {
        sender: "user",
        content: "你好",
      },
      {
        sender: "bot",
        content: "你好，有什么问题吗？",
      },
    ],
  },
]);

// 当前选中的会话索引
const selectedConversationIndex = ref(0);

// 获取当前会话
const currentConversation = computed(
  () => conversations.value[selectedConversationIndex.value]
);

// 新消息输入框绑定值
const newMessage = ref("");

// 切换会话
const selectConversation = (index: number) => {
  selectedConversationIndex.value = index;
  console.log(index);
};

// 发送消息
const sendMessage = () => {
  console.log(newMessage.value);
  if (newMessage.value.trim()) {
    console.log(newMessage.value);

    currentConversation.value.messages.push({
      sender: "user",
      content: newMessage.value,
    });
    newMessage.value = ""; // 清空输入框
  }
};
</script>
