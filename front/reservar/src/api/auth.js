import { api } from './client'

export async function login({ email, password }) {
  const { data } = await api.post('/auth/user/login', { email, password })
  // data: { token }
  localStorage.setItem('token', data.token)
  return data
}

export async function registerUser(body) {
  // {email,password,name,surname,phone}
  const { data } = await api.post('/auth/user', body)
  return data
}

export function logout() {
  localStorage.removeItem('token')
}