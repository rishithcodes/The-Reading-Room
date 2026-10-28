import api from './api'

// ============================================================
// AUTH
// ============================================================
export const authAPI = {
  login:    (credentials)  => api.post('/auth/login', credentials),
  register: (userData)     => api.post('/auth/register', userData),
  me:       ()             => api.get('/auth/me'),
  logout:   ()             => api.post('/auth/logout'),
  health:   ()             => api.get('/auth/health'),
}

// ============================================================
// BOOKS
// ============================================================
export const bookAPI = {
  getAll: (params) => api.get('/books', { params }),
  getById: (id) => api.get(`/books/${id}`),
  create: (data) => api.post('/books', data),
  update: (id, data) => api.put(`/books/${id}`, data),
  delete: (id) => api.delete(`/books/${id}`),
  getQr: (id) => api.get(`/books/${id}/qr`),
}

// ============================================================
// AUTHORS
// ============================================================
export const authorAPI = {
  getAll:  (params) => api.get('/authors', { params }),
  getById: (id)     => api.get(`/authors/${id}`),
  create:  (data)   => api.post('/authors', data),
  update:  (id, data) => api.put(`/authors/${id}`, data),
  delete:  (id)     => api.delete(`/authors/${id}`),
}

// ============================================================
// CATEGORIES
// ============================================================
export const categoryAPI = {
  getAll:  (params) => api.get('/categories', { params }),
  getById: (id)     => api.get(`/categories/${id}`),
  create:  (data)   => api.post('/categories', data),
  update:  (id, data) => api.put(`/categories/${id}`, data),
  delete:  (id)     => api.delete(`/categories/${id}`),
}

// ============================================================
// PUBLISHERS
// ============================================================
export const publisherAPI = {
  getAll:  (params) => api.get('/publishers', { params }),
  getById: (id)     => api.get(`/publishers/${id}`),
  create:  (data)   => api.post('/publishers', data),
  update:  (id, data) => api.put(`/publishers/${id}`, data),
  delete:  (id)     => api.delete(`/publishers/${id}`),
}

// ============================================================
// USERS
// ============================================================
export const userAPI = {
  getAll:         (params)          => api.get('/users', { params }),
  getById:        (id)              => api.get(`/users/${id}`),
  create:         (data)            => api.post('/users', data),
  update:         (id, data)        => api.put(`/users/${id}`, data),
  delete:         (id)              => api.delete(`/users/${id}`),
  changePassword: (id, data)        => api.put(`/users/${id}/password`, data),
  getTransactions:(id, params)      => api.get(`/users/${id}/transactions`, { params }),
  getFines:       (id)              => api.get(`/users/${id}/fines`),
}

// ============================================================
// TRANSACTIONS
// ============================================================
export const transactionAPI = {
  getAll:      (params) => api.get('/transactions', { params }),
  getById:     (id)     => api.get(`/transactions/${id}`),
  issue:       (data)   => api.post('/transactions/issue', data),
  returnBook:  (data)   => api.post('/transactions/return', data),
  getMy:       (params) => api.get('/transactions/my', { params }),
}

// ============================================================
// FINES
// ============================================================
export const fineAPI = {
  getAll:  (params)     => api.get('/fines', { params }),
  getById: (id)         => api.get(`/fines/${id}`),
  pay:     (id, data)   => api.post(`/fines/${id}/pay`, data),
  waive:   (id)         => api.put(`/fines/${id}/waive`),
  getMy:   (params)     => api.get('/fines/my', { params }),
}

// ============================================================
// RESERVATIONS
// ============================================================
export const reservationAPI = {
  getAll:  (params) => api.get('/reservations', { params }),
  create:  (data)   => api.post('/reservations', data),
  cancel:  (id)     => api.delete(`/reservations/${id}`),
  getMy:   (params) => api.get('/reservations/my', { params }),
}

// ============================================================
// NOTIFICATIONS
// ============================================================
export const notificationAPI = {
  getMy:          (params) => api.get('/notifications/my', { params }),
  markRead:       (id)     => api.put(`/notifications/${id}/read`),
  markAllRead:    ()       => api.put('/notifications/read-all'),
  getUnreadCount: ()       => api.get('/notifications/unread-count'),
}

// ============================================================
// DASHBOARD
// ============================================================
export const dashboardAPI = {
  getAdmin:   () => api.get('/dashboard/admin'),
  getStudent: () => api.get('/dashboard/student'),
}

// ============================================================
// RECOMMENDATIONS
// ============================================================
export const recommendationAPI = {
  getMy: () => api.get('/recommendations/my'),
}

// ============================================================
// REPORTS
// ============================================================
export const reportAPI = {
  getBooks:       (params) => api.get('/reports/books', { params }),
  getIssued:      (params) => api.get('/reports/issued', { params }),
  getOverdue:     (params) => api.get('/reports/overdue', { params }),
  getFines:       (params) => api.get('/reports/fines', { params }),
  getTransactions:(params) => api.get('/reports/transactions', { params }),
  getMostBorrowed:(params) => api.get('/reports/most-borrowed', { params }),
  getReservations:(params) => api.get('/reports/reservations', { params }),
  exportCsv:      (type, params) => api.get(`/reports/export/csv`, {
    params: { type, ...params },
    responseType: 'blob'
  }),
}

// ============================================================
// SETTINGS
// ============================================================
export const settingsAPI = {
  getAll:  ()           => api.get('/settings'),
  update:  (key, value) => api.put(`/settings/${key}`, { value }),
}

export const profileAPI = {
  get: () => api.get('/profile'),
  update: (data) => api.put('/profile', data),
  changePassword: (data) => api.put('/profile/password', data),
}
