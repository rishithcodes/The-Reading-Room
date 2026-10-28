import React,{useCallback,useEffect,useState} from 'react';
import {Link} from 'react-router-dom';
import {FiBookOpen,FiRefreshCw,FiArrowRight} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {recommendationAPI,reservationAPI} from '../../services/apiService';
import BookCard from '../../components/common/BookCard';
import Modal from '../../components/common/Modal';

export default function StudentRecommendations(){
 const [books,setBooks]=useState([]),[loading,setLoading]=useState(true),[error,setError]=useState(false),[selected,setSelected]=useState(null);
 const load=useCallback(async()=>{setLoading(true);setError(false);try{const {data}=await recommendationAPI.getMy();const body=data?.data??data;setBooks(Array.isArray(body)?body:body?.books||[]);}catch{setError(true);}finally{setLoading(false);}},[]);
 useEffect(()=>{load();},[load]);
 const reserve=async(book)=>{try{await reservationAPI.create({bookId:book.id});toast.success(`Reserved “${book.title}”`);setSelected(null);}catch(e){toast.error(e.response?.data?.message||'Could not reserve this book');}};
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">PICKED FOR YOUR SHELF</span><h2>For you</h2><p>Explore available titles related to what you’ve borrowed before.</p></div><div className="staff-hero-mark"><FiBookOpen/></div></section>
 {loading?<div className="book-grid">{[1,2,3,4].map(n=><div className="loan-skeleton" key={n}/>)}</div>:error?<div className="friendly-state"><span className="friendly-icon"><FiRefreshCw/></span><h3>We couldn’t find recommendations</h3><p>Try again, or browse the full catalog.</p><button className="btn btn-primary" onClick={load}>Try again</button></div>:books.length===0?<div className="friendly-state"><span className="friendly-icon"><FiBookOpen/></span><h3>Let’s find your next read</h3><p>Recommendations grow as you borrow books. In the meantime, explore the complete collection.</p><Link className="btn btn-primary" to="/student/books">Browse all books <FiArrowRight/></Link></div>:<div className="book-grid">{books.map(book=><BookCard key={book.id} book={book} onReserve={reserve} onViewDetails={setSelected}/>)}</div>}
 {selected&&<Modal isOpen onClose={()=>setSelected(null)} title="Book details"><div className="space-y-3"><h3 className="text-lg font-semibold">{selected.title}</h3><p>{selected.authorName||selected.author?.name||'Unknown author'}</p><p>{selected.categoryName||selected.category?.name||'Library collection'}</p><p>{selected.availableCopies} copies available</p>{selected.description&&<p>{selected.description}</p>}<button className="btn btn-primary" disabled={selected.availableCopies>0} onClick={()=>reserve(selected)}>{selected.availableCopies>0?'Available to borrow':'Reserve this book'}</button></div></Modal>}
 </main>
}
