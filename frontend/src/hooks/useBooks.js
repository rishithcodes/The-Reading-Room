import { useState, useEffect } from 'react';
import { bookAPI } from '../services/apiService';

export default function useBooks(filters = {}) {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [total, setTotal] = useState(0);

  useEffect(() => {
    const fetchBooks = async () => {
      try {
        setLoading(true);
        const res = await bookAPI.getAll(filters);
        setBooks(res.data?.books || []);
        setTotal(res.data?.total || 0);
        setError(null);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to fetch books');
      } finally {
        setLoading(false);
      }
    };
    fetchBooks();
  }, [JSON.stringify(filters)]);

  return { books, loading, error, total };
}
