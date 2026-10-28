export const formatDate = (dateStr) => {
  if (!dateStr) return 'N/A';
  const date = new Date(dateStr);
  return date.toLocaleDateString('en-GB', { day: '2-digit', month: 'short', year: 'numeric' });
};

export const formatDateTime = (dateStr) => {
  if (!dateStr) return 'N/A';
  const date = new Date(dateStr);
  return date.toLocaleString('en-GB', { day: '2-digit', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit' });
};

export const formatCurrency = (amount) => {
  if (amount === undefined || amount === null) return 'N/A';
  return `₹${parseFloat(amount).toFixed(2)}`;
};

export const getStatusBadgeClass = (status) => {
  switch (status?.toUpperCase()) {
    case 'ACTIVE':
    case 'RETURNED':
    case 'PAID':
    case 'AVAILABLE':
      return 'badge-success';
    case 'OVERDUE':
    case 'UNAVAILABLE':
    case 'UNPAID':
    case 'CANCELLED':
      return 'badge-danger';
    case 'PENDING':
    case 'DUE_SOON':
      return 'badge-warning';
    default:
      return 'badge-secondary';
  }
};

export const getOverdueDays = (dueDate) => {
  if (!dueDate) return 0;
  const due = new Date(dueDate);
  const now = new Date();
  const diffTime = Math.max(0, now - due);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
};

export const truncateText = (text, maxLen = 50) => {
  if (!text) return '';
  return text.length > maxLen ? text.substring(0, maxLen) + '...' : text;
};

export const downloadBlob = (blob, filename) => {
  const url = window.URL.createObjectURL(new Blob([blob]));
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', filename);
  document.body.appendChild(link);
  link.click();
  link.parentNode.removeChild(link);
};
