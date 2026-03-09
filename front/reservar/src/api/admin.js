import { api } from './client'

export async function adminListRestaurants() {
  const { data } = await api.get('/restaurants')
  return data
}

export async function adminListReservations(params = {}) {
  // OpenAPI expone /admin/reservations (GET) en tu JSON
  const { data } = await api.get('/admin/reservations', { params })
  return data
}

// reglas / mesas (opcional, ya que existen en tu API)
export async function listRules() {
  const { data } = await api.get('/rule')
  return data
}