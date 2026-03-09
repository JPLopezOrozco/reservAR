import { useEffect, useState } from 'react'
import { adminListRestaurants } from '../api/admin'
import Table from '@mui/material/Table'
import TableHead from '@mui/material/TableHead'
import TableBody from '@mui/material/TableBody'
import TableRow from '@mui/material/TableRow'
import TableCell from '@mui/material/TableCell'
import Typography from '@mui/material/Typography'

export default function AdminRestaurants() {
  const [rows, setRows] = useState([])
  useEffect(() => { adminListRestaurants().then(setRows).catch(console.error) }, [])
  return (
    <>
      <Typography variant="h6" sx={{ mb: 2 }}>Restaurants</Typography>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>Nombre</TableCell>
            <TableCell>Dirección</TableCell>
            <TableCell>Ciudad</TableCell>
            <TableCell>Precio</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {rows.map((r, i) => (
            <TableRow key={i}>
              <TableCell>{r.name}</TableCell>
              <TableCell>{r.address}</TableCell>
              <TableCell>{r.city}</TableCell>
              <TableCell>{r.price}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </>
  )
}