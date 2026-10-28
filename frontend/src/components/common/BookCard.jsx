import React from 'react';
import { FiBook, FiCheckCircle, FiXCircle } from 'react-icons/fi';

const BookCard = ({ book, onReserve, onViewDetails, showActions = true }) => {
  const isAvailable = book.availableCopies > 0;

  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl overflow-hidden hover:border-gray-600 transition-colors flex flex-col h-full group">
      <div className="h-48 bg-gray-800 flex items-center justify-center border-b border-gray-800 relative cursor-pointer" onClick={() => onViewDetails(book)}>
        {book.coverImage ? (
          <img src={book.coverImage} alt={book.title} className="w-full h-full object-cover opacity-80 group-hover:opacity-100 transition-opacity" />
        ) : (
          <FiBook size={48} className="text-gray-600" />
        )}
        <div className="absolute top-2 right-2">
          <span className={`px-2 py-1 text-xs font-semibold rounded-full flex items-center gap-1 ${isAvailable ? 'bg-green-500/20 text-green-400' : 'bg-red-500/20 text-red-400'}`}>
            {isAvailable ? <FiCheckCircle size={12}/> : <FiXCircle size={12}/>}
            {isAvailable ? 'Available' : 'Unavailable'}
          </span>
        </div>
      </div>
      <div className="p-4 flex flex-col flex-1">
        <h3 className="text-lg font-bold text-white mb-1 line-clamp-1" title={book.title}>{book.title}</h3>
        <p className="text-sm text-gray-400 mb-2">{book.author?.name || 'Unknown Author'}</p>
        <div className="mt-auto pt-4 flex items-center justify-between">
          <span className="text-xs text-gray-500 bg-gray-800 px-2 py-1 rounded">{book.category?.name || 'Uncategorized'}</span>
          {showActions && (
            <button
              onClick={() => !isAvailable && onReserve && onReserve(book)}
              disabled={isAvailable}
              className="px-3 py-1.5 text-sm font-medium bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
            >
              {isAvailable ? 'Available' : 'Reserve'}
            </button>
          )}
        </div>
      </div>
    </div>
  );
};

export default BookCard;
