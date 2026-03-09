import { api } from './client'

// GET /availability/restaurant/{id}
export async function getAvailabilityByRestaurant(id) {
  const { data } = await api.get(`/availability/restaurant/${id}`)
  // data: [{ restaurantId, restaurantName, dayOfWeek, start:"HH:mm:ss", end:"HH:mm:ss" }, ...]
  return data
}

// (opcional) carga masiva: array de items como el ejemplo que pasaste
export async function bulkCreateAvailability(items) {
  // si tu POST acepta un array, dejalo así; si es uno por uno, iterá acá
  const { data } = await api.post('/availability', items)
  return data
}