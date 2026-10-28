import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { dashboardAPI } from '../../services/apiService';
import { FiBook, FiCheckCircle, FiRotateCcw, FiClock, FiArrowRight, FiRefreshCw } from 'react-icons/fi';

export default function LibrarianDashboard() {
  const [stats,setStats]=useState(null); const [failed,setFailed]=useState(false); const [loading,setLoading]=useState(true);
  const load=async()=>{setLoading(true);setFailed(false);try{const res=await dashboardAPI.getAdmin();const d=res.data?.data??res.data;setStats(d.stats||d);}catch{setFailed(true);}finally{setLoading(false);}};
  useEffect(()=>{load();},[]);
  return <div className="role-home"><section className="reader-hero staff-hero"><div><span className="eyebrow">LIBRARY DESK</span><h2>Good day, librarian.</h2><p>Keep the shelves moving. Everything your readers need is one step away.</p></div><div className="staff-hero-mark"><FiBook/></div></section>
    <div className="section-heading"><div><h3>Your daily tools</h3><p>Common tasks, right at hand</p></div></div>
    <div className="quick-action-grid"><Link to="/librarian/issue" className="quick-action"><span className="quick-action-icon forest"><FiCheckCircle/></span><span><strong>Issue a book</strong><small>Check out a title to a reader</small></span><FiArrowRight/></Link><Link to="/librarian/return" className="quick-action"><span className="quick-action-icon clay"><FiRotateCcw/></span><span><strong>Process a return</strong><small>Receive a book back on the shelf</small></span><FiArrowRight/></Link><Link to="/librarian/books" className="quick-action"><span className="quick-action-icon gold"><FiBook/></span><span><strong>Manage catalog</strong><small>Find or update a book record</small></span><FiArrowRight/></Link><Link to="/librarian/reservations" className="quick-action"><span className="quick-action-icon blue"><FiClock/></span><span><strong>Reservations</strong><small>See what readers are waiting for</small></span><FiArrowRight/></Link></div>
    {loading ? <div className="metric-grid">{[1,2,3,4].map(x=><div className="metric-skeleton" key={x}/>)}</div> : failed ? <div className="inline-notice"><span>Live activity totals aren’t available right now.</span><button onClick={load}><FiRefreshCw/> Retry</button></div> : <div className="metric-grid">{[['Books in collection',stats.totalBooks,FiBook],['Currently issued',stats.issuedBooks,FiCheckCircle],['Overdue returns',stats.overdueBooks,FiClock],['Readers',stats.totalStudents,FiRotateCcw]].map(([label,value,Icon])=><div className="metric-card" key={label}><span className="metric-icon"><Icon/></span><span><small>{label}</small><strong>{value ?? '—'}</strong></span></div>)}</div>}
  </div>;
}
