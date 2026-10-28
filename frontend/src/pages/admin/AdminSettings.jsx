import React, { useEffect, useState } from 'react';
import { settingsAPI } from '../../services/apiService';
import toast from 'react-hot-toast';
import { FiBookOpen, FiSave } from 'react-icons/fi';

const fields = [
  { key: 'library_name', label: 'Library name', type: 'text' },
  { key: 'library_email', label: 'Library contact email', type: 'email' },
  { key: 'loan_days', label: 'Standard loan period (days)', type: 'number', min: 1 },
  { key: 'borrow_limit', label: 'Maximum books per student', type: 'number', min: 1 },
  { key: 'fine_per_day', label: 'Overdue fine per day (₹)', type: 'number', min: 0, step: '0.01' },
  { key: 'reservation_expiry_days', label: 'Reservation hold (days)', type: 'number', min: 1 },
];

const AdminSettings = () => {
  const [values, setValues] = useState({});
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    settingsAPI.getAll()
      .then(({ data }) => setValues(data?.data ?? data ?? {}))
      .catch((error) => toast.error(error.response?.data?.message || 'Could not load library settings'))
      .finally(() => setLoading(false));
  }, []);

  const save = async (event) => {
    event.preventDefault();
    setSaving(true);
    try {
      for (const field of fields) {
        await settingsAPI.update(field.key, String(values[field.key] ?? ''));
      }
      toast.success('Library settings saved');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Could not save library settings');
    } finally {
      setSaving(false);
    }
  };

  return (
    <section className="space-y-6">
      <header>
        <p className="eyebrow">LIBRARY ADMINISTRATION</p>
        <h2 className="text-2xl font-bold">Library settings</h2>
        <p className="mt-2 text-sm text-gray-500">Set borrowing rules and the contact details shown to your community.</p>
      </header>
      {loading ? <div className="friendly-state">Loading your library settings…</div> : (
        <form onSubmit={save} className="max-w-3xl rounded-2xl border border-stone-200 bg-white p-6 shadow-sm">
          <div className="mb-6 flex items-center gap-3 border-b border-stone-100 pb-5">
            <span className="quick-action-icon gold"><FiBookOpen /></span>
            <div><h3 className="font-semibold">Reading room details</h3><p className="text-sm text-gray-500">Changes take effect for new loans and reservations.</p></div>
          </div>
          <div className="grid gap-5 sm:grid-cols-2">
            {fields.map((field) => (
              <label key={field.key} className="block text-sm font-medium text-gray-700">
                {field.label}
                <input
                  className="mt-2 block w-full rounded-lg border border-stone-300 bg-white px-3 py-2.5 text-gray-900 outline-none transition focus:border-emerald-700 focus:ring-2 focus:ring-emerald-700/15"
                  type={field.type}
                  min={field.min}
                  step={field.step}
                  value={values[field.key] ?? ''}
                  required
                  onChange={(event) => setValues((current) => ({ ...current, [field.key]: event.target.value }))}
                />
              </label>
            ))}
          </div>
          <div className="mt-7 flex justify-end">
            <button disabled={saving} className="btn btn-primary inline-flex items-center gap-2 disabled:opacity-60" type="submit"><FiSave />{saving ? 'Saving…' : 'Save settings'}</button>
          </div>
        </form>
      )}
    </section>
  );
};

export default AdminSettings;
