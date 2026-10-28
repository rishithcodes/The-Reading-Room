import React, { useState, useEffect } from 'react';
import { bookAPI, categoryAPI, reservationAPI } from '../../services/apiService';
import BookCard from '../../components/common/BookCard';
import SearchBar from '../../components/common/SearchBar';
import Pagination from '../../components/common/Pagination';
import Modal from '../../components/common/Modal';
import toast from 'react-hot-toast';

const StudentBooks = () => {
  const [books, setBooks] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [page, setPage] = useState(1);
  const [search, setSearch] = useState('');
  const [categoryId, setCategoryId] = useState('');
  const [availability, setAvailability] = useState('');

  const [selectedBook, setSelectedBook] = useState(null);
  const [showModal, setShowModal] = useState(false);

  useEffect(() => {
    categoryAPI.getAll({pageSize: 100}).then(res => {
      const body = res.data?.data ?? res.data ?? {};
      setCategories(Array.isArray(body) ? body : (body.categories || body.items || []));
    }).catch(() => toast.error('Could not load book categories'));
  }, []);

  useEffect(() => {
    fetchBooks();
  }, [page, search, categoryId, availability]);

  const fetchBooks = async () => {
    setLoading(true);
    try {
      const res = await bookAPI.getAll({ search, categoryId, availability, page, pageSize: 12 });
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

  const handleReserve = async (book) => {
    try {
      await reservationAPI.create({ bookId: book.id });
      toast.success(`Successfully reserved "${book.title}"`);
      setShowModal(false);
      fetchBooks();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Failed to reserve book');
    }
  };

  const handleViewDetails = (book) => {
    setSelectedBook(book);
    setShowModal(true);
  };

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-4">
        <h2 className="text-2xl font-bold text-white">Browse Books</h2>
        <div className="flex flex-col sm:flex-row gap-3 w-full md:w-auto">
          <SearchBar value={search} onChange={setSearch} placeholder="Search books..." />
          <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white text-sm focus:ring-blue-500" value={categoryId} onChange={e => setCategoryId(e.target.value)}>
            <option value="">All Categories</option>
            {categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
          <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white text-sm focus:ring-blue-500" value={availability} onChange={e => setAvailability(e.target.value)}>
            <option value="">Availability</option>
            <option value="available">Available Now</option>
          </select>
        </div>
      </div>

      {loading ? (
        <div className="text-center py-12 text-gray-400">Loading books...</div>
      ) : books.length > 0 ? (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-6">
            {books.map(book => (
              <BookCard key={book.id} book={book} onReserve={handleReserve} onViewDetails={handleViewDetails} />
            ))}
          </div>
          <Pagination page={page} totalPages={Math.ceil(total/12)} totalCount={total} pageSize={12} onPageChange={setPage} />
        </>
      ) : (
        <div className="text-center py-12 text-gray-400 bg-gray-900 rounded-xl border border-gray-800">
          No books found matching your criteria.
        </div>
      )}

      {selectedBook && (
        <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Book Details" size="md">
          <div className="space-y-4 text-gray-300">
            <h3 className="text-xl font-bold text-white">{selectedBook.title}</h3>
            <p><strong>Author:</strong> {selectedBook.author?.name || selectedBook.authorName || 'N/A'}</p>
            <p><strong>Category:</strong> {selectedBook.category?.name || selectedBook.categoryName || 'N/A'}</p>
            <p><strong>Publisher:</strong> {selectedBook.publisher?.name || 'N/A'}</p>
            <p><strong>ISBN:</strong> {selectedBook.isbn}</p>
            <p><strong>Year:</strong> {selectedBook.publicationYear}</p>
            <p><strong>Language:</strong> {selectedBook.language}</p>
            <p><strong>Rack:</strong> {selectedBook.rackNumber || 'N/A'}</p>
            <div className="p-4 bg-gray-800 rounded-lg">
              <p className="text-sm font-medium">Availability</p>
              <p className="text-2xl font-bold text-white">{selectedBook.availableCopies} <span className="text-base font-normal text-gray-400">/ {selectedBook.totalCopies} copies</span></p>
            </div>
            {selectedBook.description && (
              <div>
                <p className="font-medium text-white mb-1">Description:</p>
                <p className="text-sm">{selectedBook.description}</p>
              </div>
            )}
            <button 
              onClick={() => handleReserve(selectedBook)}
              disabled={selectedBook.availableCopies > 0}
              className="w-full mt-4 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white font-medium rounded-lg transition-colors"
            >
              {selectedBook.availableCopies > 0 ? 'Available to borrow from the library desk' : 'Reserve Book'}
            </button>
          </div>
        </Modal>
      )}
    </div>
  );
};
export default StudentBooks;
