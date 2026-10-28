import React,{useCallback,useEffect,useState} from 'react';
import {FiCreditCard,FiCheck} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {fineAPI} from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import {formatCurrency,formatDate} from '../../utils/helpers';

export default function AdminFines(){
 const [items,setItems]=useState([]),[loading,setLoading]=useState(true),[paying,setPaying]=useState(null);
 const load=useCallback(async()=>{setLoading(true);try{const {data}=await fineAPI.getAll();const body=data?.data??data;setItems(Array.isArray(body)?body:body?.fines||[]);}catch(e){toast.error(e.response?.data?.message||'Could not load fines');}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const recordPayment=async(id)=>{setPaying(id);try{await fineAPI.pay(id,{});toast.success('Fine marked as paid');await load();}catch(e){toast.error(e.response?.data?.message||'Could not record payment');}finally{setPaying(null);}};
 const pending=items.reduce((sum,f)=>sum+Math.max(0,Number(f.amount||0)-Number(f.paidAmount||0)),0);
 const columns=[{key:'student',label:'Student',render:f=>f.transaction?.user?.fullName||'—'},{key:'book',label:'Book',render:f=>f.transaction?.book?.title||'—'},{key:'overdueDays',label:'Days late',render:f=>f.overdueDays??'—'},{key:'amount',label:'Fine',render:f=>formatCurrency(f.amount)},{key:'paidAmount',label:'Paid',render:f=>formatCurrency(f.paidAmount)},{key:'balance',label:'Balance',render:f=>formatCurrency(Math.max(0,Number(f.amount||0)-Number(f.paidAmount||0)))},{key:'status',label:'Status',render:f=><span className={`status-pill ${f.status==='PAID'?'on-time':'overdue'}`}>{f.status}</span>},{key:'action',label:'Action',render:f=>f.status==='PAID'?<span className="text-muted">Complete</span>:<button className="text-link fine-pay" disabled={paying===f.id} onClick={()=>recordPayment(f.id)}><FiCheck/>{paying===f.id?'Saving…':'Record payment'}</button>}];
 return <section className="reference-page"><header className="reference-header"><div><span className="eyebrow">ACCOUNT ADMINISTRATION</span><h2>Fines</h2><p>Review overdue charges and record payments received at the desk.</p></div><div className="fine-summary"><FiCreditCard/><span><small>Outstanding total</small><strong>{formatCurrency(pending)}</strong></span></div></header><DataTable columns={columns} data={items} loading={loading} emptyMessage="No fines have been recorded."/></section>;
}
