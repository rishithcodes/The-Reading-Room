import React, { useState, useEffect } from 'react';
import { useAuth } from '../../context/AuthContext';
import { dashboardAPI } from '../../services/apiService';
import StatCard from '../../components/common/StatCard';
import DataTable from '../../components/common/DataTable';
import { FiBookOpen, FiAlertTriangle, FiClock, FiCreditCard } from 'react-icons/fi';
import { formatDate, getOverdueDays, formatCurrency } from '../../utils/helpers';
import { Link } from 'react-router-dom';

const StudentDashboard = () => {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchDashboard = async () => {
    setLoading(true); setError(false);
    try { const res = await dashboardAPI.getStudent(); setData(res.data?.data ?? res.data); }
    catch { setError(true); }
    finally { setLoading(false); }
  };
  useEffect(() => { fetchDashboard(); }, []);

  if (loading) return <div className="dashboard-skeleton"><div/><div/><div/><div/></div>;
  if (error || !data) return <div className="role-home"><section className="reader-hero"><div><span className="eyebrow">YOUR LITTLE CORNER</span><h2>Welcome back, {user?.fullName?.split(' ')[0] || 'reader'}.</h2><p>Your next favourite book is waiting somewhere on these shelves.</p></div><div className="staff-hero-mark"><FiBookOpen/></div></section><div className="friendly-state"><span className="friendly-icon"><FiBookOpen/></span><h3>Your reading overview is unavailable</h3><p>We couldn’t load your account details just now. Try again, or browse the catalog while we reconnect.</p><button className="btn btn-primary" onClick={fetchDashboard}>Try again</button></div><Link to="/student/books" className="quick-action"><span className="quick-action-icon forest"><FiBookOpen/></span><span><strong>Browse the catalog</strong><small>Find your next good read</small></span></Link></div>;

  const { stats = {}, currentBorrows = [], activeReservations = [] } = data;

  const borrowColumns = [
    { key: 'book', label: 'Book', render: t => <span className="font-medium">{t.book?.title}</span> },
    { key: 'issueDate', label: 'Issue Date', render: t => formatDate(t.issueDate) },
    { key: 'dueDate', label: 'Due Date', render: t => formatDate(t.dueDate) },
    { key: 'status', label: 'Status', render: t => {
      const days = getOverdueDays(t.dueDate);
      if (days > 0) return <span className="px-2 py-1 rounded text-xs bg-red-500/20 text-red-500">Overdue ({days} days)</span>;
      return <span className="px-2 py-1 rounded text-xs bg-green-500/20 text-green-500">Active</span>;
    }}
  ];

  return (
    <div className="space-y-6">
      <div className="bg-blue-600/10 border border-blue-500/20 rounded-xl p-6">
        <h2 className="text-2xl font-bold text-white">Welcome back, {user?.fullName}!</h2>
        <p className="text-gray-400 mt-1">Here is the overview of your library account.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Books Borrowed" value={stats.activeBorrows || 0} icon={FiBookOpen} color="blue" />
        <StatCard title="Due Soon" value={stats.dueSoon || 0} icon={FiClock} color="yellow" subtitle="Next 3 days" />
        <StatCard title="Overdue" value={stats.overdue || 0} icon={FiAlertTriangle} color="red" />
        <StatCard title="Pending Fines" value={formatCurrency(stats.pendingFines || 0)} icon={FiCreditCard} color="cyan" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-gray-900 rounded-xl border border-gray-800 p-4">
            <h3 className="text-lg font-semibold text-white mb-4">Currently Borrowed Books</h3>
            <DataTable columns={borrowColumns} data={currentBorrows || []} emptyMessage="You have no currently borrowed books." />
          </div>
        </div>
        
        <div className="space-y-6">
          <div className="bg-gray-900 rounded-xl border border-gray-800 p-4">
            <h3 className="text-lg font-semibold text-white mb-4">Quick Links</h3>
            <div className="flex flex-col gap-2">
              <Link to="/student/books" className="px-4 py-3 bg-gray-800 hover:bg-gray-700 text-white rounded-lg transition-colors flex items-center justify-between">Browse Books <FiBookOpen /></Link>
              <Link to="/student/recommendations" className="px-4 py-3 bg-gray-800 hover:bg-gray-700 text-white rounded-lg transition-colors flex items-center justify-between">Recommendations <FiBookOpen /></Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default StudentDashboard;
