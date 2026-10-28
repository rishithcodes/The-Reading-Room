import React,{useCallback,useEffect,useState} from 'react';
import {FiBell,FiCheck,FiRefreshCw} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {notificationAPI} from '../../services/apiService';
import {formatDate} from '../../utils/helpers';

export default function StudentNotifications(){
 const [items,setItems]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(false),[saving,setSaving]=useState(false);
 const load=useCallback(async()=>{setLoading(true);setError(false);try{const {data}=await notificationAPI.getMy();const body=data?.data??data;setItems(Array.isArray(body)?body:body?.notifications||[]);}catch{setError(true);}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const markOne=async(id)=>{try{await notificationAPI.markRead(id);setItems(current=>current.map(item=>item.id===id?{...item,read:true}:item));}catch(e){toast.error(e.response?.data?.message||'Could not update notification');}};
 const markAll=async()=>{setSaving(true);try{await notificationAPI.markAllRead();setItems(current=>current.map(item=>({...item,read:true})));toast.success('All caught up');}catch(e){toast.error(e.response?.data?.message||'Could not update notifications');}finally{setSaving(false);}};
 const unread=items.filter(item=>!item.read).length;
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">KEEP IN THE LOOP</span><h2>Notifications</h2><p>Updates about loans, reservations, and your library account.</p></div><div className="reader-hero-count"><FiBell/><strong>{unread}</strong><span>unread updates</span></div></section>
 {unread>0&&<div className="section-heading"><span>You have {unread} unread {unread===1?'update':'updates'}.</span><button className="text-link" disabled={saving} onClick={markAll}>{saving?'Saving…':'Mark all as read'} <FiCheck/></button></div>}
 {loading?<div className="loan-grid"><div className="loan-skeleton"/></div>:error?<div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>Notifications didn’t load</h3><p>Please try again.</p><button className="btn btn-primary" onClick={load}>Try again</button></div>:items.length===0?<div className="friendly-state"><span className="friendly-icon"><FiBell/></span><h3>You’re all caught up</h3><p>Important updates from the library will appear here.</p></div>:<div className="notification-list">{items.map(item=><article className={`student-record notification-record ${item.read?'is-read':'is-unread'}`} key={item.id}><span className="record-icon"><FiBell/></span><div className="record-main"><div className="notification-title-row"><h3>{item.title}</h3>{!item.read&&<span className="unread-dot" aria-label="Unread"/>}</div><p>{item.message}</p><small>{item.type?.replaceAll('_',' ')} · {formatDate(item.createdAt)}</small></div>{!item.read&&<button className="icon-action" onClick={()=>markOne(item.id)} aria-label="Mark as read" title="Mark as read"><FiCheck/></button>}</article>)}</div>}</main>
}
