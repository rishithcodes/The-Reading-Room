import React from 'react';

const FilterPanel = ({ filters, onFilterChange, options }) => {
  return (
    <div className="bg-gray-900 border border-gray-800 rounded-xl p-4 space-y-4">
      <h3 className="text-sm font-semibold text-white uppercase tracking-wider mb-2">Filters</h3>
      
      {options.categories && (
        <div>
          <label className="block text-xs text-gray-400 mb-1">Category</label>
          <select 
            value={filters.category || ''} 
            onChange={(e) => onFilterChange({ ...filters, category: e.target.value })}
            className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-blue-500"
          >
            <option value="">All Categories</option>
            {options.categories.map(c => <option key={c.id} value={c.id}>{c.name}</option>)}
          </select>
        </div>
      )}

      {options.availability && (
        <div>
          <label className="block text-xs text-gray-400 mb-1">Availability</label>
          <select 
            value={filters.availability || ''} 
            onChange={(e) => onFilterChange({ ...filters, availability: e.target.value })}
            className="w-full bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-sm text-white focus:outline-none focus:border-blue-500"
          >
            <option value="">All</option>
            <option value="available">Available Now</option>
            <option value="unavailable">Unavailable</option>
          </select>
        </div>
      )}

      <button 
        onClick={() => onFilterChange({})} 
        className="w-full mt-4 px-4 py-2 text-sm text-gray-300 bg-gray-800 border border-gray-700 rounded-lg hover:bg-gray-700 transition-colors"
      >
        Clear Filters
      </button>
    </div>
  );
};

export default FilterPanel;
