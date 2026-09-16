<template>
  <div class="login-page">
    <div class="login-container glass-card">
      <!-- 品牌区 -->
      <div class="brand-section">
        <div class="brand-icon">
          <svg viewBox="0 0 48 48" width="48" height="48">
            <defs>
              <linearGradient id="chipGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" style="stop-color:#409EFF"/>
                <stop offset="100%" style="stop-color:#8B5CF6"/>
              </linearGradient>
            </defs>
            <rect x="8" y="8" width="32" height="32" rx="4" fill="url(#chipGrad)" opacity="0.15"/>
            <rect x="12" y="12" width="24" height="24" rx="2" fill="none" stroke="url(#chipGrad)" stroke-width="2"/>
            <rect x="18" y="18" width="12" height="12" rx="1" fill="url(#chipGrad)"/>
            <line x1="8" y1="8" x2="18" y2="18" stroke="url(#chipGrad)" stroke-width="1.5"/>
            <line x1="40" y1="8" x2="30" y2="18" stroke="url(#chipGrad)" stroke-width="1.5"/>
            <line x1="8" y1="40" x2="18" y2="30" stroke="url(#chipGrad)" stroke-width="1.5"/>
            <line x1="40" y1="40" x2="30" y2="30" stroke="url(#chipGrad)" stroke-width="1.5"/>
          </svg>
        </div>
        <h1 class="brand-name">芯迹 Xinji</h1>
        <p class="brand-desc">电子设备零件溯源系统 · 管理员入口</p>
      </div>

      <!-- 切换 tab -->
      <div class="form-tabs">
        <span :class="{ active: isLogin }" @click="isLogin = true">登录</span>
        <span class="tab-divider">|</span>
        <span :class="{ active: !isLogin }" @click="isLogin = false">注册</span>
      </div>

      <!-- 登录表单 -->
      <el-form v-if="isLogin" ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码"
                    :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleLogin" round>
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 注册表单 -->
      <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
        <el-form-item prop="username">
          <el-input v-model="registerForm.username" placeholder="用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="密码"
                    :prefix-icon="Lock" show-password />
        </el-form-item>
        <el-form-item prop="companyName">
          <el-input v-model="registerForm.companyName" placeholder="企业名称（选填）" :prefix-icon="OfficeBuilding" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" class="submit-btn" :loading="loading" @click="handleRegister" round>
            注 册
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { User, Lock, OfficeBuilding } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { authApi } from '@/api/auth'
import { useUserStore } from '@/stores/user'
import type { FormInstance, FormRules } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const isLogin = ref(true)
const loading = ref(false)

const loginFormRef = ref<FormInstance>()
const loginForm = reactive({ username: '', password: '' })
const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const registerFormRef = ref<FormInstance>()
const registerForm = reactive({ username: '', password: '', companyName: '' })
const registerRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 50, message: '用户名长度3-50位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 100, message: '密码长度6-100位', trigger: 'blur' }
  ]
}

async function handleLogin() {
  const valid = await loginFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await authApi.login(loginForm)
    userStore.setLogin(res.data.data)
    ElMessage.success('登录成功')
    router.push('/admin/dashboard')
  } catch {
    // error handled in interceptor
  } finally {
    loading.value = false
  }
}

async function handleRegister() {
  const valid = await registerFormRef.value?.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await authApi.register(registerForm)
    ElMessage.success('注册成功，请登录')
    isLogin.value = true
    registerForm.username = ''
    registerForm.password = ''
    registerForm.companyName = ''
  } catch {
    // error handled in interceptor
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  background: linear-gradient(135deg, #e8f0fe 0%, #ede9fe 50%, #e0f2fe 100%);
}

.login-container {
  width: 420px;
  max-width: 100%;
  padding: 48px 40px 40px;
  text-align: center;
}

.brand-section {
  margin-bottom: 28px;

  .brand-icon { margin-bottom: 12px; }

  .brand-name {
    font-size: 28px;
    font-weight: 800;
    background: linear-gradient(135deg, #409EFF, #8B5CF6);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    letter-spacing: 2px;
  }

  .brand-desc {
    font-size: 13px;
    color: #909399;
    margin-top: 6px;
  }
}

.form-tabs {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 16px;
  margin-bottom: 28px;
  font-size: 16px;

  span {
    cursor: pointer;
    color: #909399;
    transition: color 0.2s;
    font-weight: 500;

    &.active {
      color: #409EFF;
      font-weight: 700;
    }

    &:not(.tab-divider):hover {
      color: #409EFF;
    }
  }

  .tab-divider {
    color: #DCDFE6;
    cursor: default;
  }
}

.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #409EFF, #8B5CF6);
  border: none;
  height: 44px;
  font-size: 16px;
  letter-spacing: 4px;

  &:hover {
    background: linear-gradient(135deg, #337ECC, #7C4DFF);
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 36px 24px 32px;
  }
}
</style>
