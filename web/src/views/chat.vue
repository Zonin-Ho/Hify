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
        <div v-if="isNewSession">欢迎使用智能对话助手，试着向我提问吧</div>
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
              class="p-3 rounded max-w-[100%] bg-white text-blue-500 markdown-body"
              v-html="md.render(message.displayContent)"
            ></div>
            <!-- <Markdown :source="message.displayContent"></Markdown> -->
            <!-- class="last-child:after:h-4 last-child:after:border-x last-child:after:inline last-child:after:animate-pulse" -->
            <div
              v-else-if="message.displayContent && message.sender === 'user'"
              class="p-3 rounded max-w-[100%] bg-blue-500 text-white"
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
      <div class="flex flex-col p-2 space-y-2 bg-white rounded-lg">
        <a-textarea
          class="border-none focus:border-none focus:shadow-none"
          v-model:value="newMessage"
          placeholder="有问题，尽管问"
          :auto-size="{ minRows: 1, maxRows: 4 }"
          allow-clear
        />
        <div class="flex gap-2 justify-between">
          <div class="flex justify-between w-full md:w-fit">
            <a-select
              show-search
              filter-option
              class="md:w-[240px] mr-3"
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
          <div class="flex justify-end w-full">
            <a-tooltip placement="top" title="语音输入">
              <a-button
                class="flex justify-center items-center self-end mr-2"
                shape="circle"
                :type="isRecording ? 'primary' : 'default'"
                @click="audio2text"
              >
                <template #icon><AudioTwoTone /></template>
              </a-button>
            </a-tooltip>
            <a-tooltip v-if="!isThinking" placement="top" title="发送">
              <a-button
                type="primary"
                class="flex justify-center items-center self-end"
                shape="circle"
                @click="sendMessage"
                :disabled="!newMessage.trim()"
              >
                <template #icon>
                  <SendOutlined />
                </template>
              </a-button>
            </a-tooltip>
            <a-tooltip v-else placement="top" title="暂停输出">
              <a-button
                type="primary"
                class="flex justify-center items-center self-end"
                shape="circle"
                @click="stopThinking"
              >
                <template #icon>
                  <CloseSquareOutlined />
                </template>
              </a-button>
            </a-tooltip>
          </div>
        </div>
      </div>
    </div>
    <Setting v-model:open="showModel"></Setting>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, watch, onMounted } from "vue";
import { message, type SelectProps } from "ant-design-vue";
import { useUserStore } from "@/stores/userStore";
import "github-markdown-css/github-markdown-light.css";
import "highlight.js/styles/github.css";
import { nextTick } from "vue";
import Setting from "./setting.vue";
import {
  LeftOutlined,
  RightOutlined,
  MessageOutlined,
  AppstoreAddOutlined,
  MoreOutlined,
  SendOutlined,
  CloseSquareOutlined,
  AudioTwoTone,
} from "@ant-design/icons-vue";
import MarkdownIt from "markdown-it";
import hljs from "highlight.js";
import Clipboard from "clipboard";

const md = new MarkdownIt({
  html: true,
  linkify: true,
  typographer: true,
  highlight: function (str: string, lang: string) {
    // 当前时间加随机数生成唯一的id标识
    const codeIndex = (
      parseInt(Date.now().toString()) + Math.floor(Math.random() * 10000000)
    ).toString();
    // 复制功能主要使用的是 clipboard.js
    let html = `<button class="copy-btn" type="button" data-clipboard-action="copy" data-clipboard-target="#copy${codeIndex}">复制</button>`;
    const linesLength = str.split(/\n/).length - 1;
    // 生成行号
    let linesNum = '<span aria-hidden="true" class="line-numbers-rows">';
    for (let index = 0; index < linesLength; index++) {
      linesNum = linesNum + "<span></span>";
    }
    linesNum += "</span>";
    if (lang && hljs.getLanguage(lang)) {
      try {
        // highlight.js 高亮代码
        const preCode = hljs.highlight(lang, str, true).value;
        html = html + preCode;
        if (linesLength) {
          html += '<b class="name">' + lang + "</b>";
        }
        // 将代码包裹在 textarea 中
        return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(
          /<\/textarea>/g,
          "&lt;/textarea>"
        )}</textarea>`;
      } catch (error) {
        console.log(error);
      }
    }

    const preCode = md.utils.escapeHtml(str);
    html = html + preCode;
    // 将代码包裹在 textarea 中
    return `<pre class="hljs"><code>${html}</code>${linesNum}</pre><textarea style="position: absolute;top: -9999px;left: -9999px;z-index: -9999;" id="copy${codeIndex}">${str.replace(
      /<\/textarea>/g,
      "&lt;/textarea>"
    )}</textarea>`;
  },
});

const useStore = useUserStore();
// 是否新建会话
const isNewSession = ref(false);
// 模态框开关
const showModel = ref(false);
// 模型是否正在输出
const isThinking = ref(false);
// 是否终止请求
const isAbort = ref(false);
// 侧边栏开关
const isExpand = ref(true);
// 是否开启深度思考
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
  {
    label: "硅基流动",
    options: [
      { value: "Qwen/QwQ-32B", label: "QwQ-32B" },
      {
        value: "Pro/deepseek-ai/DeepSeek-R1",
        label: "DeepSeek-R1",
      },
      {
        value: "Pro/deepseek-ai/DeepSeek-V3",
        label: "DeepSeek-V3",
      },
      {
        value: "Qwen/Qwen2.5-72B-Instruct",
        label: "Qwen2.5-72B-Instruct",
      },
      { value: "Qwen/QwQ-32B-Preview", label: "QwQ-32B-Preview" },
      {
        value: "Vendor-A/Qwen/Qwen2.5-72B-Instruct",
        label: "Qwen2.5-72B-Instruct",
      },
      {
        value: "internlm/internlm2_5-20b-chat",
        label: "internlm2_5-20b-chat",
      },
    ],
  },
  {
    label: "阿里百炼",
    options: [
      { value: "qwen-max", label: "qwen-max" },
      { value: "qwq-plus", label: "qwq-plus" },
      { value: "deepseek-r1", label: "deepseek-r1" },
      {
        value: "deepseek-r1-distill-qwen-7b",
        label: "deepseek-r1-distill-qwen-7b",
      },
    ],
  },
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
// const isEnd = ref(false);
// const pollingActive = ref(false);
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
let controller: AbortController | String | null = "";

function stopThinking() {
  isThinking.value = false;
  isAbort.value = true;
  if (controller && typeof controller !== "string") {
    (controller as AbortController).abort();
  }
}
const polling = async function () {
  try {
    if (controller) {
      controller = null;
    }
    controller = new AbortController();
    let msg = newMessage.value;
    newMessage.value = "";
    // isThinking.value = true;
    isAbort.value = false;

    // 给定的字符串
    let timer = setTimeout(() => {
      if (controller && typeof controller !== "string") {
        (controller as AbortController).abort();
      }
    }, 120000);
    isThinking.value = true;
    const response = await fetch(`/api/chat`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        // "Cache-Control": "no-cache",
        // "X-Accel-Buffering": "no",
        Authorization: useStore.token,
      },
      signal: controller.signal,
      body: JSON.stringify({
        message: msg,
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

    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");
    // let buffer = ""; // 用于累积部分消息
    let done = false;
    while (!done) {
      const { done: readerDone, value } = await reader.read();
      done = readerDone;
      let chunk = decoder.decode(value, { stream: true });
      console.log("数据：", chunk);

      await processServerSentEvent(chunk);
    }
  } catch (e: any) {
    if (e.name === "AbortError") {
      isAbort.value = true;
      isThinking.value = false;
      console.log("请求已被中止");
      message.error("请求超时，请稍后再试");
      delNewMessage();
      let newAiMessage = {
        sender: "bot",
        displayContent: "已停止生成内容",
      };
      currentConversation.value.messages.push(newAiMessage);
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
const processServerSentEvent = async function (eventData: string) {
  let currentMessage = "";
  // console.log("完整数据：", eventData);
  // let data = JSON.parse(eventData.slice(5)).message.replace("/\n", "\n");
  // currentMessage += data;
  let line = eventData.split("\n");
  for (let i = 0; i < line.length; i++) {
    if (line[i].endsWith("[DONE]")) {
      isThinking.value = false;
      break;
    }

    // if (line[i].startsWith("data:") && line[i].slice(5)) {
    //   // 处理前面的"data:
    //   let data = JSON.parse(line[i].slice(5)).message.replace("/\n", "\n");
    //   if (data == "<think>") {
    //     currentMessage += "<div class='think'>";
    //   } else if (data == "</think>") {
    //     currentMessage += "</div>";
    //   } else {
    //     currentMessage += data;
    //   }
    //   // currentMessage += data;
    //   // 处理换行符
    //   // if (data.endsWith("/\n/\n")) {
    //   //   currentMessage += data.slice(0, -2) + "\n\n";
    //   // } else {

    //   // }
    // }
    if (line[i].endsWith("}")) {
      let data = JSON.parse(line[i].replace("data:", "")).message.replace(
        "/\n",
        "\n"
      );
      if (data == "<think>") {
        currentMessage += "<div class='think'>";
      } else if (data == "</think>") {
        currentMessage += "</div>";
      } else {
        currentMessage += data;
      }
    }
  }
  addNewMessage(currentMessage);
};

// 发送消息
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

// 语音识别状态
const isRecording = ref(false);
// 语音识别对象
let recognition: any = null;

// 语音转文字功能
const audio2text = function () {
  // 如果已经在录音，则停止录音
  if (isRecording.value) {
    stopRecording();
    return;
  }

  // 检查浏览器是否支持语音识别
  if (
    !("webkitSpeechRecognition" in window) &&
    !("SpeechRecognition" in window)
  ) {
    message.error("您的浏览器不支持语音识别功能，请使用Chrome浏览器");
    return;
  }

  // 创建语音识别对象
  const SpeechRecognition =
    (window as any).SpeechRecognition ||
    (window as any).webkitSpeechRecognition;
  recognition = new SpeechRecognition();

  // 设置语音识别参数
  recognition.lang = "zh-CN"; // 设置语言为中文
  recognition.continuous = false; // 不持续识别
  recognition.interimResults = false; // 不返回中间结果

  // 开始录音前提示用户
  message.loading("正在听您说话...");
  isRecording.value = true;

  // 开始录音
  recognition.start();

  // 识别结果处理
  recognition.onresult = function (event: {
    results: { transcript: any }[][];
  }) {
    const result = event.results[0][0].transcript;
    newMessage.value += result;
    message.success("语音识别成功");
    isRecording.value = false;
  };

  // 错误处理
  recognition.onerror = function (event: { error: any }) {
    console.error("语音识别错误:", event.error);
    message.error("语音识别失败，请重试");
    isRecording.value = false;
  };

  // 识别结束
  recognition.onend = function () {
    message.destroy(); // 清除所有消息提示
    isRecording.value = false;
  };
};

// 停止录音
const stopRecording = function () {
  if (recognition) {
    recognition.stop();
    message.info("已停止录音");
    isRecording.value = false;
  }
};

onMounted(() => {
  nextTick(() => {
    let clipboard = new Clipboard(".copy-btn");
    clipboard.on("success", function (e: { clearSelection: () => void }) {
      message.success("复制成功");
      e.clearSelection();
    });
    clipboard.on("error", function () {
      message.error("复制失败");
    });
  });
});
</script>

<style lang="scss">
.think {
  text-indent: 2em;
  background: #f0f0f0;
  border-left: 4px solid #ccc;
  padding-left: 13px;
}
pre.hljs {
  padding: 12px 2px 30px 40px !important;
  border-radius: 5px !important;
  position: relative;
  font-size: 14px !important;
  line-height: 22px !important;
  overflow: hidden !important;
  code {
    display: block !important;
    margin: 0 10px !important;
    overflow-x: auto !important;
  }
  .line-numbers-rows {
    position: absolute;
    pointer-events: none;
    top: 12px;
    bottom: 12px;
    left: 0;
    font-size: 100%;
    width: 40px;
    text-align: center;
    letter-spacing: -1px;
    border-right: 1px solid rgba(0, 0, 0, 0.66);
    user-select: none;
    counter-reset: linenumber;
    span {
      pointer-events: none;
      display: block;
      counter-increment: linenumber;
      &:before {
        content: counter(linenumber);
        color: #999;
        display: block;
        text-align: center;
      }
    }
  }
  b.name {
    position: absolute;
    bottom: 2px;
    right: 10px;
    z-index: 10;
    font-size: 12px;
    color: #999;
  }
  .copy-btn {
    position: absolute;
    top: 2px;
    right: 4px;
    z-index: 10;
    color: #333;
    cursor: pointer;
    padding: 4px;
    border: 0;
    border-radius: 2px;
    &:hover {
      background-color: #fff;
    }
  }
}
</style>
