import React,{useCallback,useEffect,useMemo,useState} from 'react';
import {FiDownload,FiRefreshCw} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {reportAPI} from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import {formatCurrency,formatDate} from '../../utils/helpers';

const reports={
 transactions:{label:'All transactions',load:reportAPI.getTransactions,rows:'transactions',columns:[['id','ID',r=>`#${r.id}`],['user','Student',r=>r.user?.fullName||'—'],['book','Book',r=>r.book?.title||'—'],['issueDate','Issued',r=>formatDate(r.issueDate)],['dueDate','Due',r=>formatDate(r.dueDate)],['status','Status',r=>r.status]]},
 issued:{label:'Currently issued',load:reportAPI.getIssued,rows:'transactions',columns:[['user','Student',r=>r.user?.fullName||'—'],['book','Book',r=>r.book?.title||'—'],['issueDate','Issued',r=>formatDate(r.issueDate)],['dueDate','Due',r=>formatDate(r.dueDate)]]},
 overdue:{label:'Overdue books',load:reportAPI.getOverdue,rows:'transactions',columns:[['user','Student',r=>r.user?.fullName||'—'],['book','Book',r=>r.book?.title||'—'],['dueDate','Due date',r=>formatDate(r.dueDate)],['finePreview','Fine to date',r=>formatCurrency(r.finePreview)]]},
 fines:{label:'Fines',load:reportAPI.getFines,rows:'fines',columns:[['student','Student',r=>r.transaction?.user?.fullName||'—'],['book','Book',r=>r.transaction?.book?.title||'—'],['overdueDays','Days late',r=>r.overdueDays],['amount','Amount',r=>formatCurrency(r.amount)],['paidAmount','Paid',r=>formatCurrency(r.paidAmount)],['status','Status',r=>r.status]]},
 books:{label:'Book inventory',load:reportAPI.getBooks,rows:'books',columns:[['title','Title',r=>r.title],['author','Author',r=>r.authorName||'—'],['category','Category',r=>r.categoryName||'—'],['totalCopies','Copies',r=>r.totalCopies],['availableCopies','Available',r=>r.availableCopies]]},
 popular:{label:'Most borrowed',load:reportAPI.getMostBorrowed,rows:'books',columns:[['title','Title',r=>r.book?.title||'—'],['author','Author',r=>r.book?.authorName||'—'],['borrowCount','Times borrowed',r=>r.borrowCount]]},
 users:{label:'Library members',load:reportAPI.getUsers,rows:'users',columns:[['fullName','Name',r=>r.fullName],['email','Email',r=>r.email],['role','Role',r=>r.role],['department','Department',r=>r.department||'—'],['status','Status',r=>r.status]]},
};
const unwrap=(response,key)=>{const body=response?.data?.data??response?.data??{};return Array.isArray(body)?body:(body[key]||[]);};

export default function AdminReports(){
 const [type,setType]=useState('transactions'),[rows,setRows]=useState([]),[loading,setLoading]=useState(true);
 const config=reports[type];
 const load=useCallback(async()=>{setLoading(true);try{const response=await config.load();setRows(unwrap(response,config.rows));}catch(e){setRows([]);toast.error(e.response?.data?.message||'Could not load this report');}finally{setLoading(false);}},[config]);
 useEffect(()=>{load();},[load]);
 const columns=useMemo(()=>config.columns.map(([key,label,render])=>({key,label,render})),[config]);
 const exportCsv=()=>{const headers=config.columns.map(([,label])=>label);const content=[headers,...rows.map(row=>config.columns.map(([, ,render])=>String(render(row)??'')))].map(line=>line.map(value=>`"${String(value).replaceAll('"','""')}"`).join(',')).join('\r\n');const url=URL.createObjectURL(new Blob([content],{type:'text/csv;charset=utf-8'}));const link=document.createElement('a');link.href=url;link.download=`reading-room-${type}.csv`;document.body.appendChild(link);link.click();link.remove();URL.revokeObjectURL(url);};
 return <section className="reference-page"><header className="reference-header"><div><span className="eyebrow">LIBRARY INSIGHTS</span><h2>Reports</h2><p>Review live library records and download a CSV for your records.</p></div><button className="btn btn-primary reference-add" disabled={!rows.length} onClick={exportCsv}><FiDownload/> Export CSV</button></header><div className="report-toolbar"><label>Choose a report<select value={type} onChange={e=>setType(e.target.value)}>{Object.entries(reports).map(([key,item])=><option key={key} value={key}>{item.label}</option>)}</select></label><button className="report-refresh" onClick={load} disabled={loading}><FiRefreshCw/>{loading?'Loading…':'Refresh'}</button><span>{rows.length} records</span></div><DataTable columns={columns} data={rows} loading={loading} emptyMessage="No records are available for this report yet."/></section>;
}
