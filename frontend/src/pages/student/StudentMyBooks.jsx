import React, { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { FiBookOpen, FiCalendar, FiClock, FiRefreshCw, FiArrowRight } from 'react-icons/fi';
import { transactionAPI } from '../../services/apiService';
import { formatDate, getOverdueDays } from '../../utils/helpers';

export default function StudentMyBooks() {
  const [books, setBooks] = useState([]); const [loading, setLoading] = useState(true); const [error, setError] = useState(false);
  const load = useCallback(async () => {
    setLoading(true); setError(false);
    try { const res = await transactionAPI.getMy(); const body = res.data?.data ?? res.data; setBooks(Array.isArray(body) ? body : body?.transactions || body?.items || []); }
    catch { setError(true); setBooks([]); }
    finally { setLoading(false); }
  }, []);
  useEffect(() => { load(); }, [load]);
  const active = books.filter(t => !['RETURNED','CANCELLED'].includes(String(t.status || '').toUpperCase()));
  return <div className="reader-page">
    <section className="reader-hero"><div><span className="eyebrow">YOUR READING SHELF</span><h2>Books in your care</h2><p>Keep an eye on your due dates and enjoy every page.</p></div><div className="reader-hero-count"><FiBookOpen/><strong>{active.length}</strong><span>currently borrowed</span></div></section>
    <div className="section-heading"><div><h3>Currently borrowed</h3><p>Your active loans and due dates</p></div><Link to="/student/books" className="text-link">Find another book <FiArrowRight/></Link></div>
    {loading ? <div className="loan-grid">{[1,2,3].map(n=><div className="loan-skeleton" key={n}/>)}</div> : error ? <div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>We couldn’t load your books</h3><p>Please check that the library service is running, then try again.</p><button className="btn btn-primary" onClick={load}>Try again</button></div> : active.length === 0 ? <div className="friendly-state"><span className="friendly-icon"><FiBookOpen/></span><h3>Your next read is waiting</h3><p>You don’t have any borrowed books right now. Browse the shelves and find something lovely.</p><Link className="btn btn-primary" to="/student/books">Explore the catalog <FiArrowRight/></Link></div> : <div className="loan-grid">{active.map((loan,i)=>{
      const due = loan.dueDate ? new Date(loan.dueDate) : null; const daysLate = loan.dueDate ? getOverdueDays(loan.dueDate) : 0; const soon = due && !daysLate && (due-new Date())/86400000 <= 3;
      const state = daysLate ? 'overdue' : soon ? 'due-soon' : 'on-time'; const book = loan.book || {};
      return <article className="loan-card" key={loan.id || i}><div className={`book-cover cover-${i%5}`}><FiBookOpen/><span>{book.title || 'Reading copy'}</span></div><div className="loan-card-body"><div className="loan-meta"><span className={`status-pill ${state}`}>{daysLate ? `${daysLate} days overdue` : soon ? 'Due soon' : 'On track'}</span><span className="loan-id">#{book.id || loan.bookId || loan.id || '—'}</span></div><h4>{book.title || loan.bookTitle || 'Borrowed book'}</h4><p className="loan-author">{book.author?.name || book.authorName || ''}</p><div className="loan-due"><FiCalendar/><span>Due {due ? formatDate(loan.dueDate) : 'date unavailable'}</span></div><div className="due-track"><span className={state}/></div></div></article>
    })}</div>}
    <div className="reader-tip"><FiClock/><span><strong>A little reminder</strong> Return books on time so the next reader can enjoy them too.</span></div>
  </div>;
}
