import React, { useState, useEffect } from 'react';
import { transactionAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import SearchBar from '../../components/common/SearchBar';
import Modal from '../../components/common/Modal';
import { formatDate, getStatusBadgeClass, getOverdueDays } from '../../utils/helpers';
import { FiEye } from 'react-icons/fi';

const AdminTransactions = () => {
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('');
  
  const [selectedTx, setSelectedTx] = useState(null);

  useEffect(() => {
    fetchTransactions();
  }, [search, statusFilter]);

  const fetchTransactions = async () => {
    setLoading(true);
    try {
      const res = await transactionAPI.getAll({ search, status: statusFilter });
      const body = res.data?.data ?? res.data;
      setTransactions(Array.isArray(body) ? body : body?.transactions || []);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { key: 'id', label: 'ID', render: t => `#${t.id.toString().padStart(4, '0')}` },
    { key: 'user', label: 'User', render: t => t.user?.fullName || 'N/A' },
    { key: 'book', label: 'Book', render: t => t.book?.title || 'N/A' },
    { key: 'issueDate', label: 'Issue Date', render: t => formatDate(t.issueDate) },
    { key: 'dueDate', label: 'Due Date', render: t => formatDate(t.dueDate) },
    { key: 'status', label: 'Status', render: t => {
      const overdueDays = getOverdueDays(t.dueDate);
      const isOverdue = t.status === 'ISSUED' && overdueDays > 0;
      const displayStatus = isOverdue ? 'OVERDUE' : t.status;
      return <span className={`px-2 py-1 rounded text-xs ${getStatusBadgeClass(displayStatus)}`}>{displayStatus}</span>
    }},
    { key: 'actions', label: 'Details', render: t => (
      <button onClick={() => setSelectedTx(t)} className="text-blue-400 hover:text-blue-300"><FiEye size={18} /></button>
    )}
  ];

  return (
    <div className="space-y-4">
      <h2 className="text-2xl font-bold text-white">All Transactions</h2>
      
      <div className="flex gap-4 mb-4">
        <SearchBar value={search} onChange={setSearch} placeholder="Search user or book..." />
        <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white" value={statusFilter} onChange={e => setStatusFilter(e.target.value)}>
          <option value="">All Statuses</option>
          <option value="ISSUED">Issued</option>
          <option value="RETURNED">Returned</option>
          <option value="OVERDUE">Overdue</option>
        </select>
      </div>

      <DataTable columns={columns} data={transactions} loading={loading} />

      {selectedTx && (
        <Modal isOpen={!!selectedTx} onClose={() => setSelectedTx(null)} title="Transaction Details">
          <div className="space-y-3 text-gray-300">
            <p><strong>Transaction ID:</strong> #{selectedTx.id}</p>
            <p><strong>Student:</strong> {selectedTx.user?.fullName} ({selectedTx.user?.email})</p>
            <p><strong>Book:</strong> {selectedTx.book?.title}</p>
            <p><strong>Issue Date:</strong> {formatDate(selectedTx.issueDate)}</p>
            <p><strong>Due Date:</strong> {formatDate(selectedTx.dueDate)}</p>
            {selectedTx.returnDate && <p><strong>Return Date:</strong> {formatDate(selectedTx.returnDate)}</p>}
            <p><strong>Status:</strong> {selectedTx.status}</p>
            <p><strong>Fine Amount:</strong> ₹{selectedTx.fineAmount || 0}</p>
          </div>
        </Modal>
      )}
    </div>
  );
};
export default AdminTransactions;
