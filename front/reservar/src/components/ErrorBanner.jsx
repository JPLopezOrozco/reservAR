import Alert from '@mui/material/Alert'
export default function ErrorBanner({ msg }) {
  if (!msg) return null
  return <Alert severity="error" sx={{ mb: 2 }}>{msg}</Alert>
}