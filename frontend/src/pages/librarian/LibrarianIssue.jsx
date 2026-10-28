import React, { useState, useEffect } from 'react';
import { userAPI, bookAPI, transactionAPI } from '../../services/apiService';
import FormInput from '../../components/common/FormInput';
import toast from 'react-hot-toast';
import useDebounce from '../../hooks/useDebounce';
import { FiCheckCircle, FiSearch, FiUser, FiBook } from 'react-icons/fi';

const LibrarianIssue = () => {
  const [userSearch, setUserSearch] = useState('');
  const [bookSearch, setBookSearch] = useState('');
  
  const debouncedUserSearch = useDebounce(userSearch, 300);
  const debouncedBookSearch = useDebounce(bookSearch, 300);
  
  const [users, setUsers] = useState([]);
  const [books, setBooks] = useState([]);
  
  const [selectedUser, setSelectedUser] = useState(null);
  const [selectedBook, setSelectedBook] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (debouncedUserSearch.length > 2) fetchUsers();
    else setUsers([]);
  }, [debouncedUserSearch]);

  useEffect(() => {
    if (debouncedBookSearch.length > 2) fetchBooks();
    else setBooks([]);
  }, [debouncedBookSearch]);

  const fetchUsers = async () => {
    try {
      const res = await userAPI.getAll({ search: debouncedUserSearch, role: 'STUDENT', pageSize: 5 });
      const body = res.data?.data ?? res.data ?? {};
      setUsers(Array.isArray(body) ? body : (body.users || body.items || []));
    } catch (e) { toast.error(e.response?.data?.message || 'Could not find students'); }
  };

  const fetchBooks = async () => {
    try {
      const res = await bookAPI.getAll({ search: debouncedBookSearch, availability: 'available', pageSize: 5 });
      const body = res.data?.data ?? res.data ?? {};
      setBooks(Array.isArray(body) ? body : (body.books || body.items || []));
    } catch (e) { toast.error(e.response?.data?.message || 'Could not find available books'); }
  };

  const handleIssue = async () => {
    if (!selectedUser || !selectedBook) return toast.error('Select both user and book');
    setLoading(true);
    try {
      await transactionAPI.issue({ userId: selectedUser.id, bookId: selectedBook.id });
      toast.success('Book issued successfully!');
      setSelectedUser(null);
      setSelectedBook(null);
      setUserSearch('');
      setBookSearch('');
    } catch (e) {
      toast.error(e.response?.data?.message || 'Failed to issue book');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto space-y-6">
      <h2 className="text-2xl font-bold text-white">Issue Book</h2>
      
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        <div className="bg-gray-900 p-6 rounded-xl border border-gray-800">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2"><FiUser /> Step 1: Select Student</h3>
          {!selectedUser ? (
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"><FiSearch className="text-gray-500" /></div>
              <input type="text" className="block w-full pl-10 pr-3 py-2 border border-gray-700 rounded-lg bg-gray-800 text-white placeholder-gray-500" placeholder="Search by name or email..." value={userSearch} onChange={e => setUserSearch(e.target.value)} />
              {users.length > 0 && (
                <div className="absolute z-10 w-full mt-1 bg-gray-800 border border-gray-700 rounded-md shadow-lg">
                  {users.map(u => (
                    <div key={u.id} className="px-4 py-2 hover:bg-gray-700 cursor-pointer text-white" onClick={() => { setSelectedUser(u); setUsers([]); }}>
                      {u.fullName} ({u.email})
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : (
            <div className="bg-gray-800 p-4 rounded-lg flex justify-between items-center">
              <div>
                <p className="text-white font-medium">{selectedUser.fullName}</p>
                <p className="text-sm text-gray-400">{selectedUser.email}</p>
                <p className="text-sm text-gray-400">ID: {selectedUser.studentId || selectedUser.id}</p>
              </div>
              <button onClick={() => setSelectedUser(null)} className="text-blue-500 text-sm hover:underline">Change</button>
            </div>
          )}
        </div>

        <div className="bg-gray-900 p-6 rounded-xl border border-gray-800">
          <h3 className="text-lg font-semibold text-white mb-4 flex items-center gap-2"><FiBook /> Step 2: Select Book</h3>
          {!selectedBook ? (
            <div className="relative">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"><FiSearch className="text-gray-500" /></div>
              <input type="text" className="block w-full pl-10 pr-3 py-2 border border-gray-700 rounded-lg bg-gray-800 text-white placeholder-gray-500" placeholder="Search by title or ISBN..." value={bookSearch} onChange={e => setBookSearch(e.target.value)} />
              {books.length > 0 && (
                <div className="absolute z-10 w-full mt-1 bg-gray-800 border border-gray-700 rounded-md shadow-lg max-h-48 overflow-y-auto">
                  {books.map(b => (
                    <div key={b.id} className="px-4 py-2 hover:bg-gray-700 cursor-pointer text-white" onClick={() => { setSelectedBook(b); setBooks([]); }}>
                      {b.title} (ISBN: {b.isbn})
                    </div>
                  ))}
                </div>
              )}
            </div>
          ) : (
            <div className="bg-gray-800 p-4 rounded-lg flex justify-between items-center">
              <div>
                <p className="text-white font-medium line-clamp-1">{selectedBook.title}</p>
                <p className="text-sm text-gray-400">{selectedBook.isbn}</p>
                <span className="text-xs bg-green-500/20 text-green-400 px-2 py-1 rounded">Available: {selectedBook.availableCopies}</span>
              </div>
              <button onClick={() => setSelectedBook(null)} className="text-blue-500 text-sm hover:underline">Change</button>
            </div>
          )}
        </div>
      </div>

      <div className="bg-gray-900 p-6 rounded-xl border border-gray-800 text-center">
        <h3 className="text-lg font-semibold text-white mb-4">Step 3: Confirm Issue</h3>
        <p className="text-gray-400 mb-6">Review the details above before confirming.</p>
        <button onClick={handleIssue} disabled={!selectedUser || !selectedBook || loading} className="px-8 py-3 bg-blue-600 hover:bg-blue-700 text-white font-bold rounded-xl disabled:opacity-50 flex items-center justify-center gap-2 mx-auto">
          <FiCheckCircle size={20} /> {loading ? 'Issuing...' : 'Issue Book'}
        </button>
      </div>
    </div>
  );
};
export default LibrarianIssue;
