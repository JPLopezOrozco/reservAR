import { jwtDecode } from 'jwt-decode'

const TOKEN_KEY = 'token'

export function getJwt() {
  return localStorage.getItem(TOKEN_KEY) || null
}

function normalizeRole(raw) {
  if (!raw) return null
  return String(raw).toUpperCase().replace(/^ROLE_/, '')
}

// Verifica expiración del token (campo exp en segundos)
function isExpired(payload) {
  if (!payload?.exp) return false
  const now = Date.now() / 1000
  return now > payload.exp
}

// Retorna toda la sesión (o null si token inválido o expirado)
export function getSession() {
  const raw = getJwt()
  if (!raw) return null
  try {
    const payload = jwtDecode(raw)
    if (isExpired(payload)) {
      logout()
      return null
    }

    // ID del usuario (según tus claims reales)
    const id =
      payload.id ??
      payload.userId ??
      (typeof payload.sub === 'string' && /^\d+$/.test(payload.sub)
        ? Number(payload.sub)
        : payload.sub)

    // Role: puede venir en distintos campos
    let role = payload.role
    if (!role && Array.isArray(payload.roles) && payload.roles.length)
      role = payload.roles[0]
    if (!role && Array.isArray(payload.authorities) && payload.authorities.length)
      role = payload.authorities[0].authority ?? payload.authorities[0]
    role = normalizeRole(role)

    return { token: raw, id, role, payload }
  } catch {
    logout()
    return null
  }
}

// Helpers rápidos
export function getUsername() {
  return getSession()?.id ?? null
}

export function getRole() {
  return getSession()?.role ?? null
}

export function isStaffOrAdmin() {
  const r = getRole()
  return r === 'STAFF' || r === 'ADMIN'
}

export function isLoggedIn() {
  return !!getSession()
}

export function logout() {
  localStorage.removeItem(TOKEN_KEY)
  // redirige al login si no estás ya ahí
  if (!window.location.pathname.startsWith('/login')) {
    window.location.assign('/login')
  }
}