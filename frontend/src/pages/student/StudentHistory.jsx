import React,{useCallback,useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import {FiBookOpen,FiRefreshCw} from 'react-icons/fi';
import {transactionAPI} from '../../services/apiService';
import {formatDate} from '../../utils/helpers';

export default function StudentHistory(){
 const [loans,setLoans]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(false);
 const load=useCallback(async()=>{setLoading(true);setError(false);try{const {data}=await transactionAPI.getMy();const body=data?.data??data;const all=Array.isArray(body)?body:body?.transactions||[];setLoans(all.filter(t=>String(t.status).toUpperCase()==='RETURNED'));}catch{setError(true);}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">YOUR READING JOURNEY</span><h2>Reading history</h2><p>A record of the books you’ve returned to the shelves.</p></div><div className="reader-hero-count"><FiBookOpen/><strong>{loans.length}</strong><span>books returned</span></div></section>
 {loading?<div className="loan-grid"><div className="loan-skeleton"/><div className="loan-skeleton"/></div>:error?<div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>We couldn’t load your history</h3><p>Check your connection and try again.</p><button className="btn btn-primary" onClick={load}>Try again</button></div>:loans.length===0?<div className="friendly-state"><span className="friendly-icon"><FiBookOpen/></span><h3>Your story starts with a first book</h3><p>Once you borrow and return a book, it will show up here.</p><Link className="btn btn-primary" to="/student/books">Browse books</Link></div>:<div className="table-container"><table><thead><tr><th>Book</th><th>Author</th><th>Borrowed</th><th>Returned</th><th>Record</th></tr></thead><tbody>{loans.map(t=><tr key={t.id}><td>{t.book?.title||'Library book'}</td><td>{t.book?.authorName||t.book?.author?.name||'—'}</td><td>{formatDate(t.issueDate)}</td><td>{formatDate(t.returnDate)}</td><td><span className="status-pill on-time">Returned</span></td></tr>)}</tbody></table></div>}</main>
}
