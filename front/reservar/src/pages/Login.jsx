import { useState } from 'react'
import { login } from '../api/auth'
import TextField from '@mui/material/TextField'
import Button from '@mui/material/Button'
import Typography from '@mui/material/Typography'
import Alert from '@mui/material/Alert'
import { useNavigate } from 'react-router-dom'

export default function Login() {
  const nav = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [msg, setMsg] = useState(null)

  const submit = async (e) => {
    e.preventDefault()
    try {
      await login({ email, password })
      nav('/')
    } catch (e) { setMsg(e.message) }
  }

  return (
    <form onSubmit={submit}>
      <Typography variant="h6" sx={{ mb: 2 }}>Ingresar</Typography>
      {msg && <Alert severity="error" sx={{ mb: 2 }}>{msg}</Alert>}
      <TextField label="Email" fullWidth sx={{ mb: 2 }} value={email} onChange={e => setEmail(e.target.value)} />
      <TextField label="Password" type="password" fullWidth sx={{ mb: 2 }} value={password} onChange={e => setPassword(e.target.value)} />
      <Button type="submit" variant="contained">Entrar</Button>
    </form>
  )
}