import { Outlet, Link as RouterLink, useLocation } from 'react-router-dom'
import Tabs from '@mui/material/Tabs'
import Tab from '@mui/material/Tab'
import Box from '@mui/material/Box'
import Typography from '@mui/material/Typography'

export default function AdminDashboard() {
  const loc = useLocation()
  const value = loc.pathname.includes('/admin/reservations') ? 1 : 0
  return (
    <Box>
      <Typography variant="h5" sx={{ mb: 2 }}>Panel Staff/Admin</Typography>
      <Tabs value={value} sx={{ mb: 2 }}>
        <Tab label="Restaurants" component={RouterLink} to="/admin/restaurants" />
        <Tab label="Reservas" component={RouterLink} to="/admin/reservations" />
      </Tabs>
      <Outlet />
    </Box>
  )
}