import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useUserStore = defineStore('user', () => {
  const username = ref('');
  const email = ref('');
  const avatar = ref('');
  const token = ref('');

  // 新增：初始化时从localStorage加载数据
  const loadFromStorage = () => {
    const storedUser = localStorage.getItem('user_info');
    if (storedUser) {
      const user = JSON.parse(storedUser);
      username.value = user.username || '';
      email.value = user.email || '';
      avatar.value = user.avatar || '';
      token.value = user.token || '';
    }
  };
  loadFromStorage();

  const setUser = (info: { username: string, email: string, avatar: string, token: string }) => {
    username.value = info.username;
    email.value = info.email;
    avatar.value = info.avatar;
    token.value = info.token;

    // 新增：同步到localStorage
    localStorage.setItem('user_info', JSON.stringify({
      username: info.username,
      email: info.email,
      avatar: info.avatar,
      token: info.token
    }));
  };

  const clearUser = () => {
    username.value = '';
    email.value = '';
    avatar.value = ''; // 删除重复的avatar.value = '' 
    token.value = '';
    
    // 新增：清除localStorage
    localStorage.removeItem('user_info');
  };

  return {
    username,
    email,
    avatar,
    token,
    setUser,
    clearUser
  };
});