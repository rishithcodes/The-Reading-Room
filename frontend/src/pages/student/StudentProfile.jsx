import React,{useEffect,useState} from 'react';
import {FiUser,FiSave,FiLock,FiRefreshCw} from 'react-icons/fi';
import toast from 'react-hot-toast';
import {profileAPI} from '../../services/apiService';
import {useAuth} from '../../context/AuthContext';

export default function StudentProfile(){
 const {user,updateUserInContext}=useAuth();
 const [profile,setProfile]=useState({fullName:'',email:'',phone:'',department:'',studentId:''});
 const [password,setPassword]=useState({currentPassword:'',newPassword:'',confirmPassword:''});
 const [loading,setLoading]=useState(true),[saving,setSaving]=useState(false),[passwordSaving,setPasswordSaving]=useState(false);
 useEffect(()=>{profileAPI.get().then(({data})=>{const body=data?.data??data;setProfile({...profile,...body});}).catch(e=>toast.error(e.response?.data?.message||'Could not load your profile')).finally(()=>setLoading(false));},[]);
 const saveProfile=async(e)=>{e.preventDefault();setSaving(true);try{const {data}=await profileAPI.update(profile);const body=data?.data??data;setProfile({...profile,...body});updateUserInContext({...user,...body});toast.success('Profile updated');}catch(e){toast.error(e.response?.data?.message||'Could not save your profile');}finally{setSaving(false);}};
 const changePassword=async(e)=>{e.preventDefault();if(password.newPassword!==password.confirmPassword){toast.error('New passwords do not match');return;}setPasswordSaving(true);try{await profileAPI.changePassword({currentPassword:password.currentPassword,newPassword:password.newPassword});setPassword({currentPassword:'',newPassword:'',confirmPassword:''});toast.success('Password updated');}catch(e){toast.error(e.response?.data?.message||'Could not update your password');}finally{setPasswordSaving(false);}};
 if(loading)return <main className="reader-page"><div className="loan-skeleton"/></main>;
 return <main className="reader-page"><section className="reader-hero"><div><span className="eyebrow">YOUR ACCOUNT</span><h2>My profile</h2><p>Keep your contact details and sign-in information up to date.</p></div><div className="staff-hero-mark"><FiUser/></div></section>
 <div className="profile-grid"><form className="profile-card" onSubmit={saveProfile}><div className="profile-card-heading"><span className="quick-action-icon forest"><FiUser/></span><div><h3>Personal details</h3><p>Your library uses these details to reach you.</p></div></div>
 {[['fullName','Full name'],['email','Email address'],['studentId','Student ID'],['department','Department'],['phone','Phone number']].map(([key,label])=><label className="profile-field" key={key}>{label}<input type={key==='email'?'email':'text'} value={profile[key]||''} readOnly={key==='email'} required={key==='fullName'} onChange={e=>setProfile(p=>({...p,[key]:e.target.value}))}/></label>)}
 <button className="btn btn-primary profile-save" disabled={saving}><FiSave/>{saving?'Saving…':'Save profile'}</button></form>
 <form className="profile-card" onSubmit={changePassword}><div className="profile-card-heading"><span className="quick-action-icon gold"><FiLock/></span><div><h3>Change password</h3><p>Use at least 8 characters for your new password.</p></div></div>
 {[['currentPassword','Current password'],['newPassword','New password'],['confirmPassword','Confirm new password']].map(([key,label])=><label className="profile-field" key={key}>{label}<input type="password" autoComplete="new-password" minLength={key==='currentPassword'?undefined:8} required value={password[key]} onChange={e=>setPassword(p=>({...p,[key]:e.target.value}))}/></label>)}
 <button className="btn btn-primary profile-save" disabled={passwordSaving}><FiLock/>{passwordSaving?'Updating…':'Update password'}</button></form></div>
 </main>;
}
