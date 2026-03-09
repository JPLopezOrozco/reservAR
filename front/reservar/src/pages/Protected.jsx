
import { Navigate, Outlet } from 'react-router-dom'
import { isLoggedIn } from '../util/auth'

export default function Protected() {
  return isLoggedIn() ? <Outlet/> : <Navigate to="/login" replace />
}