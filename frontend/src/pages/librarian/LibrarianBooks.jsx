import React, { useCallback, useEffect, useState } from 'react';
import { FiPlus, FiEdit2, FiRefreshCw } from 'react-icons/fi';
import toast from 'react-hot-toast';
import { bookAPI, authorAPI, categoryAPI, publisherAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import Modal from '../../components/common/Modal';
import FormInput from '../../components/common/FormInput';

const blank = { isbn: '', title: '', description: '', authorId: '', categoryId: '', publisherId: '', publicationYear: '', language: 'English', totalCopies: 1, rackNumber: '' };
const list = (response, key) => { const body = response?.data?.data ?? response?.data ?? {}; return Array.isArray(body) ? body : body[key] || body.items || []; };

export default function LibrarianBooks() {
  const [books, setBooks] = useState([]), [options, setOptions] = useState({ authors: [], categories: [], publishers: [] });
  const [search, setSearch] = useState(''), [loading, setLoading] = useState(true), [saving, setSaving] = useState(false);
  const [modal, setModal] = useState(false), [editing, setEditing] = useState(null), [form, setForm] = useState(blank);
  const load = useCallback(async () => {
    setLoading(true);
    try { const [b, a, c, p] = await Promise.all([bookAPI.getAll({ search, pageSize: 100 }), authorAPI.getAll(), categoryAPI.getAll(), publisherAPI.getAll()]);
      setBooks(list(b, 'books')); setOptions({ authors: list(a, 'authors'), categories: list(c, 'categories'), publishers: list(p, 'publishers') });
    } catch (e) { toast.error(e.response?.data?.message || 'Could not load the book catalogue'); }
    finally { setLoading(false); }
  }, [search]);
  useEffect(() => { const timer = setTimeout(load, 200); return () => clearTimeout(timer); }, [load]);
  const open = (book = null) => { setEditing(book); setForm(book ? { ...blank, ...book, authorId: book.authorId || '', categoryId: book.categoryId || '', publisherId: book.publisherId || '' } : blank); setModal(true); };
  const submit = async e => { e.preventDefault(); setSaving(true); try {
    const payload = { ...form, authorId: Number(form.authorId), categoryId: Number(form.categoryId), publisherId: form.publisherId ? Number(form.publisherId) : null, publicationYear: form.publicationYear ? Number(form.publicationYear) : null, totalCopies: Number(form.totalCopies) };
    if (editing) await bookAPI.update(editing.id, payload); else await bookAPI.create(payload);
    toast.success(editing ? 'Book details saved' : 'Book added to the catalogue'); setModal(false); await load();
  } catch (e) { toast.error(e.response?.data?.message || 'Could not save this book'); } finally { setSaving(false); } };
  const columns = [
    { key: 'title', label: 'Book', render: b => <span className="table-primary">{b.title}<small className="table-secondary">{b.isbn || 'No ISBN'}</small></span> },
    { key: 'author', label: 'Author', render: b => b.author?.name || b.authorName || '—' },
    { key: 'category', label: 'Category', render: b => b.category?.name || b.categoryName || '—' },
    { key: 'copies', label: 'Copies', render: b => `${b.availableCopies ?? 0} available / ${b.totalCopies ?? 0}` },
    { key: 'rack', label: 'Rack', render: b => b.rackNumber || '—' },
    { key: 'status', label: 'Availability', render: b => <span className={`status-pill ${Number(b.availableCopies) > 0 ? 'on-time' : 'overdue'}`}>{Number(b.availableCopies) > 0 ? 'Available' : 'Checked out'}</span> },
    { key: 'actions', label: 'Actions', render: b => <button className="icon-action" title="Edit book details" aria-label={`Edit ${b.title}`} onClick={() => open(b)}><FiEdit2 /></button> },
  ];
  return <section className="reference-page">
    <header className="reference-header"><div><span className="eyebrow">CATALOGUE</span><h2>Books</h2><p>Find titles, check availability, and keep catalogue details up to date.</p></div><button className="btn btn-primary reference-add" onClick={() => open()}><FiPlus /> Add book</button></header>
    <div className="reference-toolbar"><label className="reference-search">Search catalogue<input value={search} onChange={e => setSearch(e.target.value)} placeholder="Search by title or ISBN" /></label><button className="report-refresh" onClick={load} disabled={loading}><FiRefreshCw /> Refresh</button><span>{books.length} titles</span></div>
    <DataTable columns={columns} data={books} loading={loading} emptyMessage={search ? 'No books match this search.' : 'The catalogue is empty. Add your first book to get started.'} />
    <Modal isOpen={modal} onClose={() => setModal(false)} title={editing ? 'Update book' : 'Add a book'}>
      <form className="reference-form" onSubmit={submit}>
        <FormInput label="Title" name="title" value={form.title} onChange={e => setForm({ ...form, title: e.target.value })} required />
        <FormInput label="ISBN" name="isbn" value={form.isbn} onChange={e => setForm({ ...form, isbn: e.target.value })} required />
        <FormInput type="textarea" label="Description" name="description" value={form.description} onChange={e => setForm({ ...form, description: e.target.value })} />
        <div className="reference-form-grid">
          <FormInput type="select" label="Author" name="authorId" value={form.authorId} onChange={e => setForm({ ...form, authorId: e.target.value })} options={options.authors} required />
          <FormInput type="select" label="Category" name="categoryId" value={form.categoryId} onChange={e => setForm({ ...form, categoryId: e.target.value })} options={options.categories} required />
          <FormInput type="select" label="Publisher" name="publisherId" value={form.publisherId} onChange={e => setForm({ ...form, publisherId: e.target.value })} options={options.publishers} />
          <FormInput label="Language" name="language" value={form.language} onChange={e => setForm({ ...form, language: e.target.value })} />
          <FormInput type="number" label="Publication year" name="publicationYear" value={form.publicationYear} onChange={e => setForm({ ...form, publicationYear: e.target.value })} />
          <FormInput type="number" label="Total copies" name="totalCopies" min="1" value={form.totalCopies} onChange={e => setForm({ ...form, totalCopies: e.target.value })} required />
          <FormInput label="Rack number" name="rackNumber" value={form.rackNumber} onChange={e => setForm({ ...form, rackNumber: e.target.value })} />
        </div>
        <button className="btn btn-primary" disabled={saving}>{saving ? 'Saving…' : editing ? 'Save changes' : 'Add book'}</button>
      </form>
    </Modal>
  </section>;
}
