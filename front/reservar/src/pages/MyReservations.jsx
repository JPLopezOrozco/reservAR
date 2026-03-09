// src/pages/MyReservations.jsx
import { useEffect, useMemo, useState } from 'react'
import { listReservationsByUser, cancelReservation, completeReservation } from '../api/reservations'
import { getSession, getUsername, isStaffOrAdmin } from '../util/auth'
import Button from '@mui/material/Button'
import TextField from '@mui/material/TextField'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import TableBody from '@mui/material/TableBody'
import Typography from '@mui/material/Typography'
import Alert from '@mui/material/Alert'
import Box from '@mui/material/Box'

export default function MyReservations() {
  const session = useMemo(() => getSession(), [])
  const [userId, setUserId] = useState(isStaffOrAdmin() ? '' : getUsername() || '')
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  async function load(idParam) {
  
    const id = idParam ?? userId

    if (!id) return

    try {
      setError(null)
      setLoading(true)
      const data = await listReservationsByUser(id)
      setRows(Array.isArray(data) ? data : [])
    } catch (e) {
      setError('No se pudieron cargar las reservas')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    if (!isStaffOrAdmin() && getUsername()) load(getUsername())
  }, [])

  const doCancel = async (id) => { await cancelReservation(id); await load() }
  const doComplete = async (id) => { await completeReservation(id); await load() }

  if (!session) {
    return <Alert severity="info">Iniciá sesión para ver tus reservas.</Alert>
  }

  return (
    <>
      <Typography variant="h6" sx={{ mb: 2 }}>Mis reservas</Typography>

      {isStaffOrAdmin() ? (
        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center', mb: 2 }}>
          <TextField
            label="User ID"
            value={userId}
            onChange={e => setUserId(e.target.value)}
            size="small"
          />
          <Button variant="contained" onClick={() => load()}>Buscar</Button>
        </Box>
      ) : (
        <Alert severity="info" sx={{ mb: 2 }}>
          Mostrando reservas del usuario #{getUsername()}
        </Alert>
      )}

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>Restaurant</TableCell>
            <TableCell>Inicio</TableCell>
            <TableCell>Estado</TableCell>
            <TableCell>Acciones</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((r, idx) => {
            const reservationId = r.id || r.reservationId
            return (
              <TableRow key={reservationId ?? idx}>
                <TableCell>{r.restaurant || r.restaurantId}</TableCell>
                <TableCell>{r.start}</TableCell>
                <TableCell>{r.status}</TableCell>
                <TableCell>
                  <Button size="small" onClick={() => doCancel(reservationId)} disabled={loading}>
                    Cancelar
                  </Button>
                  <Button size="small" onClick={() => doComplete(reservationId)} disabled={loading}>
                    Completar
                  </Button>
                </TableCell>
              </TableRow>
            )
          })}
          {rows.length === 0 && !loading && (
            <TableRow><TableCell colSpan={4}>Sin reservas</TableCell></TableRow>
          )}
        </TableBody>
      </Table>
    </>
  )
}