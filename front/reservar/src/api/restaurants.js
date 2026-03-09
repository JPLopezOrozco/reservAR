import { api } from './client'

export async function listRestaurants() {
  const { data } = await api.get('/restaurants')
  return data
}

export async function getRestaurantById(id) {
  const { data } = await api.get(`/restaurants/id/${id}`)
  return data
}