import React from 'react';
import { FiLoader, FiInbox } from 'react-icons/fi';

const DataTable = ({ columns, data, loading, emptyMessage = 'No data available', onRowClick }) => {
  if (loading) {
    return (
      <div className="table-state" role="status">
        <FiLoader className="animate-spin" />
        <span>Loading records…</span>
      </div>
    );
  }

  if (!data || data.length === 0) {
    return (
      <div className="table-state">
        <FiInbox />
        <span>{emptyMessage}</span>
      </div>
    );
  }

  return (
    <div className="data-table-wrapper">
      <table className="data-table">
        <thead>
          <tr>
            {columns.map((col, idx) => (
              <th key={idx}>{col.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          {data.map((row, rowIndex) => (
            <tr key={row.id ?? rowIndex} onClick={() => onRowClick && onRowClick(row)} className={onRowClick ? 'clickable-row' : ''}>
              {columns.map((col, colIndex) => (
                <td key={colIndex}>
                  {col.render ? col.render(row) : row[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default DataTable;
