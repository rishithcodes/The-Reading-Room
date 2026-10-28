import React from 'react';
import { FiMenu, FiBell, FiChevronDown } from 'react-icons/fi';
import { useAuth } from '../../context/AuthContext';
import { useLocation, Link } from 'react-router-dom';

const titles = { dashboard:'Overview', books:'Books', 'my-books':'My books', users:'People', issue:'Issue a book', return:'Returns', transactions:'Circulation', reservations:'Reservations', fines:'Fines', reports:'Reports', settings:'Settings', categories:'Categories', authors:'Authors', publishers:'Publishers', history:'Reading history', notifications:'Notifications', recommendations:'For you', profile:'My profile' };
export default function Navbar({ onMenuClick }) {
  const { user } = useAuth();
  const location = useLocation();
  const key = location.pathname.split('/').filter(Boolean).pop() || 'dashboard';
  return <header className="topbar">
    <div className="topbar-heading"><button onClick={onMenuClick} className="menu-toggle" aria-label="Open navigation"><FiMenu/></button><div><span className="topbar-kicker">THE READING ROOM</span><h1>{titles[key] || key.replaceAll('-', ' ')}</h1></div></div>
    <div className="topbar-actions"><span className="today-note">A good day for a good book</span>{user?.role === 'STUDENT' && <Link className="topbar-bell" to="/student/notifications" aria-label="Notifications"><FiBell/></Link>}<div className="topbar-user"><span className="avatar avatar-small">{user?.fullName?.charAt(0) || 'R'}</span><span className="topbar-user-name">{user?.fullName?.split(' ')[0]}</span><FiChevronDown className="muted-icon"/></div></div>
  </header>;
}
