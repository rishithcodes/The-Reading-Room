import React, { useCallback, useEffect, useState } from 'react';
import { FiRefreshCw } from 'react-icons/fi';
import toast from 'react-hot-toast';
import { transactionAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import { formatDate } from '../../utils/helpers';

const statuses = ['ALL', 'ISSUED', 'OVERDUE', 'RETURNED'];
const unwrap = response => { const body = response?.data?.data ?? response?.data ?? {}; return Array.isArray(body) ? body : body.transactions || body.items || []; };

export default function LibrarianTransactions() {
  const [items, setItems] = useState([]), [search, setSearch] = useState(''), [status, setStatus] = useState('ALL'), [loading, setLoading] = useState(true);
  const load = useCallback(async () => { setLoading(true); try {
    const params = { pageSize: 100 }; if (search.trim()) params.search = search.trim(); if (status !== 'ALL') params.status = status;
    const response = await transactionAPI.getAll(params); setItems(unwrap(response));
  } catch (e) { toast.error(e.response?.data?.message || 'Could not load circulation records'); }
  finally { setLoading(false); } }, [search, status]);
  useEffect(() => { const timer = setTimeout(load, 200); return () => clearTimeout(timer); }, [load]);
  const columns = [
    { key: 'student', label: 'Student', render: t => <span className="table-primary">{t.user?.fullName || '—'}<small className="table-secondary">{t.user?.email || ''}</small></span> },
    { key: 'book', label: 'Book', render: t => t.book?.title || '—' },
    { key: 'issueDate', label: 'Issued', render: t => formatDate(t.issueDate) },
    { key: 'dueDate', label: 'Due', render: t => formatDate(t.dueDate) },
    { key: 'returnDate', label: 'Returned', render: t => t.returnDate ? formatDate(t.returnDate) : '—' },
    { key: 'status', label: 'Status', render: t => <span className={`status-pill ${t.status === 'RETURNED' ? 'on-time' : t.status === 'OVERDUE' ? 'overdue' : 'pending'}`}>{t.status}</span> },
  ];
  return <section className="reference-page">
    <header className="reference-header"><div><span className="eyebrow">LOANS & RETURNS</span><h2>Circulation</h2><p>Review current and past loans across the reading room.</p></div><div className="fine-summary"><strong>{items.length}</strong><small>matching records</small></div></header>
    <div className="reference-toolbar"><label className="reference-search">Search records<input value={search} onChange={e => setSearch(e.target.value)} placeholder="Student, email, book title, or ISBN" /></label><label className="report-filter">Status<select value={status} onChange={e => setStatus(e.target.value)}>{statuses.map(value => <option key={value} value={value}>{value === 'ALL' ? 'All statuses' : value.charAt(0) + value.slice(1).toLowerCase()}</option>)}</select></label><button className="report-refresh" onClick={load} disabled={loading}><FiRefreshCw />{loading ? 'Loading…' : 'Refresh'}</button></div>
    <DataTable columns={columns} data={items} loading={loading} emptyMessage="No circulation records match these filters." />
  </section>;
}
