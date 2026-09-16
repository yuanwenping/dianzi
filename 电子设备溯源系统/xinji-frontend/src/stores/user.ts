import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('xinji-token') || '')
  const username = ref(localStorage.getItem('xinji-username') || '')
  const companyName = ref(localStorage.getItem('xinji-company') || '')
  const role = ref(localStorage.getItem('xinji-role') || '')

  function setLogin(data: { token: string; username: string; companyName: string; role: string }) {
    token.value = data.token
    username.value = data.username
    companyName.value = data.companyName
    role.value = data.role

    localStorage.setItem('xinji-token', data.token)
    localStorage.setItem('xinji-username', data.username)
    localStorage.setItem('xinji-company', data.companyName)
    localStorage.setItem('xinji-role', data.role)
  }

  function logout() {
    token.value = ''
    username.value = ''
    companyName.value = ''
    role.value = ''

    localStorage.removeItem('xinji-token')
    localStorage.removeItem('xinji-username')
    localStorage.removeItem('xinji-company')
    localStorage.removeItem('xinji-role')
  }

  const isLoggedIn = () => !!token.value
  const isAdmin = () => role.value === 'admin'

  return { token, username, companyName, role, setLogin, logout, isLoggedIn, isAdmin }
})
