import React, { useState, useEffect } from 'react';
import { bookAPI, authorAPI, categoryAPI, publisherAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import Modal from '../../components/common/Modal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import SearchBar from '../../components/common/SearchBar';
import FilterPanel from '../../components/common/FilterPanel';
import Pagination from '../../components/common/Pagination';
import FormInput from '../../components/common/FormInput';
import QrModal from '../../components/common/QrModal';
import toast from 'react-hot-toast';
import { FiEdit, FiTrash2, FiMaximize } from 'react-icons/fi';
import { getStatusBadgeClass } from '../../utils/helpers';

const AdminBooks = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const [filters, setFilters] = useState({});
  const [options, setOptions] = useState({ categories: [], authors: [], publishers: [] });
  
  const [showModal, setShowModal] = useState(false);
  const [editData, setEditData] = useState(null);
  const [formData, setFormData] = useState({ isbn: '', title: '', description: '', authorId: '', categoryId: '', publisherId: '', publicationYear: '', language: 'English', totalCopies: 1, rackNumber: '' });
  
  const [showConfirm, setShowConfirm] = useState(false);
  const [deleteId, setDeleteId] = useState(null);
  const [showQr, setShowQr] = useState(false);
  const [qrBook, setQrBook] = useState(null);

  useEffect(() => {
    fetchOptions();
  }, []);

  useEffect(() => {
    fetchBooks();
  }, [page, search, filters]);

  const fetchOptions = async () => {
    try {
      const [catRes, authRes, pubRes] = await Promise.all([
        categoryAPI.getAll({ pageSize: 50 }),
        authorAPI.getAll({ pageSize: 100 }),
        publisherAPI.getAll({ pageSize: 100 })
      ]);
      const unwrap = (response, key) => {
        const body = response?.data?.data ?? response?.data ?? {};
        return Array.isArray(body) ? body : (body[key] || body.items || []);
      };
      setOptions({ categories: unwrap(catRes, 'categories'), authors: unwrap(authRes, 'authors'), publishers: unwrap(pubRes, 'publishers') });
    } catch (e) { toast.error('Failed to load filter options'); }
  };

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = await bookAPI.getAll({ search, ...filters, page, pageSize: 10 });
      const body = res.data?.data ?? res.data ?? {};
      const items = Array.isArray(body) ? body : (body.books || body.items || []);
      setBooks(items);
      setTotal(Number(body.total ?? items.length));
    } catch (e) {
      toast.error('Failed to fetch books');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (book = null) => {
    if (book) {
      setEditData(book);
      setFormData({ ...book, authorId: book.authorId || '', categoryId: book.categoryId || '', publisherId: book.publisherId || '' });
    } else {
      setEditData(null);
      setFormData({ isbn: '', title: '', description: '', authorId: '', categoryId: '', publisherId: '', publicationYear: '', language: 'English', totalCopies: 1, rackNumber: '' });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editData) await bookAPI.update(editData.id, formData);
      else await bookAPI.create(formData);
      toast.success(editData ? 'Book updated' : 'Book created');
      setShowModal(false);
      fetchBooks();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Action failed');
    }
  };

  const handleDelete = async () => {
    try {
      await bookAPI.delete(deleteId);
      toast.success('Book deleted');
      fetchBooks();
    } catch (e) { toast.error('Delete failed'); }
  };

  const columns = [
    { key: 'title', label: 'Title' },
    { key: 'author', label: 'Author', render: b => b.author?.name || b.authorName || 'N/A' },
    { key: 'category', label: 'Category', render: b => b.category?.name || b.categoryName || 'N/A' },
    { key: 'isbn', label: 'ISBN' },
    { key: 'copies', label: 'Copies', render: b => `${b.availableCopies}/${b.totalCopies}` },
    { key: 'status', label: 'Status', render: b => <span className={`px-2 py-1 rounded text-xs ${b.availableCopies > 0 ? 'bg-green-500/20 text-green-500' : 'bg-red-500/20 text-red-500'}`}>{b.availableCopies > 0 ? 'Available' : 'Unavailable'}</span> },
    { key: 'actions', label: 'Actions', render: b => (
      <div className="flex gap-2">
        <button onClick={() => { setQrBook(b); setShowQr(true); }} className="text-blue-400 hover:text-blue-300"><FiMaximize size={16} /></button>
        <button onClick={() => handleOpenModal(b)} className="text-yellow-400 hover:text-yellow-300"><FiEdit size={16} /></button>
        <button onClick={() => { setDeleteId(b.id); setShowConfirm(true); }} className="text-red-400 hover:text-red-300"><FiTrash2 size={16} /></button>
      </div>
    )}
  ];

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-white">Manage Books</h2>
        <button onClick={() => handleOpenModal()} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">Add Book</button>
      </div>
      
      <div className="flex gap-4 mb-4">
        <SearchBar value={search} onChange={setSearch} placeholder="Search by title, ISBN..." />
        <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white" value={filters.categoryId || ''} onChange={e => setFilters({...filters, categoryId: e.target.value})}>
          <option value="">All Categories</option>
          {options.categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
        </select>
        <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white" value={filters.availability || ''} onChange={e => setFilters({...filters, availability: e.target.value})}>
          <option value="">All Availability</option>
          <option value="available">Available</option>
          <option value="unavailable">Unavailable</option>
        </select>
      </div>

      <DataTable columns={columns} data={books} loading={loading} />
      <Pagination page={page} totalPages={Math.ceil(total/10)} totalCount={total} pageSize={10} onPageChange={setPage} />

      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title={editData ? 'Edit Book' : 'Add Book'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <FormInput label="Title" name="title" value={formData.title} onChange={e => setFormData({...formData, title: e.target.value})} required />
          <FormInput label="ISBN" name="isbn" value={formData.isbn} onChange={e => setFormData({...formData, isbn: e.target.value})} required />
          <FormInput type="textarea" label="Description" name="description" value={formData.description} onChange={e => setFormData({...formData, description: e.target.value})} />
          <div className="grid grid-cols-2 gap-4">
            <FormInput type="select" label="Category" name="categoryId" value={formData.categoryId} onChange={e => setFormData({...formData, categoryId: e.target.value})} options={options.categories} required />
            <FormInput type="select" label="Author" name="authorId" value={formData.authorId} onChange={e => setFormData({...formData, authorId: e.target.value})} options={options.authors} required />
            <FormInput type="select" label="Publisher" name="publisherId" value={formData.publisherId} onChange={e => setFormData({...formData, publisherId: e.target.value})} options={options.publishers} />
            <FormInput label="Language" name="language" value={formData.language} onChange={e => setFormData({...formData, language: e.target.value})} />
            <FormInput type="number" label="Year" name="publicationYear" value={formData.publicationYear} onChange={e => setFormData({...formData, publicationYear: e.target.value})} />
            <FormInput type="number" label="Total Copies" name="totalCopies" value={formData.totalCopies} onChange={e => setFormData({...formData, totalCopies: parseInt(e.target.value)})} required />
            <FormInput label="Rack Number" name="rackNumber" value={formData.rackNumber} onChange={e => setFormData({...formData, rackNumber: e.target.value})} />
          </div>
          <button type="submit" className="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save Book</button>
        </form>
      </Modal>

      <ConfirmDialog isOpen={showConfirm} onClose={() => setShowConfirm(false)} onConfirm={handleDelete} title="Delete Book" message="Are you sure you want to delete this book? This cannot be undone." />
      <QrModal isOpen={showQr} onClose={() => setShowQr(false)} book={qrBook} />
    </div>
  );
};
export default AdminBooks;
