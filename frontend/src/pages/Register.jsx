import React,{useState} from 'react';
import {Link,useNavigate} from 'react-router-dom';
import {FiArrowRight,FiBook,FiMail,FiUser,FiHash,FiLayers,FiPhone,FiLock} from 'react-icons/fi';
import {authAPI} from '../services/apiService';
import toast from 'react-hot-toast';

const initial={fullName:'',email:'',studentId:'',department:'',phone:'',password:'',confirmPassword:''};
const Register=()=>{
 const [form,setForm]=useState(initial),[loading,setLoading]=useState(false);const navigate=useNavigate();
 const change=e=>setForm(current=>({...current,[e.target.name]:e.target.value}));
 const submit=async e=>{e.preventDefault();if(form.password.length<8){toast.error('Use at least 8 characters for your password');return;}if(form.password!==form.confirmPassword){toast.error('Passwords do not match');return;}setLoading(true);try{await authAPI.register(form);toast.success('Your student account is ready. Please sign in.');navigate('/login');}catch(error){toast.error(error.response?.data?.message||'Registration failed. Please try again.');}finally{setLoading(false);}};
 const field=(name,label,type,Icon,required=true,autoComplete)=> <label className="register-field" key={name}><span>{label}{required&&<i> *</i>}</span><div className="register-input"><Icon/><input name={name} type={type} value={form[name]} onChange={change} required={required} autoComplete={autoComplete} minLength={type==='password'?8:undefined}/></div></label>;
 return <main className="auth-page register-page"><section className="auth-story"><div className="auth-brand"><span className="brand-mark"><FiBook/></span><span>The Reading Room</span></div><div className="story-copy"><span className="eyebrow">A little room to yourself</span><h1>Make space for<br/><em>new stories.</em></h1><p>Join a thoughtful community of readers and find your next favourite book.</p></div><div className="story-footer">A good place to read, learn, and return.</div></section>
 <section className="auth-form-wrap register-form-wrap"><form className="auth-form register-form" onSubmit={submit}><span className="eyebrow">YOUR LIBRARY CARD</span><h2>Create a student account</h2><p className="form-intro">A few details and you’ll be ready to explore the shelves.</p><div className="register-grid">{field('fullName','Full name','text',FiUser,true,'name')}{field('email','Email address','email',FiMail,true,'email')}{field('studentId','Student ID','text',FiHash,true,'off')}{field('department','Department','text',FiLayers,true,'organization')}{field('phone','Phone number','tel',FiPhone,false,'tel')}{field('password','Password','password',FiLock,true,'new-password')}{field('confirmPassword','Confirm password','password',FiLock,true,'new-password')}</div><button className="auth-submit" disabled={loading}>{loading?'Creating account…':<>Create account <FiArrowRight/></>}</button><p className="auth-register">Already have an account? <Link to="/login">Sign in</Link></p></form><div className="auth-note">Every reader has a story worth discovering.</div></section></main>;
};
export default Register;
