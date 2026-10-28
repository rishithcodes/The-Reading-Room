import React from 'react';

export default function StatCard({ title, value, icon: Icon, color = 'forest', subtitle, trend }) {
  return <article className="stat-card">
    <div className="stat-info"><p className="stat-label">{title}</p><div className="stat-value">{value ?? '—'}</div>{(subtitle || trend != null) && <p className="stat-subtitle">{trend != null && <strong className={trend > 0 ? 'positive' : 'negative'}>{trend > 0 ? '+' : ''}{trend}% </strong>}{subtitle}</p>}</div>
    <div className={`stat-icon ${color}`} aria-hidden="true"><Icon size={22}/></div>
  </article>;
}
