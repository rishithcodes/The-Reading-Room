import React, { useCallback, useEffect, useState } from 'react';
import { FiCheck, FiCreditCard, FiRefreshCw } from 'react-icons/fi';
import toast from 'react-hot-toast';
import { fineAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import { formatCurrency, formatDate } from '../../utils/helpers';

const unwrap = response => { const body = response?.data?.data ?? response?.data ?? {}; return Array.isArray(body) ? body : body.fines || []; };
export default function LibrarianFines() {
  const [items, setItems] = useState([]), [loading, setLoading] = useState(true), [paying, setPaying] = useState(null), [filter, setFilter] = useState('ALL');
  const load = useCallback(async () => { setLoading(true); try { const response = await fineAPI.getAll(); setItems(unwrap(response)); } catch (e) { toast.error(e.response?.data?.message || 'Could not load fines'); } finally { setLoading(false); } }, []);
  useEffect(() => { load(); }, [load]);
  const recordPayment = async fine => { setPaying(fine.id); try { await fineAPI.pay(fine.id, {}); toast.success('Payment recorded'); await load(); } catch (e) { toast.error(e.response?.data?.message || 'Could not record payment'); } finally { setPaying(null); } };
  const outstanding = items.filter(f => f.status !== 'PAID').reduce((total, f) => total + Math.max(0, Number(f.amount || 0) - Number(f.paidAmount || 0)), 0);
  const visible = items.filter(f => filter === 'ALL' || (filter === 'PENDING' ? f.status !== 'PAID' : f.status === 'PAID'));
  const columns = [
    { key: 'reader', label: 'Reader', render: f => <span className="table-primary">{f.transaction?.user?.fullName || '—'}<small className="table-secondary">{f.transaction?.user?.email || ''}</small></span> },
    { key: 'book', label: 'Book', render: f => f.transaction?.book?.title || '—' },
    { key: 'dueDate', label: 'Due date', render: f => formatDate(f.transaction?.dueDate) },
    { key: 'overdueDays', label: 'Days late', render: f => f.overdueDays ?? '—' },
    { key: 'amount', label: 'Fine', render: f => formatCurrency(f.amount) },
    { key: 'paidAmount', label: 'Paid', render: f => formatCurrency(f.paidAmount) },
    { key: 'balance', label: 'Balance', render: f => formatCurrency(Math.max(0, Number(f.amount || 0) - Number(f.paidAmount || 0))) },
    { key: 'status', label: 'Status', render: f => <span className={`status-pill ${f.status === 'PAID' ? 'on-time' : 'overdue'}`}>{f.status}</span> },
    { key: 'action', label: 'Action', render: f => f.status === 'PAID' ? 'Complete' : <button className="text-link fine-pay" disabled={paying === f.id} onClick={() => recordPayment(f)}><FiCheck />{paying === f.id ? 'Saving…' : 'Record payment'}</button> },
  ];
  return <section className="reference-page">
    <header className="reference-header"><div><span className="eyebrow">PAYMENTS</span><h2>Fines</h2><p>Review overdue charges and record payments received at the desk.</p></div><div className="fine-summary"><FiCreditCard /><span><small>Outstanding balance</small><strong>{formatCurrency(outstanding)}</strong></span></div></header>
    <div className="reference-toolbar"><label className="report-filter">Show<select value={filter} onChange={e => setFilter(e.target.value)}><option value="ALL">All fines</option><option value="PENDING">Outstanding</option><option value="PAID">Paid</option></select></label><button className="report-refresh" onClick={load} disabled={loading}><FiRefreshCw />{loading ? 'Loading…' : 'Refresh'}</button><span>{visible.length} records</span></div>
    <DataTable columns={columns} data={visible} loading={loading} emptyMessage="No fines are recorded for this view." />
  </section>;
}
