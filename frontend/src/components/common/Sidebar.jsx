import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { FiHome, FiBook, FiUsers, FiLayers, FiPenTool, FiArchive, FiCreditCard, FiClock, FiFileText, FiSettings, FiX, FiLogOut, FiBell, FiThumbsUp, FiUser, FiRepeat, FiCheckSquare } from 'react-icons/fi';

const menus = {
  ADMIN: [
    ['/admin/dashboard','Overview',FiHome], ['/admin/books','Books',FiBook], ['/admin/users','People',FiUsers],
    ['/admin/categories','Categories',FiLayers], ['/admin/authors','Authors',FiPenTool], ['/admin/publishers','Publishers',FiArchive],
    ['/admin/transactions','Circulation',FiRepeat], ['/admin/fines','Fines',FiCreditCard], ['/admin/reservations','Reservations',FiClock],
    ['/admin/reports','Reports',FiFileText], ['/admin/settings','Settings',FiSettings],
  ],
  LIBRARIAN: [
    ['/librarian/dashboard','Overview',FiHome], ['/librarian/books','Books',FiBook], ['/librarian/issue','Issue a book',FiCheckSquare],
    ['/librarian/return','Returns',FiRepeat], ['/librarian/transactions','Circulation',FiFileText], ['/librarian/reservations','Reservations',FiClock], ['/librarian/fines','Fines',FiCreditCard],
  ],
  STUDENT: [
    ['/student/dashboard','My reading',FiHome], ['/student/books','Browse books',FiBook], ['/student/my-books','My books',FiArchive],
    ['/student/reservations','Reservations',FiClock], ['/student/fines','My fines',FiCreditCard], ['/student/history','History',FiFileText],
    ['/student/notifications','Notifications',FiBell], ['/student/recommendations','For you',FiThumbsUp], ['/student/profile','Profile',FiUser],
  ],
};

export default function Sidebar({ isOpen, onClose }) {
  const { user, logout } = useAuth();
  const items = menus[user?.role] || [];
  return <>
    <button className={`sidebar-scrim ${isOpen ? 'is-visible' : ''}`} onClick={onClose} aria-label="Close navigation" />
    <aside className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
      <div className="sidebar-brand"><span className="sidebar-brand-icon"><FiBook /></span><span>The Reading Room</span><button className="sidebar-close" onClick={onClose} aria-label="Close"><FiX /></button></div>
      <div className="sidebar-caption">YOUR LIBRARY</div>
      <nav className="sidebar-nav" aria-label="Main navigation">{items.map(([path,label,Icon]) => <NavLink key={path} to={path} onClick={onClose} className={({isActive}) => `sidebar-link ${isActive ? 'active' : ''}`}><Icon/><span>{label}</span></NavLink>)}</nav>
      <div className="sidebar-bottom"><div className="sidebar-profile"><span className="avatar">{user?.fullName?.charAt(0) || 'R'}</span><span className="profile-label"><strong>{user?.fullName || 'Reader'}</strong><small>{user?.role?.toLowerCase() || 'member'}</small></span></div><button className="sidebar-logout" onClick={logout}><FiLogOut/> Sign out</button></div>
    </aside>
  </>;
}
