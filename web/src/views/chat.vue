<template>
  <div class="flex h-[calc(100vh-50px)]">
    <!-- 左侧会话列表 -->
    <div
      :class="[
        'w-[230px] relative border-r border-gray-200 bg-white flex flex-col flex-wrap p-4 transition-all duration-200',
        !isExpand && 'w-[40px] px-0',
      ]"
    >
      <LeftOutlined
        v-if="isExpand"
        @click="isExpand = !isExpand"
        class="absolute border bg-gray-200 cursor-pointer text-[gray] rounded-[50%] z-[1] right-0 translate-x-[50%] top-[50%]"
      />
      <RightOutlined
        v-else
        @click="isExpand = !isExpand"
        class="absolute border bg-gray-200 cursor-pointer text-[gray] rounded-[50%] z-[1] right-0 translate-x-[50%] top-[50%]"
      />
      <div
        :class="[
          'flex w-full mb-4 items-center justify-center h-[36px] bg-gray-100 rounded cursor-pointer ',
          // isExpand && 'm-4',
          isExpand && 'hover:bg-gray-200',
          !isExpand && 'm-0 bg-transparent my-4',
        ]"
        @click="isNewSession = true"
      >
        <span v-if="isExpand">新建会话</span>
        <a-tooltip placement="right" title="新建会话" v-else>
          <AppstoreAddOutlined />
        </a-tooltip>
      </div>
      <a-list class="w-full">
        <a-list-item
          :class="[
            '!p-0 cursor-pointer rounded-lg',
            isExpand && 'hover:bg-gray-100',
          ]"
          v-for="(conversation, index) in conversations"
          :key="index"
          @click="selectConversation(index)"
        >
          <a-list-item-meta class="group">
            <template #description>
              <div
                :class="[
                  !isExpand && '!justify-center',
                  'flex justify-between content-center w-full h-10 text-center',
                ]"
              >
                <span v-if="isExpand" class="pl-4 leading-10 text-black">
                  {{
                    conversation.messages[0]?.displayContent || "无消息"
                  }}</span
                >
                <a-tooltip
                  v-else
                  placement="right"
                  :title="conversation.messages[0]?.displayContent || '无消息'"
                  ><MessageOutlined
                /></a-tooltip>
                <a-popover
                  placement="bottomLeft"
                  trigger="click"
                  v-if="isExpand"
                >
                  <template #content>
                    <div class="flex flex-col justify-items-start">
                      <a-button type="link" class="p-0 text-black"
                        >重命名</a-button
                      >
                      <a-button type="link" class="p-0 text-black"
                        >置顶</a-button
                      >
                      <a-button
                        type="link"
                        class="text-[red] p-0 hover:!text-red-600"
                        >删除</a-button
                      >
                    </div>
                  </template>
                  <a-button
                    type="link"
                    size="small"
                    class="hidden mt-2 mr-4 group-hover:block hover:bg-slate-200"
                  >
                    <template #icon><MoreOutlined /></template>
                  </a-button>
                </a-popover>
              </div>
            </template>
          </a-list-item-meta>
        </a-list-item>
      </a-list>
      <div
        :class="[
          'flex items-center mt-auto w-full rounded-lg cursor-pointer user-info group',
          isExpand && 'px-2 py-1 hover:bg-gray-200',
          !isExpand && 'justify-center',
        ]"
        @click="showModel = true"
      >
        <a-avatar
          src="http://localhost:9000/hify/b_b75df21309c6906e28cbc27f6d781135.jpg"
          :class="['mr-2 aspect-square min-w-8', !isExpand && '!mr-0']"
          shape="square"
        >
          <!-- <template #icon><UserOutlined /></template> -->
        </a-avatar>
        <span
          v-if="isExpand"
          :title="useStore.username"
          class="overflow-hidden w-48 text-lg text-gray-800 text-ellipsis user-name"
          >{{ useStore.username }}</span
        >
        <RightOutlined
          class="hidden text-black group-hover:block"
          v-if="isExpand"
        />
      </div>
    </div>

    <!-- 中间聊天区域 -->
    <div class="flex flex-col flex-1 p-5 space-y-4 bg-gray-100">
      <div class="overflow-y-auto flex-1 p-2" ref="chatBox">
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
              v-if="message.displayContent && message.sender === 'bot'"
              class="p-3 rounded max-w-[70%] bg-white text-blue-500 markdown-body pb-0"
            >
              <Markdown :source="message.displayContent"></Markdown>
              <!-- class="last-child:after:h-4 last-child:after:border-x last-child:after:inline last-child:after:animate-pulse" -->
            </div>
            <div
              v-else-if="message.displayContent && message.sender === 'user'"
              class="p-3 rounded max-w-[70%] bg-blue-500 text-white"
            >
              {{ message.displayContent }}
            </div>

            <!-- 加载中 -->
            <div
              v-else-if="!message.displayContent && !isAbort"
              class="p-3 rounded max-w-[70%] bg-white text-blue-500"
            >
              <a-spin />
            </div>
            <!-- <span v-else class="p-3 rounded max-w-[70%] bg-white text-red-500">
              请求超时，请稍后再试
            </span> -->
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
        <div class="flex flex-wrap justify-between">
          <div class="flex flex-wrap justify-between">
            <a-select
              show-search
              filter-option
              style="width: 240px; margin-right: 12px"
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
    <Setting v-model:open="showModel"></Setting>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch } from "vue";
import { message, type SelectProps } from "ant-design-vue";
import { useUserStore } from "@/stores/userStore";
import "github-markdown-css/github-markdown-light.css";
import Markdown from "vue3-markdown-it";
import "highlight.js/styles/github.css";
import { nextTick } from "vue";
import Setting from "./setting.vue";
import {
  LeftOutlined,
  RightOutlined,
  MessageOutlined,
  AppstoreAddOutlined,
  MoreOutlined,
} from "@ant-design/icons-vue";
const useStore = useUserStore();
const isNewSession = ref(false);

const showModel = ref(false);
// const isThinking = ref(false);
const isAbort = ref(false);
const isExpand = ref(true);
const isDeepThink = ref(true);
const selectedModel = ref(
  localStorage.getItem("selectedModel") || "Qwen/QwQ-32B"
);

watch(
  () => selectedModel.value,
  (newValue) => {
    localStorage.setItem("selectedModel", newValue);
  }
);

const modelOption = ref<SelectProps["options"]>([
  { value: "Qwen/QwQ-32B", label: "Qwen/QwQ-32B" },
  {
    value: "Pro/deepseek-ai/DeepSeek-R1",
    label: "Pro/deepseek-ai/DeepSeek-R1",
  },
  {
    value: "Pro/deepseek-ai/DeepSeek-V3",
    label: "Pro/deepseek-ai/DeepSeek-V3",
  },
  { value: "deepseek-ai/DeepSeek-R1", label: "deepseek-ai/DeepSeek-R1" },
  { value: "deepseek-ai/DeepSeek-V3", label: "deepseek-ai/DeepSeek-V3" },
  {
    value: "deepseek-ai/DeepSeek-R1-Distill-Qwen-32B",
    label: "deepseek-ai/DeepSeek-R1-Distill-Qwen-32B",
  },
  {
    value: "deepseek-ai/DeepSeek-R1-Distill-Qwen-14B",
    label: "deepseek-ai/DeepSeek-R1-Distill-Qwen-14B",
  },
  {
    value: "deepseek-ai/DeepSeek-R1-Distill-Qwen-7B",
    label: "deepseek-ai/DeepSeek-R1-Distill-Qwen-7B",
  },
  {
    value: "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",
    label: "deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",
  },
  {
    value: "Pro/deepseek-ai/DeepSeek-R1-Distill-Qwen-7B",
    label: "Pro/deepseek-ai/DeepSeek-R1-Distill-Qwen-7B",
  },
  {
    value: "Pro/deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",
    label: "Pro/deepseek-ai/DeepSeek-R1-Distill-Qwen-1.5B",
  },
  { value: "deepseek-ai/DeepSeek-V2.5", label: "deepseek-ai/DeepSeek-V2.5" },
  {
    value: "Qwen/Qwen2.5-72B-Instruct-128K",
    label: "Qwen/Qwen2.5-72B-Instruct-128K",
  },
  { value: "Qwen/Qwen2.5-72B-Instruct", label: "Qwen/Qwen2.5-72B-Instruct" },
  { value: "Qwen/Qwen2.5-32B-Instruct", label: "Qwen/Qwen2.5-32B-Instruct" },
  { value: "Qwen/Qwen2.5-14B-Instruct", label: "Qwen/Qwen2.5-14B-Instruct" },
  { value: "Qwen/Qwen2.5-7B-Instruct", label: "Qwen/Qwen2.5-7B-Instruct" },
  {
    value: "Qwen/Qwen2.5-Coder-32B-Instruct",
    label: "Qwen/Qwen2.5-Coder-32B-Instruct",
  },
  {
    value: "Qwen/Qwen2.5-Coder-7B-Instruct",
    label: "Qwen/Qwen2.5-Coder-7B-Instruct",
  },
  { value: "Qwen/Qwen2-7B-Instruct", label: "Qwen/Qwen2-7B-Instruct" },
  { value: "Qwen/Qwen2-1.5B-Instruct", label: "Qwen/Qwen2-1.5B-Instruct" },
  { value: "Qwen/QwQ-32B-Preview", label: "Qwen/QwQ-32B-Preview" },
  // { value: "TeleAI/TeleChat2", label: "TeleAI/TeleChat2" },
  { value: "THUDM/glm-4-9b-chat", label: "THUDM/glm-4-9b-chat" },
  {
    value: "Vendor-A/Qwen/Qwen2.5-72B-Instruct",
    label: "Vendor-A/Qwen/Qwen2.5-72B-Instruct",
  },
  {
    value: "internlm/internlm2_5-7b-chat",
    label: "internlm/internlm2_5-7b-chat",
  },
  {
    value: "internlm/internlm2_5-20b-chat",
    label: "internlm/internlm2_5-20b-chat",
  },
  {
    value: "Pro/Qwen/Qwen2.5-7B-Instruct",
    label: "Pro/Qwen/Qwen2.5-7B-Instruct",
  },
  { value: "Pro/Qwen/Qwen2-7B-Instruct", label: "Pro/Qwen/Qwen2-7B-Instruct" },
  {
    value: "Pro/Qwen/Qwen2-1.5B-Instruct",
    label: "Pro/Qwen/Qwen2-1.5B-Instruct",
  },
  { value: "Pro/THUDM/chatglm3-6b", label: "Pro/THUDM/chatglm3-6b" },
  { value: "Pro/THUDM/glm-4-9b-chat", label: "Pro/THUDM/glm-4-9b-chat" },
]);
// interface messageType {
//   sender: "user" | "bot"; // 发送者
//   displayContent: string; // 消息内容，经过解析后的 HTML
// }
// 定义会话数据结构
const conversations = ref<any[]>([
  {
    messages: [
      {
        sender: "user",
        displayContent: "你好",
      },
      {
        sender: "bot",
        displayContent: "你好，有什么问题吗？",
      },
    ],
  },
  {
    messages: [
      {
        sender: "user",
        displayContent: "你好",
      },
      {
        sender: "bot",
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
    // isThinking.value = true;
    isAbort.value = false;
    const controller = new AbortController();
    // 给定的字符串
    let timer = setTimeout(() => {
      controller.abort();
    }, 60000);
    const response = await fetch(`/api/chat`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: useStore.token,
      },
      signal: controller.signal,
      body: JSON.stringify({
        message: newMessage.value,
        modelName: selectedModel.value,
      }),
    });
    clearTimeout(timer);
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
  } catch (e: any) {
    if (e.name === "AbortError") {
      isAbort.value = true;
      console.log("请求已被中止");
      message.error("请求超时，请稍后再试");
      delNewMessage();
    }
    console.log(e.toString());
  }
};

const delNewMessage = function () {
  currentConversation.value.messages.pop();
};

const addNewMessage = async function (data: string) {
  if (data) {
    try {
      let newMessageContent = data;
      // 通过消息id获取目前的AI输入位置
      const aiMessage =
        currentConversation.value.messages[
          currentConversation.value.messages.length - 1
        ];
      if (aiMessage) {
        aiMessage.displayContent = `${aiMessage.displayContent}${newMessageContent}`;
      }
      nextTick(() => {
        scrollToBottom();
      });
    } catch (error) {
      console.error("Failed to parse JSON:", error);
    }
  }
};
const processServerSentEvent = function (eventData: string) {
  let currentMessage = "";
  console.log("完整数据：", eventData);
  let line = eventData.split("\n");
  for (let i = 0; i < line.length; i++) {
    if (line[i].endsWith("[DONE]")) {
      break;
    }

    if (line[i].startsWith("data:") && line[i].slice(5)) {
      console.log("转换前数据", line[i]);

      let data = JSON.parse(line[i].slice(5)).message;
      console.log("转换后数据", data);

      if (data.endsWith("/\n/\n")) {
        currentMessage += data.slice(0, -2) + "\n\n";
      } else {
        currentMessage += data;
      }
    }
  }
  addNewMessage(currentMessage);
};

const sendMessage = async function () {
  if (!newMessage.value.trim()) return;

  // 添加用户消息
  currentConversation.value.messages.push({
    sender: "user",
    displayContent: newMessage.value,
  });

  // 清空输入框

  // 添加 AI 回复占位
  let newAiMessage = {
    sender: "bot",
    displayContent: "",
  };
  currentConversation.value.messages.push(newAiMessage);
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
