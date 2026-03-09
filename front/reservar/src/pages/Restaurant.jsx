import { useParams } from 'react-router-dom'
import { useEffect, useMemo, useState } from 'react'
import { getRestaurantById } from '../api/restaurants'
import { getAvailabilityByRestaurant } from '../api/availability'
import { createReservation, getPaymentLink } from '../api/reservations'
import { isStaffOrAdmin } from '../util/auth'

import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import Alert from '@mui/material/Alert'
import Chip from '@mui/material/Chip'

import dayjs from 'dayjs'
import { LocalizationProvider } from '@mui/x-date-pickers/LocalizationProvider'
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs'
import { DateTimePicker } from '@mui/x-date-pickers/DateTimePicker'

const DOW_TO_IDX = { SUNDAY:0, MONDAY:1, TUESDAY:2, WEDNESDAY:3, THURSDAY:4, FRIDAY:5, SATURDAY:6 }
const DOW_LABEL = { SUNDAY:'Dom', MONDAY:'Lun', TUESDAY:'Mar', WEDNESDAY:'Mié', THURSDAY:'Jue', FRIDAY:'Vie', SATURDAY:'Sáb' }

export default function Restaurant() {
  const { restaurantId } = useParams()
  const rid = Number(restaurantId)

  const [restaurant, setRestaurant] = useState(null)
  const [availability, setAvailability] = useState([])
  const [msg, setMsg] = useState(null)

  const [userId, setUserId] = useState('')
  const [partySize, setParty] = useState(2)
  const [dt, setDt] = useState(dayjs().add(1, 'day').hour(20).minute(0).second(0).millisecond(0))

  const isPrivileged = isStaffOrAdmin()

  useEffect(() => {
    getRestaurantById(rid).then(setRestaurant).catch(e => setMsg(e.message))
    getAvailabilityByRestaurant(rid).then(setAvailability).catch(e => setMsg(e.message))
  }, [rid])

  // Mapa: dow(0..6) => [{startMin,endMin}]
  const dayMap = useMemo(() => {
    const m = new Map()
    availability.forEach(a => {
      const idx = DOW_TO_IDX[a.dayOfWeek]
      if (idx == null) return
      const toMin = (hhmmss) => {
        const [h=0, mi=0, s=0] = hhmmss.split(':').map(Number)
        return h*60 + mi // segundos no importan para el picker
      }
      const arr = m.get(idx) || []
      arr.push({ start: toMin(a.start), end: toMin(a.end) })
      m.set(idx, arr)
    })
    // normalizar por día (ordenar rangos)
    for (const [k, arr] of m) {
      arr.sort((r1, r2) => r1.start - r2.start)
      m.set(k, arr)
    }
    return m
  }, [availability])

  // Deshabilitar fechas sin disponibilidad
  const shouldDisableDate = (date) => !dayMap.has(date.day())

  // Deshabilitar horas/minutos fuera de los rangos del día elegido
  const withinRanges = (value) => {
    const ranges = dayMap.get(value.day()) || []
    if (!ranges.length) return false
    const mins = value.hour() * 60 + value.minute()
    return ranges.some(r => mins >= r.start && mins <= r.end)
  }
  const shouldDisableTime = (value /* dayjs */, view) => {
    if (!value) return false
    // Para que bloquee minutos también
    return !withinRanges(value)
  }

  async function reserve() {
    try {
      const body = {
        restaurantId: rid,
        userId: isPrivileged && userId ? Number(userId) : undefined,
        startDate: dt.toISOString(),
        partySize,
      }
      const res = await createReservation(body)
      if(res.price===0){
      }else{
        try {
          const link = await getPaymentLink(res.id)
          if (typeof link === 'string' && link.startsWith('http')) {
            window.location.href = link; return
          }
        } catch {}
        setMsg(`Reserva creada. Estado: ${res.status || 'BOOKED'}`)
      }
    } catch (e) { setMsg(e.message) }
  }

  // Agrupar availability por día para mostrar chips prolijos
  const grouped = useMemo(() => {
    const g = {}
    availability.forEach(a => {
      (g[a.dayOfWeek] ||= []).push(`${a.start.slice(0,5)}–${a.end.slice(0,5)}`)
    })
    return g
  }, [availability])

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Grid container spacing={2}>
        {msg && <Grid item xs={12}><Alert severity="info">{msg}</Alert></Grid>}

        <Grid item xs={12}>
          <Typography variant="h5">{restaurant?.name || 'Restaurant'}</Typography>
          {restaurant?.address && <Typography color="text.secondary">{restaurant.address}</Typography>}
        </Grid>

        {isPrivileged && (
          <Grid item xs={12} md={4}>
            <TextField label="User ID" value={userId} onChange={e => setUserId(e.target.value)} fullWidth />
          </Grid>
        )}

        <Grid item xs={6} md={2}>
          <TextField type="number" label="Personas" value={partySize}
            onChange={e => setParty(parseInt(e.target.value || '1'))} fullWidth />
        </Grid>

        <Grid item xs={12} md={6}>
          <DateTimePicker
            label="Inicio"
            value={dt}
            onChange={(v) => v && setDt(v)}
            slotProps={{ textField: { fullWidth: true } }}
            minutesStep={15}
            shouldDisableDate={shouldDisableDate}
            shouldDisableTime={shouldDisableTime}
          />
        </Grid>

        <Grid item xs={12}>
          <Typography variant="subtitle1" sx={{ mb: 1 }}>Disponibilidad</Typography>
          {availability.length === 0
            ? <Typography>No hay availability configurada.</Typography>
            : Object.keys(grouped).sort((a,b)=>DOW_TO_IDX[a]-DOW_TO_IDX[b]).map(dow => (
                <div key={dow} style={{ marginBottom: 8 }}>
                  <Typography variant="body2" sx={{ mb: 0.5 }}>{DOW_LABEL[dow]}:</Typography>
                  {grouped[dow].map((label, i) => <Chip key={i} label={label} size="small" sx={{ mr: 1, mb: 1 }} />)}
                </div>
              ))
          }
        </Grid>

        <Grid item xs={12}>
          <Button variant="contained" onClick={reserve}>Reservar</Button>
        </Grid>
      </Grid>
    </LocalizationProvider>
  )
}