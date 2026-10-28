import React from 'react';

const FormInput = ({ label, name, type = 'text', value, onChange, error, required, placeholder, disabled, options }) => {
  const baseClasses = `block w-full rounded-lg border bg-gray-800 px-4 py-2.5 text-white placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-blue-500/50 transition-colors disabled:opacity-50 disabled:cursor-not-allowed ${error ? 'border-red-500 focus:border-red-500' : 'border-gray-700 focus:border-blue-500'}`;

  return (
    <div className="mb-4 w-full">
      {label && <label className="block text-sm font-medium text-gray-300 mb-1.5">{label} {required && <span className="text-red-500">*</span>}</label>}
      
      {type === 'select' ? (
        <select name={name} value={value} onChange={onChange} disabled={disabled} className={baseClasses}>
          <option value="" disabled>{placeholder || 'Select an option'}</option>
          {options?.map((opt, i) => (
            <option key={i} value={opt.value || opt.id}>{opt.label || opt.name}</option>
          ))}
        </select>
      ) : type === 'textarea' ? (
        <textarea name={name} value={value} onChange={onChange} disabled={disabled} placeholder={placeholder} rows={4} className={baseClasses} />
      ) : (
        <input type={type} name={name} value={value} onChange={onChange} disabled={disabled} placeholder={placeholder} className={baseClasses} />
      )}
      
      {error && <p className="mt-1 text-sm text-red-500">{error}</p>}
    </div>
  );
};

export default FormInput;
