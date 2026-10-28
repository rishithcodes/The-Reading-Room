import React from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import LoadingScreen from '../components/common/LoadingScreen'

/**
 * Protects routes by checking authentication and role.
 * Redirects to /login if not authenticated.
 * Redirects to role's default page if role doesn't match.
 */
export default function ProtectedRoute({ allowedRoles }) {
  const { user, initialized } = useAuth()

  if (!initialized) return <LoadingScreen />
  if (!user) return <Navigate to="/login" replace />

  if (allowedRoles && !allowedRoles.includes(user.role)) {
    // Redirect to correct role's default page
    if (user.role === 'ADMIN')     return <Navigate to="/admin/dashboard" replace />
    if (user.role === 'LIBRARIAN') return <Navigate to="/librarian/dashboard" replace />
    return <Navigate to="/student/dashboard" replace />
  }

  return <Outlet />
}
