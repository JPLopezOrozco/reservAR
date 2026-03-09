import axios from 'axios'
import { getSession, logout } from '../util/auth'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || 'http://localhost:8080',
})

http.interceptors.request.use(cfg => {
  const s = getSession()
  if (s?.token) cfg.headers.Authorization = `Bearer ${s.token}`
  return cfg
})

http.interceptors.response.use(
  res => res,
  err => {
    const status = err?.response?.status
    if (status === 401 || status === 403) {
      logout()
      // evita loops si ya estás en /login
      if (!location.pathname.startsWith('/login')) {
        window.location.replace('/login')
      }
    }
    return Promise.reject(err)
  }
)

export default http