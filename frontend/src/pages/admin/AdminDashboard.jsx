import React, { useEffect, useState } from 'react';
import { dashboardAPI } from '../../services/apiService';
import StatCard from '../../components/common/StatCard';
import DataTable from '../../components/common/DataTable';
import { FiBook, FiUsers, FiAlertCircle, FiClock, FiDollarSign, FiCheckCircle } from 'react-icons/fi';
import { PieChart, Pie, Cell, BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Legend } from 'recharts';
import { formatDate, getStatusBadgeClass } from '../../utils/helpers';
import { Link } from 'react-router-dom';

const AdminDashboard = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(false);

  const fetchData = async () => {
    setLoading(true); setError(false);
    try { const res = await dashboardAPI.getAdmin(); setData(res.data?.data ?? res.data); }
    catch { setError(true); }
    finally { setLoading(false); }
  };
  useEffect(() => { fetchData(); }, []);

  if (loading) return <div className="dashboard-skeleton"><div/><div/><div/><div/></div>;
  if (error || !data) return <div className="role-home"><section className="reader-hero"><div><span className="eyebrow">ADMINISTRATION</span><h2>Your library, at a glance.</h2><p>Manage the collection, support your team, and keep readers moving.</p></div></section><div className="friendly-state"><span className="friendly-icon"><FiBook/></span><h3>Dashboard data is taking a break</h3><p>We couldn’t reach the dashboard service. Try again, or head directly to a library section.</p><button className="btn btn-primary" onClick={fetchData}>Retry dashboard</button></div><div className="quick-action-grid"><Link className="quick-action" to="/admin/books"><span className="quick-action-icon forest"><FiBook/></span><span><strong>Manage books</strong><small>Update the library catalog</small></span></Link><Link className="quick-action" to="/admin/users"><span className="quick-action-icon gold"><FiUsers/></span><span><strong>Manage people</strong><small>Review staff and student accounts</small></span></Link></div></div>;

  const { stats = {}, charts = {}, recentTransactions = [] } = data;
  const categoryDistribution = charts.categoryDistribution || [];
  const monthlyIssuesReturns = charts.monthlyIssuesReturns || [];
  const COLORS = ['#53735e', '#c9a227', '#a4483e', '#557c83', '#87636c'];

  const columns = [
    { key: 'user', label: 'User', render: (t) => t.user?.fullName },
    { key: 'book', label: 'Book', render: (t) => t.book?.title },
    { key: 'issueDate', label: 'Issue Date', render: (t) => formatDate(t.issueDate) },
    { key: 'status', label: 'Status', render: (t) => <span className={`px-2 py-1 rounded text-xs ${getStatusBadgeClass(t.status)}`}>{t.status}</span> }
  ];

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="Total Books" value={stats.totalBooks} icon={FiBook} color="blue" />
        <StatCard title="Total Students" value={stats.totalStudents} icon={FiUsers} color="green" />
        <StatCard title="Books Issued" value={stats.issuedBooks} icon={FiCheckCircle} color="purple" />
        <StatCard title="Overdue Books" value={stats.overdueBooks} icon={FiAlertCircle} color="red" />
        <StatCard title="Active Reservations" value={stats.activeReservations} icon={FiClock} color="yellow" />
        <StatCard title="Pending Fines" value={`₹${stats.pendingFines}`} icon={FiDollarSign} color="cyan" />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h3 className="text-lg font-semibold text-white mb-4">Books by Category</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <PieChart>
                <Pie data={categoryDistribution} cx="50%" cy="50%" innerRadius={60} outerRadius={80} paddingAngle={5} dataKey="value">
                  {categoryDistribution.map((entry, index) => <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />)}
                </Pie>
                <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: '8px', color: '#fff' }} />
                <Legend />
              </PieChart>
            </ResponsiveContainer>
          </div>
        </div>
        <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
          <h3 className="text-lg font-semibold text-white mb-4">Issues vs Returns (Last 6 Months)</h3>
          <div className="h-64">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={monthlyIssuesReturns}>
                <XAxis dataKey="month" stroke="#9ca3af" />
                <YAxis stroke="#9ca3af" />
                <Tooltip contentStyle={{ backgroundColor: '#1f2937', border: 'none', borderRadius: '8px', color: '#fff' }} />
                <Legend />
                <Bar dataKey="issues" fill="#3b82f6" name="Issues" radius={[4, 4, 0, 0]} />
                <Bar dataKey="returns" fill="#10b981" name="Returns" radius={[4, 4, 0, 0]} />
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>
      </div>

      <div className="bg-gray-900 border border-gray-800 rounded-xl p-4">
        <h3 className="text-lg font-semibold text-white mb-4">Recent Transactions</h3>
        <DataTable columns={columns} data={recentTransactions} loading={false} />
      </div>
    </div>
  );
};

export default AdminDashboard;
