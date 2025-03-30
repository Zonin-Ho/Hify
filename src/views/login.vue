<template>
  <div class="flex items-center justify-center h-screen bg-gray-100">
    <div class="w-full max-w-md p-6 bg-white rounded-lg shadow-md">
      <h2 class="mb-6 text-2xl font-bold text-center">{{ currentForm }}</h2>
      <a-form
        :label-col="{ style: { width: '72px' } }"
        :model="form"
        :rules="rules"
        ref="formRef"
      >
        <!-- 登录表单 -->
        <template v-if="currentForm === '登录'">
          <a-form-item name="email" label="邮箱">
            <a-input
              v-model:value="form.email"
              placeholder="请输入邮箱"
              required
            />
          </a-form-item>
          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="form.password"
              placeholder="请输入密码"
              required
            />
          </a-form-item>
          <a-form-item class="w-full">
            <a-button class="w-full" type="primary" @click="handleSubmit"
              >登录</a-button
            >
          </a-form-item>
        </template>

        <!-- 注册表单 -->
        <template v-else-if="currentForm === '注册'">
          <a-form-item name="email" label="邮箱">
            <a-input
              v-model:value="form.email"
              placeholder="请输入邮箱"
              required
            />
          </a-form-item>
          <a-form-item name="password" label="密码">
            <a-input-password
              v-model:value="form.password"
              placeholder="请输入密码"
              required
            />
          </a-form-item>
          <a-form-item name="confirmPassword" label="确认密码">
            <a-input-password
              v-model:value="form.confirmPassword"
              placeholder="请确认密码"
              required
            />
          </a-form-item>
          <a-form-item class="w-full">
            <a-button class="w-full" type="primary" @click="handleSubmit"
              >注册</a-button
            >
          </a-form-item>
        </template>

        <!-- 找回密码表单 -->
        <template v-else-if="currentForm === '找回密码'">
          <a-form-item name="email" label="邮箱">
            <a-input
              v-model:value="form.email"
              placeholder="请输入邮箱"
              required
            />
          </a-form-item>
          <a-form-item class="w-full">
            <a-button class="w-full" type="primary" @click="handleSubmit"
              >找回密码</a-button
            >
          </a-form-item>
        </template>
      </a-form>
      <div class="flex justify-between">
        <span
          class="transition-colors cursor-pointer hover:text-blue-500"
          @click.prevent="switchForm(currentForm === '登录' ? '注册' : '登录')"
        >
          {{ currentForm === "登录" ? "注册新账号" : "已有账号，去登录" }}
        </span>
        <span
          v-if="currentForm === '登录'"
          class="transition-colors cursor-pointer hover:text-blue-500"
          @click.prevent="switchForm('找回密码')"
          >找回密码?</span
        >
      </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { message } from "ant-design-vue";
import { login, registry, forgotPassword } from "@/api/auth.ts";
import type { FormInstance, Rule } from "ant-design-vue/es/form";

const router = useRouter();

// 表单类型
const formRef = ref<FormInstance>();
const currentForm = ref("登录");

// 表单数据
const form = reactive({
  email: "",
  password: "",
  confirmPassword: "",
});

const rules: Record<string, Rule[]> = {
  email: [
    {
      required: true,
      message: "请输入邮箱",
      trigger: "blur",
    },
    {
      type: "email",
      message: "无效的邮箱格式",
      trigger: "blur",
    },
  ],
  password: [
    {
      required: true,
      message: "请输入密码",
      trigger: "blur",
    },
    {
      min: 6,
      max: 20,
      message: "密码长度在 6 到 20 个字符之间",
      trigger: "blur",
    },
  ],
  confirmPassword: [
    {
      required: true,
      message: "请输入确认密码",
      trigger: "blur",
    },
    {
      min: 6,
      max: 20,
      message: "密码长度在 6 到 20 个字符之间",
      trigger: "blur",
    },
    {
      validator: async (_rule, value) => {
        if (value != form.password) {
          return Promise.reject("两次输入的密码不一致");
        }
        return Promise.resolve();
      },
    },
  ],
};
// 切换表单
const switchForm = (title: string) => {
  currentForm.value = title;
  form.email = "";
  form.password = "";
  form.confirmPassword = "";
};

// 提交表单
const handleSubmit = async function () {
  if (!formRef.value) return;

  if (currentForm.value === "登录") {
    await formRef.value
      .validate(["email", "password"])
      .then(() => {
        login({ email: form.email, password: form.password }).then((res) => {
          if (res.code == "200") {
            message.success("登录成功");
            router.push("/chat");
          } else {
            message.error(res.msg);
          }
          console.log(res);
        });
      })
      .catch((error) => console.error(error));
  } else if (currentForm.value === "注册") {
    await formRef.value
      .validateFields(["email", "password", "confirmPassword"])
      .then(() => {
        registry({ email: form.email, password: form.password }).then((res) => {
          if (res.code == "200") {
            message.success("注册成功");
            switchForm("登录");
          } else {
            message.error(res.msg);
          }
        });
      });
  } else if (currentForm.value === "找回密码") {
    await formRef.value
      .validateFields(["email"])
      .then(() => {
        forgotPassword({ email: form.email });
        message.success("找回密码邮件已发送");
        switchForm("登录");
      })
      .catch((error) => console.error(error));
  }
};
</script>

<style scoped>
:deep(.ant-input):-webkit-autofill,
:deep(.ant-input):-webkit-autofill:hover,
:deep(.ant-input):-webkit-autofill:focus,
:deep(.ant-input):-webkit-autofill:active {
  background-color: var(--ant-input-bg) !important;
  color: var(--ant-input-color) !important;
  transition: background-color 50000s ease-in-out !important;
}

/* 处理 Firefox 浏览器 */
:deep(.ant-input):-moz-autofill,
:deep(.ant-input):-moz-autofill:hover,
:deep(.ant-input):-moz-autofill:focus,
:deep(.ant-input):-moz-autofill:active {
  background-color: var(--ant-input-bg) !important;
  color: var(--ant-input-color) !important;
}
</style>
