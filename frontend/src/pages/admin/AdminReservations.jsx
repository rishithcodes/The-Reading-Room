import React,{useCallback,useEffect,useState} from 'react';
import toast from 'react-hot-toast';
import {FiX} from 'react-icons/fi';
import {reservationAPI} from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import {formatDate} from '../../utils/helpers';

export default function AdminReservations(){
 const [items,setItems]=useState([]),[loading,setLoading]=useState(true),[selected,setSelected]=useState(null);
 const load=useCallback(async()=>{setLoading(true);try{const {data}=await reservationAPI.getAll();const body=data?.data??data;setItems(Array.isArray(body)?body:body?.reservations||[]);}catch(e){toast.error(e.response?.data?.message||'Could not load reservations');}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const cancel=async()=>{try{await reservationAPI.cancel(selected.id);toast.success('Reservation cancelled');setSelected(null);await load();}catch(e){toast.error(e.response?.data?.message||'Could not cancel reservation');}};
 const columns=[{key:'user',label:'Student',render:r=><span>{r.user?.fullName||'—'}<small className="table-secondary">{r.user?.email||''}</small></span>},{key:'book',label:'Book',render:r=>r.book?.title||'—'},{key:'createdAt',label:'Placed',render:r=>formatDate(r.createdAt)},{key:'expiresAt',label:'Hold until',render:r=>formatDate(r.expiresAt)},{key:'status',label:'Status',render:r=><span className={`status-pill ${r.status==='ACTIVE'?'on-time':'overdue'}`}>{r.status}</span>},{key:'action',label:'Action',render:r=>r.status==='ACTIVE'?<button className="icon-action" title="Cancel reservation" aria-label="Cancel reservation" onClick={()=>setSelected(r)}><FiX/></button>:'—'}];
 return <section className="reference-page"><header className="reference-header"><div><span className="eyebrow">READER QUEUE</span><h2>Reservations</h2><p>See who is waiting for unavailable titles and when their hold expires.</p></div><div className="fine-summary"><strong>{items.filter(r=>r.status==='ACTIVE').length}</strong><small>active requests</small></div></header><DataTable columns={columns} data={items} loading={loading} emptyMessage="There are no reservations right now."/><ConfirmDialog isOpen={!!selected} onClose={()=>setSelected(null)} onConfirm={cancel} title="Cancel reservation" message={`Cancel ${selected?.user?.fullName||'this reader'}’s hold on “${selected?.book?.title||'this book'}”?`}/></section>;
}
