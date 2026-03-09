import AppBar from '@mui/material/AppBar'
import Toolbar from '@mui/material/Toolbar'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import { Link as RouterLink, useNavigate } from 'react-router-dom'
import { logout } from '../api/auth'

export default function NavBar() {
  const nav = useNavigate()
  const token = localStorage.getItem('token')

  const doLogout = () => {
    logout()
    nav('/login')
  }

  return (
    <AppBar position="sticky">
      <Toolbar>
        <Typography variant="h6" component={RouterLink} to="/"
          sx={{ flexGrow: 1, color: 'inherit', textDecoration: 'none' }}>
          ReservAR
        </Typography>
        <Button color="inherit" component={RouterLink} to="/">Home</Button>
        <Button color="inherit" component={RouterLink} to="/my-reservations">Mis reservas</Button>
        <Button color="inherit" component={RouterLink} to="/admin">Staff/Admin</Button>
        {token ? (
          <Button color="inherit" onClick={doLogout}>Salir</Button>
        ) : (
          <>
            <Button color="inherit" component={RouterLink} to="/login">Ingresar</Button>
            <Button color="inherit" component={RouterLink} to="/register">Registro</Button>
          </>
        )}
      </Toolbar>
    </AppBar>
  )
}