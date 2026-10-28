import React,{useCallback,useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import {FiClock,FiBookOpen,FiX,FiRefreshCw} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {reservationAPI} from '../../services/apiService';
import {formatDate} from '../../utils/helpers';

export default function StudentReservations(){
 const [items,setItems]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(false),[busy,setBusy]=useState(null);
 const load=useCallback(async()=>{setLoading(true);setError(false);try{const {data}=await reservationAPI.getMy();const body=data?.data??data;setItems(Array.isArray(body)?body:body?.reservations||[]);}catch{setError(true);}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const cancel=async(id)=>{setBusy(id);try{await reservationAPI.cancel(id);toast.success('Reservation cancelled');await load();}catch(e){toast.error(e.response?.data?.message||'Could not cancel reservation');}finally{setBusy(null);}};
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">YOUR PLACE IN LINE</span><h2>Reservations</h2><p>Keep track of books you’re waiting to read.</p></div><div className="reader-hero-count"><FiClock/><strong>{items.filter(x=>x.status==='ACTIVE').length}</strong><span>active reservations</span></div></section>
 {loading?<div className="loan-grid">{[1,2].map(n=><div className="loan-skeleton" key={n}/>)}</div>:error?<div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>We couldn’t load your reservations</h3><p>Check your connection and try again.</p><button className="btn btn-primary" onClick={load}>Try again</button></div>:items.length===0?<div className="friendly-state"><span className="friendly-icon"><FiBookOpen/></span><h3>No reservations yet</h3><p>If a book is checked out, reserve it from the catalog and it will appear here.</p><Link className="btn btn-primary" to="/student/books">Browse books</Link></div>:<div className="reservation-list">{items.map(item=><article className="student-record" key={item.id}><span className="record-icon"><FiBookOpen/></span><div className="record-main"><h3>{item.book?.title||'Reserved book'}</h3><p>{item.book?.authorName||item.book?.author?.name||'Library collection'}</p><small>Requested {formatDate(item.createdAt)}{item.expiresAt?` · Hold until ${formatDate(item.expiresAt)}`:''}</small></div><span className={`status-pill ${item.status==='ACTIVE'?'on-time':'overdue'}`}>{item.status}</span>{item.status==='ACTIVE'&&<button className="icon-action" disabled={busy===item.id} onClick={()=>cancel(item.id)} aria-label="Cancel reservation" title="Cancel reservation">{busy===item.id?'…':<FiX/>}</button>}</article>)}</div>}</main>
}
