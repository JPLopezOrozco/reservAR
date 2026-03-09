import { useEffect, useState } from 'react'
import { listRestaurants } from '../api/restaurants'
import Grid from '@mui/material/Grid'
import Card from '@mui/material/Card'
import CardContent from '@mui/material/CardContent'
import Typography from '@mui/material/Typography'
import Button from '@mui/material/Button'
import { Link as RouterLink } from 'react-router-dom'
import ErrorBanner from '../components/ErrorBanner'

export default function Home() {
  const [rows, setRows] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    listRestaurants()
      .then(setRows)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false))
  }, [])

  if (loading) return <Typography>Cargando...</Typography>

  return (
    <>
      <ErrorBanner msg={error} />
      <Grid container spacing={2}>
        {rows.map(r => (
          <Grid item xs={12} md={6} lg={4} key={r.name + r.address}>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="h6">{r.name}</Typography>
                {r.address && <Typography color="text.secondary">{r.address}</Typography>}
                <Button sx={{ mt: 1 }} component={RouterLink} to={`/restaurant/${r.id}`}>
                  Ver disponibilidad
                </Button>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>
    </>
  )
}