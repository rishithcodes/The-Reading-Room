import React, { Suspense, lazy } from 'react'
import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import ProtectedRoute from './routes/ProtectedRoute'
import AppLayout from './layouts/AppLayout'
import LoadingScreen from './components/common/LoadingScreen'

// Auth Pages (eager-loaded)
import Login from './pages/Login'
import Register from './pages/Register'

// Lazy load all dashboard pages
const AdminDashboard        = lazy(() => import('./pages/admin/AdminDashboard'))
const AdminBooks            = lazy(() => import('./pages/admin/AdminBooks'))
const AdminUsers            = lazy(() => import('./pages/admin/AdminUsers'))
const AdminCategories       = lazy(() => import('./pages/admin/AdminCategories'))
const AdminAuthors          = lazy(() => import('./pages/admin/AdminAuthors'))
const AdminPublishers       = lazy(() => import('./pages/admin/AdminPublishers'))
const AdminTransactions     = lazy(() => import('./pages/admin/AdminTransactions'))
const AdminFines            = lazy(() => import('./pages/admin/AdminFines'))
const AdminReservations     = lazy(() => import('./pages/admin/AdminReservations'))
const AdminReports          = lazy(() => import('./pages/admin/AdminReports'))
const AdminSettings         = lazy(() => import('./pages/admin/AdminSettings'))

const LibrarianDashboard    = lazy(() => import('./pages/librarian/LibrarianDashboard'))
const LibrarianBooks        = lazy(() => import('./pages/librarian/LibrarianBooks'))
const LibrarianIssue        = lazy(() => import('./pages/librarian/LibrarianIssue'))
const LibrarianReturn       = lazy(() => import('./pages/librarian/LibrarianReturn'))
const LibrarianTransactions = lazy(() => import('./pages/librarian/LibrarianTransactions'))
const LibrarianReservations = lazy(() => import('./pages/librarian/LibrarianReservations'))
const LibrarianFines        = lazy(() => import('./pages/librarian/LibrarianFines'))

const StudentDashboard      = lazy(() => import('./pages/student/StudentDashboard'))
const StudentBooks          = lazy(() => import('./pages/student/StudentBooks'))
const StudentMyBooks        = lazy(() => import('./pages/student/StudentMyBooks'))
const StudentReservations   = lazy(() => import('./pages/student/StudentReservations'))
const StudentFines          = lazy(() => import('./pages/student/StudentFines'))
const StudentHistory        = lazy(() => import('./pages/student/StudentHistory'))
const StudentNotifications  = lazy(() => import('./pages/student/StudentNotifications'))
const StudentRecommendations= lazy(() => import('./pages/student/StudentRecommendations'))
const StudentProfile        = lazy(() => import('./pages/student/StudentProfile'))

function RoleRedirect() {
  const { user } = useAuth()
  if (!user) return <Navigate to="/login" replace />
  if (user.role === 'ADMIN')      return <Navigate to="/admin/dashboard" replace />
  if (user.role === 'LIBRARIAN')  return <Navigate to="/librarian/dashboard" replace />
  return <Navigate to="/student/dashboard" replace />
}

export default function App() {
  const { initialized } = useAuth()
  if (!initialized) return <LoadingScreen />

  return (
    <Suspense fallback={<LoadingScreen />}>
      <Routes>
        {/* Public Routes */}
        <Route path="/login"    element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/"         element={<RoleRedirect />} />

        {/* ADMIN Routes */}
        <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
          <Route element={<AppLayout />}>
            <Route path="/admin/dashboard"    element={<AdminDashboard />} />
            <Route path="/admin/books"        element={<AdminBooks />} />
            <Route path="/admin/users"        element={<AdminUsers />} />
            <Route path="/admin/categories"   element={<AdminCategories />} />
            <Route path="/admin/authors"      element={<AdminAuthors />} />
            <Route path="/admin/publishers"   element={<AdminPublishers />} />
            <Route path="/admin/transactions" element={<AdminTransactions />} />
            <Route path="/admin/fines"        element={<AdminFines />} />
            <Route path="/admin/reservations" element={<AdminReservations />} />
            <Route path="/admin/reports"      element={<AdminReports />} />
            <Route path="/admin/settings"     element={<AdminSettings />} />
          </Route>
        </Route>

        {/* LIBRARIAN Routes */}
        <Route element={<ProtectedRoute allowedRoles={['LIBRARIAN']} />}>
          <Route element={<AppLayout />}>
            <Route path="/librarian/dashboard"    element={<LibrarianDashboard />} />
            <Route path="/librarian/books"        element={<LibrarianBooks />} />
            <Route path="/librarian/issue"        element={<LibrarianIssue />} />
            <Route path="/librarian/return"       element={<LibrarianReturn />} />
            <Route path="/librarian/transactions" element={<LibrarianTransactions />} />
            <Route path="/librarian/reservations" element={<LibrarianReservations />} />
            <Route path="/librarian/fines"        element={<LibrarianFines />} />
          </Route>
        </Route>

        {/* STUDENT Routes */}
        <Route element={<ProtectedRoute allowedRoles={['STUDENT']} />}>
          <Route element={<AppLayout />}>
            <Route path="/student/dashboard"        element={<StudentDashboard />} />
            <Route path="/student/books"            element={<StudentBooks />} />
            <Route path="/student/my-books"         element={<StudentMyBooks />} />
            <Route path="/student/reservations"     element={<StudentReservations />} />
            <Route path="/student/fines"            element={<StudentFines />} />
            <Route path="/student/history"          element={<StudentHistory />} />
            <Route path="/student/notifications"    element={<StudentNotifications />} />
            <Route path="/student/recommendations"  element={<StudentRecommendations />} />
            <Route path="/student/profile"          element={<StudentProfile />} />
          </Route>
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Suspense>
  )
}
