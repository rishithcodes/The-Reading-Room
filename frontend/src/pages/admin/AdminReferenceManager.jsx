import React,{useCallback,useEffect,useMemo,useState} from 'react';
import DataTable from '../../components/common/DataTable';
import Modal from '../../components/common/Modal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import SearchBar from '../../components/common/SearchBar';
import toast from 'react-hot-toast';
import {FiEdit,FiTrash2,FiPlus} from 'react-icons/fi';

const configs={
 categories:{title:'Categories',singular:'category',api:'categoryAPI',fields:[['name','Name','text',true],['description','Description','textarea',false]],columns:[['name','Category'],['description','Description']]},
 authors:{title:'Authors',singular:'author',api:'authorAPI',fields:[['name','Name','text',true],['country','Country','text',false],['biography','Biography','textarea',false]],columns:[['name','Author'],['country','Country'],['biography','Biography']]},
 publishers:{title:'Publishers',singular:'publisher',api:'publisherAPI',fields:[['name','Name','text',true],['email','Email','email',false],['phone','Phone','tel',false],['website','Website','url',false],['address','Address','textarea',false]],columns:[['name','Publisher'],['email','Email'],['phone','Phone'],['website','Website']]},
};

export default function AdminReferenceManager({kind,api}){
 const config=configs[kind];const [rows,setRows]=useState([]),[loading,setLoading]=useState(true),[search,setSearch]=useState(''),[modal,setModal]=useState(false),[editing,setEditing]=useState(null),[deleting,setDeleting]=useState(null),[form,setForm]=useState({});
 const load=useCallback(async()=>{setLoading(true);try{const {data}=await api.getAll({pageSize:100});const body=data?.data??data;setRows(Array.isArray(body)?body:body?.[kind]||[]);}catch(e){toast.error(e.response?.data?.message||`Could not load ${config.title.toLowerCase()}`);}finally{setLoading(false);}},[api,kind,config.title]);
 useEffect(()=>{load();},[load]);
 const filtered=useMemo(()=>rows.filter(row=>config.fields.some(([key])=>String(row[key]||'').toLowerCase().includes(search.toLowerCase()))),[rows,search,config.fields]);
 const open=(row=null)=>{setEditing(row);setForm(row?{...row}:{ });setModal(true);};
 const save=async e=>{e.preventDefault();try{if(editing)await api.update(editing.id,form);else await api.create(form);toast.success(`${config.singular[0].toUpperCase()+config.singular.slice(1)} ${editing?'updated':'added'}`);setModal(false);await load();}catch(err){toast.error(err.response?.data?.message||`Could not save ${config.singular}`);}};
 const remove=async()=>{try{await api.delete(deleting.id);toast.success(`${config.singular[0].toUpperCase()+config.singular.slice(1)} removed`);setDeleting(null);await load();}catch(err){toast.error(err.response?.data?.message||`Could not remove ${config.singular}`);}};
 const columns=[...config.columns.map(([key,label])=>({key,label,render:row=><span className={key==='description'||key==='biography'?'reference-description':''}>{row[key]||'—'}</span>})),{key:'actions',label:'Actions',render:row=><div className="reference-actions"><button aria-label={`Edit ${config.singular}`} onClick={()=>open(row)}><FiEdit/></button><button aria-label={`Delete ${config.singular}`} onClick={()=>setDeleting(row)}><FiTrash2/></button></div>}];
 return <section className="reference-page"><header className="reference-header"><div><span className="eyebrow">COLLECTION DETAILS</span><h2>Manage {config.title.toLowerCase()}</h2><p>Add and maintain the names used throughout the catalog.</p></div><button className="btn btn-primary reference-add" onClick={()=>open()}><FiPlus/> Add {config.singular}</button></header>
 <div className="reference-toolbar"><SearchBar value={search} onChange={setSearch} placeholder={`Search ${config.title.toLowerCase()}...`}/><span>{filtered.length} {config.title.toLowerCase()}</span></div>
 <DataTable columns={columns} data={filtered} loading={loading} emptyMessage={`No ${config.title.toLowerCase()} found. Add the first one to get started.`}/>
 <Modal isOpen={modal} onClose={()=>setModal(false)} title={`${editing?'Edit':'Add'} ${config.singular}`}>
  <form className="reference-form" onSubmit={save}>{config.fields.map(([key,label,type,required])=><label key={key}>{label}{required&&<i> *</i>}{type==='textarea'?<textarea rows="4" value={form[key]||''} required={required} onChange={e=>setForm(v=>({...v,[key]:e.target.value}))}/>:<input type={type} value={form[key]||''} required={required} onChange={e=>setForm(v=>({...v,[key]:e.target.value}))}/>}</label>)}<button className="btn btn-primary" type="submit">{editing?'Save changes':`Add ${config.singular}`}</button></form>
 </Modal>
 <ConfirmDialog isOpen={!!deleting} onClose={()=>setDeleting(null)} onConfirm={remove} title={`Delete ${config.singular}`} message={`Delete “${deleting?.name||''}”? Items used by books cannot be removed.`}/>
 </section>;
}
