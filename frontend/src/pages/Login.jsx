import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FiBook, FiLock, FiMail, FiArrowRight } from 'react-icons/fi';
import toast from 'react-hot-toast';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const { login } = useAuth();
  const navigate = useNavigate();
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const user = await login(email, password);
      if (!user?.role) { toast.error(user?.error || 'Unable to sign in. Check your details.'); return; }
      toast.success('Welcome back');
      navigate(user.role === 'ADMIN' ? '/admin/dashboard' : user.role === 'LIBRARIAN' ? '/librarian/dashboard' : '/student/dashboard');
    } catch (err) { toast.error(err.response?.data?.message || 'Unable to sign in. Check your details.'); }
    finally { setLoading(false); }
  };
  return <main className="auth-page">
    <section className="auth-story">
      <div className="auth-brand"><span className="brand-mark"><FiBook /></span><span>The Reading Room</span></div>
      <div className="story-copy"><span className="eyebrow">A little room to yourself</span><h1>Find your next<br/><em>great story.</em></h1><p>A quiet corner for every story, every curious mind, and every new beginning.</p></div>
      <div className="story-footer">A thoughtful place to read, learn, and return.</div>
    </section>
    <section className="auth-form-wrap"><form className="auth-form" onSubmit={handleSubmit}>
      <span className="eyebrow">Welcome back</span><h2>Sign in to your account</h2><p className="form-intro">Your next chapter is waiting.</p>
      <label>Email address</label><div className="auth-input"><FiMail/><input type="email" required value={email} onChange={e=>setEmail(e.target.value)} placeholder="you@example.com" autoComplete="email"/></div>
      <label>Password</label><div className="auth-input"><FiLock/><input type="password" required value={password} onChange={e=>setPassword(e.target.value)} placeholder="Enter your password" autoComplete="current-password"/></div>
      <button className="auth-submit" disabled={loading}>{loading ? 'Signing in…' : <>Sign in <FiArrowRight/></>}</button>
      <p className="auth-register">New to the Reading Room? <Link to="/register">Create a student account</Link></p>
    </form><div className="auth-note">A good book is a friend that never lets you down. <span>— Anonymous</span></div></section>
  </main>;
}
