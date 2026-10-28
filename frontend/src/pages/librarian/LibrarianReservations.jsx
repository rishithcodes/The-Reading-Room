import React, { useCallback, useEffect, useState } from 'react';
import { FiRefreshCw, FiX } from 'react-icons/fi';
import toast from 'react-hot-toast';
import { reservationAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import { formatDate } from '../../utils/helpers';

const unwrap = response => { const body = response?.data?.data ?? response?.data ?? {}; return Array.isArray(body) ? body : body.reservations || []; };
export default function LibrarianReservations() {
  const [items, setItems] = useState([]), [loading, setLoading] = useState(true), [selected, setSelected] = useState(null), [status, setStatus] = useState('ALL');
  const load = useCallback(async () => { setLoading(true); try { const response = await reservationAPI.getAll(); setItems(unwrap(response)); } catch (e) { toast.error(e.response?.data?.message || 'Could not load reservations'); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const cancel = async () => { if (!selected) return; try { await reservationAPI.cancel(selected.id); toast.success('Reservation cancelled'); setSelected(null); await load(); } catch (e) { toast.error(e.response?.data?.message || 'Could not cancel reservation'); } };
  const visible = items.filter(item => status === 'ALL' || item.status === status);
  const columns = [
    { key: 'reader', label: 'Reader', render: r => <span className="table-primary">{r.user?.fullName || '—'}<small className="table-secondary">{r.user?.email || ''}</small></span> },
    { key: 'book', label: 'Book', render: r => r.book?.title || '—' },
    { key: 'createdAt', label: 'Requested', render: r => formatDate(r.createdAt) },
    { key: 'expiresAt', label: 'Hold expires', render: r => formatDate(r.expiresAt) },
    { key: 'status', label: 'Status', render: r => <span className={`status-pill ${r.status === 'ACTIVE' ? 'pending' : r.status === 'FULFILLED' ? 'on-time' : 'overdue'}`}>{r.status}</span> },
    { key: 'action', label: 'Action', render: r => r.status === 'ACTIVE' ? <button className="icon-action" title="Cancel reservation" aria-label="Cancel reservation" onClick={() => setSelected(r)}><FiX /></button> : '—' },
  ];
  return <section className="reference-page">
    <header className="reference-header"><div><span className="eyebrow">READER QUEUE</span><h2>Reservations</h2><p>Track reader holds and cancel a request when needed.</p></div><div className="fine-summary"><strong>{items.filter(r => r.status === 'ACTIVE').length}</strong><small>active holds</small></div></header>
    <div className="reference-toolbar"><label className="report-filter">Show<select value={status} onChange={e => setStatus(e.target.value)}><option value="ALL">All reservations</option><option value="ACTIVE">Active</option><option value="FULFILLED">Fulfilled</option><option value="CANCELLED">Cancelled</option></select></label><button className="report-refresh" onClick={load} disabled={loading}><FiRefreshCw />{loading ? 'Loading…' : 'Refresh'}</button><span>{visible.length} records</span></div>
    <DataTable columns={columns} data={visible} loading={loading} emptyMessage="There are no reservations in this view." />
    <ConfirmDialog isOpen={!!selected} onClose={() => setSelected(null)} onConfirm={cancel} title="Cancel reservation" message={`Cancel ${selected?.user?.fullName || 'this reader'}’s hold on “${selected?.book?.title || 'this book'}”?`} />
  </section>;
}
