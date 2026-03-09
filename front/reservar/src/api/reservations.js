import { api } from './client'

// Crear reserva
export async function createReservation(body) {
  // {restaurantId,userId,startDate(ISO),partySize}
  try{
    const { data } = await api.post('/reservation', body, {
      headers: { 'Idempotency-Key': crypto.randomUUID() },
    })
    return data // ReservationResponseDto
  }catch(e){
    console.log(e)
  }
}

// Ver reservas de un usuario
export async function listReservationsByUser(userId) {
  const { data } = await api.get(`/reservation/user?username=${userId}`)
  return data
}

// Acciones
export async function cancelReservation(id) {
  console.log(id)
  const { data } = await api.put(`/reservation/cancel/${id}`)
  return data
}

export async function completeReservation(id) {
  const { data } = await api.put(`/reservation/completedReservation/${id}`)
  return data
}

// (futuro) pago/mercado
export async function getPaymentLink(id) {
  try{
    const  data  = await api.get(`/payment/mercado-pago/${id}`,{
      responseType: 'text'
    })
    const link = data.data?.trim()
    return link || null
  }catch(e){
    console.log(e)
  }
}