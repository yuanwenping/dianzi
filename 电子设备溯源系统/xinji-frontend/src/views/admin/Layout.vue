<template>
  <div class="admin-layout">
    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ collapsed: isCollapse }">
      <div class="sidebar-header" @click="$router.push('/admin/dashboard')">
        <svg viewBox="0 0 48 48" width="32" height="32">
          <defs>
            <linearGradient id="chipGrad3" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#409EFF"/>
              <stop offset="100%" style="stop-color:#8B5CF6"/>
            </linearGradient>
          </defs>
          <rect x="8" y="8" width="32" height="32" rx="4" fill="url(#chipGrad3)" opacity="0.15"/>
          <rect x="12" y="12" width="24" height="24" rx="2" fill="none" stroke="url(#chipGrad3)" stroke-width="2"/>
          <rect x="18" y="18" width="12" height="12" rx="1" fill="url(#chipGrad3)"/>
        </svg>
        <span v-show="!isCollapse" class="sidebar-title">芯迹管理</span>
      </div>

      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        router
        background-color="transparent"
        text-color="rgba(255,255,255,0.7)"
        active-text-color="#fff"
        class="sidebar-menu"
      >
        <el-menu-item index="/admin/dashboard">
          <el-icon><DataAnalysis /></el-icon>
          <span>数据看板</span>
        </el-menu-item>
        <el-menu-item index="/admin/parts">
          <el-icon><Cpu /></el-icon>
          <span>零件管理</span>
        </el-menu-item>
      </el-menu>

      <div class="sidebar-footer">
        <el-button class="collapse-btn" @click="isCollapse = !isCollapse" text>
          <el-icon><Fold v-if="!isCollapse" /><Expand v-else /></el-icon>
        </el-button>
      </div>
    </aside>

    <!-- 主内容 -->
    <div class="main-area" :class="{ expanded: isCollapse }">
      <header class="topbar">
        <div class="topbar-left">
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/admin' }">管理后台</el-breadcrumb-item>
            <el-breadcrumb-item v-if="activeMenu !== '/admin/dashboard'">
              {{ activeMenu === '/admin/parts' ? '零件管理' : '溯源管理' }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="topbar-right">
          <span class="user-info">
            <el-icon><User /></el-icon>
            {{ userStore.username }}
            <el-tag size="small" type="primary" effect="dark" round>{{ userStore.companyName }}</el-tag>
          </span>
          <el-button text @click="goToQuery">
            <el-icon><View /></el-icon>
            查询页面
          </el-button>
          <el-button text type="danger" @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>
            退出
          </el-button>
        </div>
      </header>

      <main class="content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  DataAnalysis, Cpu, Fold, Expand, User, View, SwitchButton
} from '@element-plus/icons-vue'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)

const activeMenu = computed(() => {
  const path = route.path
  if (path.includes('/admin/parts') && !path.includes('traces')) return '/admin/parts'
  if (path.includes('/admin/traces') || path.includes('/admin/parts/')) return '/admin/parts'
  return '/admin/dashboard'
})

function goToQuery() {
  window.open('/#/query', '_blank')
}

function handleLogout() {
  ElMessageBox.confirm('确定要退出登录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    userStore.logout()
    router.push('/login')
  }).catch(() => {})
}
</script>

<style lang="scss" scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
}

// 侧边栏
.sidebar {
  width: 220px;
  background: linear-gradient(180deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
  z-index: 100;

  &.collapsed {
    width: 64px;
  }
}

.sidebar-header {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 20px 16px;
  cursor: pointer;

  .sidebar-title {
    color: #fff;
    font-size: 18px;
    font-weight: 700;
    letter-spacing: 2px;
    white-space: nowrap;
  }
}

.sidebar-menu {
  flex: 1;
  border-right: none !important;

  :deep(.el-menu-item) {
    margin: 4px 8px;
    border-radius: 10px;

    &:hover {
      background: rgba(255, 255, 255, 0.08) !important;
    }

    &.is-active {
      background: linear-gradient(135deg, #409EFF, #8B5CF6) !important;
    }
  }

  :deep(.el-menu-item i) {
    color: inherit;
  }
}

.sidebar-footer {
  padding: 12px;
  text-align: center;

  .collapse-btn {
    color: rgba(255, 255, 255, 0.5);
  }
}

// 主区域
.main-area {
  flex: 1;
  margin-left: 220px;
  transition: margin-left 0.3s;
  display: flex;
  flex-direction: column;
  min-height: 100vh;

  &.expanded {
    margin-left: 64px;
  }
}

.topbar {
  height: 60px;
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 50;
}

.topbar-left {
  :deep(.el-breadcrumb__inner) {
    font-weight: 500;
  }
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 12px;

  .user-info {
    display: flex;
    align-items: center;
    gap: 6px;
    font-size: 14px;
    color: #606266;
  }
}

.content {
  flex: 1;
  padding: 24px;
}

@media (max-width: 768px) {
  .sidebar {
    width: 64px;

    .sidebar-title { display: none; }
  }

  .main-area {
    margin-left: 64px;
  }

  .topbar-right .user-info {
    display: none;
  }
}
</style>
