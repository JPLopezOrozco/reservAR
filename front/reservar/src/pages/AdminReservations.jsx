import { useEffect, useState } from 'react'
import { adminListReservations } from '../api/admin'
import dayjs from 'dayjs'
import Grid from '@mui/material/Grid'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableBody from '@mui/material/TableBody'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import Typography from '@mui/material/Typography'

export default function AdminReservations() {
  const [date, setDate] = useState(dayjs().format('YYYY-MM-DD'))
  const [restaurantId, setRestaurantId] = useState('')
  const [rows, setRows] = useState([])

  const load = async () => setRows(await adminListReservations({ date, restaurantId }))

  useEffect(() => { load() }, []) // primera carga

  return (
    <>
      <Typography variant="h6" sx={{ mb: 2 }}>Reservas</Typography>
      <Grid container spacing={2} sx={{ mb: 2 }}>
        <Grid item xs={12} md={3}>
          <TextField type="date" label="Fecha" value={date}
            onChange={e => setDate(e.target.value)} fullWidth InputLabelProps={{ shrink: true }} />
        </Grid>
        <Grid item xs={12} md={3}>
          <TextField label="Restaurant ID" value={restaurantId}
            onChange={e => setRestaurantId(e.target.value)} fullWidth />
        </Grid>
        <Grid item xs={12} md={3}>
          <Button variant="contained" sx={{ mt: 1.2 }} onClick={load}>Buscar</Button>
        </Grid>
      </Grid>

      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Restaurant</TableCell>
            <TableCell>Inicio</TableCell>
            <TableCell>Estado</TableCell>
            <TableCell>Mesas</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((r, i) => (
            <TableRow key={i}>
              <TableCell>{r.restaurant || r.restaurantId}</TableCell>
              <TableCell>{r.start}</TableCell>
              <TableCell>{r.status}</TableCell>
              <TableCell>{Array.isArray(r.tables) ? r.tables.length : 0}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </>
  )
}