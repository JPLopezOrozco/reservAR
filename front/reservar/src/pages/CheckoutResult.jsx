import { useEffect, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import Typography from '@mui/material/Typography'

export default function CheckoutResult() {
  const [params] = useSearchParams()
  const [text, setText] = useState('Procesando...')

  useEffect(() => {
    const status = params.get('status')
    const reservationId = params.get('reservationId')
    if (status === 'CONFIRMED') setText(`Reserva confirmada (#${reservationId})`)
    else if (status === 'PENDING' || status === 'HOLD') setText('Pago pendiente.')
    else if (status === 'FAILED' || status === 'CANCELED') setText('Pago cancelado o fallido.')
  }, [params])

  return <Typography variant="h6">{text}</Typography>
}