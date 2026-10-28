import React, { createContext, useContext, useState, useCallback, useEffect } from 'react'
import { authAPI } from '../services/apiService'
import toast from 'react-hot-toast'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser]       = useState(() => {
    try { return JSON.parse(localStorage.getItem('user')) } catch { return null }
  })
  const [token, setToken]     = useState(() => localStorage.getItem('token'))
  const [loading, setLoading] = useState(false)
  const [initialized, setInitialized] = useState(false)

  // On mount, verify token is still valid
  useEffect(() => {
    const verifyToken = async () => {
      const storedToken = localStorage.getItem('token')
      if (!storedToken) { setInitialized(true); return }
      try {
        const { data } = await authAPI.me()
        if (data.success) {
          setUser(data.data)
          localStorage.setItem('user', JSON.stringify(data.data))
        }
      } catch {
        // Token invalid — clear storage
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        setUser(null)
        setToken(null)
      } finally {
        setInitialized(true)
      }
    }
    verifyToken()
  }, [])

  const login = useCallback(async (email, password) => {
    setLoading(true)
    try {
      const { data } = await authAPI.login({ email, password })
      if (data.success) {
        const { token: jwt, user: userData } = data.data
        localStorage.setItem('token', jwt)
        localStorage.setItem('user', JSON.stringify(userData))
        setToken(jwt)
        setUser(userData)
        toast.success(`Welcome back, ${userData.fullName}!`)
      return userData
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.response?.data?.error || 'Login failed. Please try again.'
      toast.error(msg)
      return { success: false, error: msg }
    } finally {
      setLoading(false)
    }
    return { success: false }
  }, [])

  const register = useCallback(async (userData) => {
    setLoading(true)
    try {
      const { data } = await authAPI.register(userData)
      if (data.success) {
        toast.success('Account created! Please login.')
        return { success: true }
      }
    } catch (error) {
      const msg = error.response?.data?.message || error.response?.data?.error || 'Registration failed.'
      toast.error(msg)
      return { success: false, error: msg }
    } finally {
      setLoading(false)
    }
    return { success: false }
  }, [])

  const logout = useCallback(async () => {
    try { await authAPI.logout() } catch { /* ignore */ }
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
    setToken(null)
    toast.success('Logged out successfully.')
  }, [])

  const updateUserInContext = useCallback((updatedUser) => {
    setUser(updatedUser)
    localStorage.setItem('user', JSON.stringify(updatedUser))
  }, [])

  const isAdmin     = user?.role === 'ADMIN'
  const isLibrarian = user?.role === 'LIBRARIAN'
  const isStudent   = user?.role === 'STUDENT'
  const isStaff     = isAdmin || isLibrarian

  return (
    <AuthContext.Provider value={{
      user, token, loading, initialized,
      login, register, logout, updateUserInContext,
      isAdmin, isLibrarian, isStudent, isStaff,
    }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
