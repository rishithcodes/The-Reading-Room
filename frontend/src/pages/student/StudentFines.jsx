import React,{useCallback,useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import {FiCreditCard,FiBookOpen,FiRefreshCw} from 'react-icons/fi';
import {fineAPI} from '../../services/apiService';
import {formatCurrency,formatDate} from '../../utils/helpers';

export default function StudentFines(){
 const [fines,setFines]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(false);
 const load=useCallback(async()=>{setLoading(true);setError(false);try{const {data}=await fineAPI.getMy();const body=data?.data??data;setFines(Array.isArray(body)?body:body?.fines||[]);}catch{setError(true);}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const due=fines.filter(f=>!['PAID','WAIVED'].includes(String(f.status).toUpperCase())).reduce((sum,f)=>sum+Number(f.amount||0)-Number(f.paidAmount||0),0);
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">ACCOUNT SUMMARY</span><h2>Fines & payments</h2><p>See any overdue charges recorded on your library account.</p></div><div className="reader-hero-count"><FiCreditCard/><strong>{formatCurrency(due)}</strong><span>outstanding balance</span></div></section>
 {loading?<div className="loan-grid"><div className="loan-skeleton"/><div className="loan-skeleton"/></div>:error?<div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>Fines aren’t available right now</h3><p>Please try again in a moment.</p><button className="btn btn-primary" onClick={load}>Try again</button></div>:fines.length===0?<div className="friendly-state"><span className="friendly-icon"><FiCreditCard/></span><h3>You’re all clear</h3><p>No fines have been recorded on your account.</p><Link className="btn btn-primary" to="/student/books">Browse the catalog</Link></div>:<div className="table-container"><table><thead><tr><th>Book</th><th>Recorded</th><th>Overdue days</th><th>Fine</th><th>Paid</th><th>Balance</th><th>Status</th></tr></thead><tbody>{fines.map(f=>{const balance=Math.max(0,Number(f.amount||0)-Number(f.paidAmount||0));return <tr key={f.id}><td><span className="font-medium">{f.transaction?.book?.title||'Library fine'}</span></td><td>{formatDate(f.transaction?.returnDate||f.transaction?.dueDate)}</td><td>{f.overdueDays??'—'}</td><td>{formatCurrency(f.amount)}</td><td>{formatCurrency(f.paidAmount)}</td><td>{formatCurrency(balance)}</td><td><span className={`status-pill ${balance===0?'on-time':'overdue'}`}>{f.status}</span></td></tr>})}</tbody></table></div>}</main>
}
