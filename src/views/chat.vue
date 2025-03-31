<template>
  <div class="flex h-[calc(100vh-50px)] bg-gray-100">
    <!-- 左侧会话列表 -->
    <div class="w-[300px] border-r border-gray-200 bg-white overflow-y-auto">
      <div
        class="flex my-4 items-center justify-center h-[36px] bg-slate-200 rounded mx-4 cursor-pointer"
        @click="isNewSession = true"
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
      <div class="flex-1 p-6 overflow-y-auto" ref="chatBox">
        <div v-if="isNewSession">欢迎使用Hify智能助手</div>
        <template v-else>
          <div
            v-for="(message, idx) in currentConversation?.messages || []"
            :key="idx"
            :class="[
              message.sender === 'user' && 'justify-end',
              message.sender === 'bot' && 'justify-start ',
              'flex w-full mb-3',
            ]"
          >
            <!-- 消息内容 -->
            <div
              v-if="message.displayContent"
              :class="[
                message.sender === 'user' && 'bg-blue-500 text-white',
                message.sender === 'bot' &&
                  ' bg-white text-blue-500 markdown-body',
                ' p-3 rounded max-w-[70%]',
              ]"
              v-html="message.displayContent"
            ></div>

            <!-- 加载中 -->
            <div v-else class="p-3 rounded max-w-[70%] bg-white text-blue-500">
              <a-spin />
            </div>
          </div>
        </template>
      </div>
      <div class="flex flex-col p-4 space-y-2 bg-white rounded-lg">
        <a-textarea
          v-model:value="newMessage"
          placeholder="请输入消息..."
          :auto-size="{ minRows: 2, maxRows: 5 }"
          allow-clear
        />
        <div class="flex justify-between">
          <div class="flex justify-between">
            <a-select
              style="width: 180px; margin-right: 12px"
              v-model:value="selectedModel"
              :options="modelOption"
            >
            </a-select>
            <a-button
              @click="isDeepThink = !isDeepThink"
              :type="isDeepThink ? 'primary' : 'default'"
              >深度思考</a-button
            >
          </div>

          <a-button type="primary" class="self-end" @click="sendMessage"
            >发送</a-button
          >
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed } from "vue";
import { marked } from "marked";
import { markedHighlight } from "marked-highlight";
import hljs from "highlight.js";
import { message, type SelectProps } from "ant-design-vue";
import { useUserStore } from "@/stores/userStore";
import "github-markdown-css/github-markdown.css";

const useStore = useUserStore();
const isNewSession = ref(false);
// 在setup外全局配置marked
marked.use(
  {
    gfm: true,
    breaks: true,
  },
  markedHighlight({
    langPrefix: "hljs language-",
    highlight(code, lang) {
      const language = hljs.getLanguage(lang) ? lang : "shell";
      return hljs.highlight(code, { language }).value;
    },
  })
);

const isDeepThink = ref(true);
const selectedModel = ref("deepseek-r1:1.5b");
const modelOption = ref<SelectProps["options"]>([
  {
    value: "deepseek-r1:1.5b",
    label: "deepseek-r1:1.5b",
  },
  {
    value: "deepseek-r1:8b",
    label: "deepseek-r1:8b",
  },
  {
    value: "disabled",
    label: "Disabled",
    disabled: true,
  },
  {
    value: "yiminghe",
    label: "Yiminghe",
  },
]);
interface messageType {
  sender: "user" | "bot"; // 发送者
  content: string; // 大模型原本返回的消息内容
  displayContent: string; // 消息内容，经过解析后的 HTML
}
// 定义会话数据结构
const conversations = ref<any[]>([
  {
    messages: [
      {
        sender: "user",
        content: "你好",
        displayContent: "你好",
      },
      {
        sender: "bot",
        content: "你好，有什么问题吗？",
        displayContent: "你好，有什么问题吗？",
      },
    ],
  },
  {
    messages: [
      {
        sender: "user",
        content: "你好",
        displayContent: "你好",
      },
      {
        sender: "bot",
        content: "你好，有什么问题吗？",
        displayContent: "你好，有什么问题吗？",
      },
    ],
  },
]);
const isEnd = ref(false);
const pollingActive = ref(false);
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

const chatBox = ref<HTMLDivElement | null>(null);
const scrollToBottom = function () {
  if (chatBox.value) {
    chatBox.value.scrollTop = chatBox.value.scrollHeight;
  }
};

const polling = async function () {
  try {
    // 给定的字符串
    const response = await fetch(
      `/api/chat?message=${newMessage.value}&modelName=${selectedModel.value}`,
      {
        method: "GET",
        headers: {
          "Content-Type": "text/event-stream",
          Authorization: useStore.token,
        },
      }
    );
    if (!response.ok || !response.body) {
      if (response.status === 401) {
        // 跳转到登录页面
        message.error("登录信息已过期，请重新登录");
        window.location.href = "/login";
      }
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    newMessage.value = "";
    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");
    let buffer = ""; // 用于累积部分消息
    while (true) {
      const { done, value } = await reader.read();
      if (done) {
        isEnd.value = true;
        pollingActive.value = false;
        break;
      }
      buffer = decoder.decode(value, { stream: true });
      processServerSentEvent(buffer);
    }
    // 流结束时处理可能剩余的部分消息
    // this.processServerSentEvent(buffer);
  } catch (e: any) {
    console.log(e.toString());
  }
};

const addNewMessage = function (data: string) {
  if (data) {
    try {
      let newMessageContent = data;
      // 通过消息id获取目前的AI输入位置
      const aiMessage =
        currentConversation.value.messages[
          currentConversation.value.messages.length - 1
        ];
      // newMessageContent = this.markMessage(newMessageContent)
      if (aiMessage) {
        aiMessage.displayContent = `${aiMessage.displayContent}${newMessageContent}`;
      }
      scrollToBottom();
    } catch (error) {
      console.error("Failed to parse JSON:", error);
    }
  }
};
const processServerSentEvent = function (eventData: string) {
  console.log(eventData);
  // let currentMessage = "";
  const lines = eventData.split("\n");
  let currentMessage = "";
  lines.forEach((line: string) => {
    if (line.startsWith("data:")) {
      // 提取data字段的值（去掉前面的'data: '）
      let a = line.split(":");
      if (a[1] == "<think>") {
        currentMessage += "";
      } else if (a[1] == "</think>") {
        currentMessage += "";
      } else if (a[1] == "。") {
        currentMessage += ".\n";
      } else {
        currentMessage += a[1];
      }
      // currentMessage += a[1];
    } else {
      currentMessage += line.trim();
    }
  });
  markMessage(currentMessage);
  addNewMessage(currentMessage);
};
const markMessage = function (message: string) {
  // message = message.replaceAll("\\n", "\n");
  // 直接使用已配置的marked.parse
  return marked.parse(message);
};
const sendMessage = function () {
  if (!newMessage.value.trim()) return;

  // 添加用户消息
  currentConversation.value.messages.push({
    sender: "user",
    content: newMessage.value,
    displayContent: newMessage.value,
  });

  // this.userMsgData.content = markMessage(this.userInput);
  // send(this.userMsgData);

  // 清空输入框

  // 添加 AI 回复占位
  let newAiMessage = {
    sender: "bot",
    content: "",
    displayContent: "",
  };
  currentConversation.value.messages.push(newAiMessage);

  // this.currentAiMessageId = newAiMessage.id;

  // 启动轮询
  // if (this.isEnd || !this.pollingActive) {
  //   this.isEnd = false;
  //   this.pollingActive = true;
  //   this.polling();
  // }
  polling();
};
</script>

<style>
.think {
  text-indent: 2em;
  background: #f0f0f0;
  border-left: 4px solid #ccc;
  padding-left: 13px;
}
</style>
