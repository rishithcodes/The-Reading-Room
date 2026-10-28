import React, { useState } from 'react';
import { transactionAPI } from '../../services/apiService';
import SearchBar from '../../components/common/SearchBar';
import DataTable from '../../components/common/DataTable';
import toast from 'react-hot-toast';
import { formatDate, getOverdueDays, formatCurrency } from '../../utils/helpers';
import { FiRefreshCw } from 'react-icons/fi';

const LibrarianReturn = () => {
  const [search, setSearch] = useState('');
  const [transactions, setTransactions] = useState([]);
  const [loading, setLoading] = useState(false);

  const handleSearch = async () => {
    if (!search.trim()) return;
    setLoading(true);
    try {
      const res = await transactionAPI.getAll({ search, status: 'ISSUED,OVERDUE', pageSize: 10 });
      const body = res.data?.data ?? res.data ?? {};
      setTransactions(Array.isArray(body) ? body : (body.transactions || body.items || []));
    } catch (e) {
      toast.error('Failed to search transactions');
    } finally {
      setLoading(false);
    }
  };

  const handleReturn = async (id) => {
    try {
      setLoading(true);
      const res = await transactionAPI.returnBook({ transactionId: id });
      toast.success(res.data?.message || res.data?.data?.message || 'Book returned successfully!');
      handleSearch(); // Refresh list
    } catch (e) {
      toast.error(e.response?.data?.message || 'Failed to return book');
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { key: 'user', label: 'Student', render: t => t.user?.fullName || 'N/A' },
    { key: 'book', label: 'Book Title', render: t => t.book?.title || 'N/A' },
    { key: 'issueDate', label: 'Issue Date', render: t => formatDate(t.issueDate) },
    { key: 'dueDate', label: 'Due Date', render: t => formatDate(t.dueDate) },
    { key: 'status', label: 'Status', render: t => {
      const overdue = getOverdueDays(t.dueDate) > 0;
      return <span className={`px-2 py-1 rounded text-xs ${overdue ? 'bg-red-500/20 text-red-500' : 'bg-yellow-500/20 text-yellow-500'}`}>{overdue ? 'OVERDUE' : 'ISSUED'}</span>
    }},
    { key: 'fine', label: 'Fine Preview', render: t => {
      const overdue = getOverdueDays(t.dueDate);
      return formatCurrency(t.finePreview ?? 0);
    }},
    { key: 'actions', label: 'Action', render: t => (
      <button onClick={() => handleReturn(t.id)} className="px-3 py-1 bg-green-600 hover:bg-green-700 text-white rounded flex items-center gap-1 text-sm">
        <FiRefreshCw size={14} /> Return
      </button>
    )}
  ];

  return (
    <div className="space-y-6">
      <h2 className="text-2xl font-bold text-white">Return Book</h2>
      
      <div className="bg-gray-900 p-6 rounded-xl border border-gray-800">
        <div className="flex gap-4 max-w-2xl">
          <SearchBar value={search} onChange={setSearch} placeholder="Search by student name, email, or book title..." onSearch={handleSearch} />
          <button onClick={handleSearch} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">Search</button>
        </div>
      </div>

      {transactions.length > 0 ? (
        <div className="bg-gray-900 p-4 rounded-xl border border-gray-800">
          <h3 className="text-lg font-semibold text-white mb-4">Active Transactions Found</h3>
          <DataTable columns={columns} data={transactions} loading={loading} />
        </div>
      ) : search && !loading && (
        <div className="p-8 text-center text-gray-400 bg-gray-900 rounded-xl border border-gray-800">
          No active transactions found matching your search.
        </div>
      )}
    </div>
  );
};
export default LibrarianReturn;
