import { Routes, Route, Navigate } from 'react-router-dom'
import Container from '@mui/material/Container'
import NavBar from './components/NavBar.jsx'
import Home from './pages/Home.jsx'
import Restaurant from './pages/Restaurant.jsx'
import MyReservations from './pages/MyReservations.jsx'
import CheckoutResult from './pages/CheckoutResult.jsx'
import AdminDashboard from './pages/AdminDashboard.jsx'
import AdminRestaurants from './pages/AdminRestaurants.jsx'
import AdminReservations from './pages/AdminReservations.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import NotFound from './pages/NotFound.jsx'

export default function App() {
  return (
    <>
      <NavBar />
      <Container sx={{ py: 3 }}>
        <Routes>
          {/* Público / Usuario */}
          <Route path="/" element={<Home />} />
          <Route path="/restaurant/:restaurantId" element={<Restaurant />} />
          <Route path="/my-reservations" element={<MyReservations />} />
          <Route path="/checkout" element={<CheckoutResult />} />

          {/* Auth */}
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />

          {/* Staff/Admin (sin guard por ahora) */}
          <Route path="/admin" element={<AdminDashboard />}>
            <Route index element={<Navigate to="restaurants" replace />} />
            <Route path="restaurants" element={<AdminRestaurants />} />
            <Route path="reservations" element={<AdminReservations />} />
          </Route>

          <Route path="*" element={<NotFound />} />
        </Routes>
      </Container>
    </>
  )
}