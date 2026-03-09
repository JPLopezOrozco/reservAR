import { useState } from 'react'
import { registerUser } from '../api/auth'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import Alert from '@mui/material/Alert'
import { useNavigate } from 'react-router-dom'

export default function Register() {
  const nav = useNavigate()
  const [form, setForm] = useState({ email:'', password:'', name:'', surname:'', phone:'' })
  const [msg, setMsg] = useState(null)

  const onChange = (k) => (e) => setForm(s => ({ ...s, [k]: e.target.value }))
  const submit = async (e) => {
    e.preventDefault()
    try {
      await registerUser(form)
      nav('/login')
    } catch (e) { setMsg(e.message) }
  }

  return (
    <form onSubmit={submit}>
      <Typography variant="h6" sx={{ mb: 2 }}>Registro</Typography>
      {msg && <Alert severity="error" sx={{ mb: 2 }}>{msg}</Alert>}
      <TextField label="Nombre" fullWidth sx={{ mb: 2 }} value={form.name} onChange={onChange('name')} />
      <TextField label="Apellido" fullWidth sx={{ mb: 2 }} value={form.surname} onChange={onChange('surname')} />
      <TextField label="Teléfono" fullWidth sx={{ mb: 2 }} value={form.phone} onChange={onChange('phone')} />
      <TextField label="Email" fullWidth sx={{ mb: 2 }} value={form.email} onChange={onChange('email')} />
      <TextField label="Password" type="password" fullWidth sx={{ mb: 2 }} value={form.password} onChange={onChange('password')} />
      <Button type="submit" variant="contained">Crear cuenta</Button>
    </form>
  )
}