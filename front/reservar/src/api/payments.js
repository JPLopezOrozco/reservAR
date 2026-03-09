import { authFetch } from './authFetch'

export async function startPayment(reservationId) {
  const res = await authFetch(`/payment/mercado-pago/${reservationId}`, {
    method: 'GET'
  })

  if (!res.ok) {
    throw new Error('No se pudo iniciar el pago')
  }

  const txt = await res.text()

  return txt?.trim() ? txt.trim() : null
}