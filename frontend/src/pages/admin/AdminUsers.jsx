import React, { useState, useEffect } from 'react';
import { userAPI } from '../../services/apiService';
import DataTable from '../../components/common/DataTable';
import Modal from '../../components/common/Modal';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import SearchBar from '../../components/common/SearchBar';
import FormInput from '../../components/common/FormInput';
import toast from 'react-hot-toast';
import { FiEdit, FiTrash2, FiUserCheck, FiUserX } from 'react-icons/fi';
import { formatDate } from '../../utils/helpers';

const AdminUsers = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  
  const [showModal, setShowModal] = useState(false);
  const [editData, setEditData] = useState(null);
  const [formData, setFormData] = useState({ fullName: '', email: '', phone: '', password: '', role: 'STUDENT', department: '', studentId: '' });
  
  const [showConfirm, setShowConfirm] = useState(false);
  const [deleteId, setDeleteId] = useState(null);

  useEffect(() => {
    fetchUsers();
  }, [search, roleFilter]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const res = await userAPI.getAll({ search, role: roleFilter });
      const body = res.data?.data ?? res.data ?? {};
      setUsers(Array.isArray(body) ? body : (body.users || body.items || []));
    } catch (e) {
      toast.error('Failed to fetch users');
    } finally {
      setLoading(false);
    }
  };

  const handleOpenModal = (user = null) => {
    if (user) {
      setEditData(user);
      setFormData({ ...user, password: '' });
    } else {
      setEditData(null);
      setFormData({ fullName: '', email: '', phone: '', password: '', role: 'STUDENT', department: '', studentId: '' });
    }
    setShowModal(true);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (editData) {
        const payload = { ...formData };
        if (!payload.password) delete payload.password;
        await userAPI.update(editData.id, payload);
      } else {
        await userAPI.create(formData);
      }
      toast.success(editData ? 'User updated' : 'User created');
      setShowModal(false);
      fetchUsers();
    } catch (e) {
      toast.error(e.response?.data?.message || 'Action failed');
    }
  };

  const handleDeactivate = async () => {
    try {
      await userAPI.delete(deleteId); // assuming delete implies deactivate
      toast.success('User deactivated');
      fetchUsers();
    } catch (e) { toast.error('Deactivate failed'); }
  };

  const columns = [
    { key: 'fullName', label: 'Name' },
    { key: 'email', label: 'Email' },
    { key: 'role', label: 'Role', render: u => <span className={`px-2 py-1 rounded text-xs ${u.role === 'ADMIN' ? 'bg-purple-500/20 text-purple-400' : u.role === 'LIBRARIAN' ? 'bg-blue-500/20 text-blue-400' : 'bg-gray-700 text-gray-300'}`}>{u.role}</span> },
    { key: 'department', label: 'Department', render: u => u.department || 'N/A' },
    { key: 'status', label: 'Status', render: u => <span className={`px-2 py-1 rounded text-xs ${u.isActive !== false ? 'bg-green-500/20 text-green-500' : 'bg-red-500/20 text-red-500'}`}>{u.isActive !== false ? 'Active' : 'Inactive'}</span> },
    { key: 'createdAt', label: 'Joined', render: u => formatDate(u.createdAt) },
    { key: 'actions', label: 'Actions', render: u => (
      <div className="flex gap-2">
        <button onClick={() => handleOpenModal(u)} className="text-yellow-400 hover:text-yellow-300"><FiEdit size={16} /></button>
        <button onClick={() => { setDeleteId(u.id); setShowConfirm(true); }} className="text-red-400 hover:text-red-300" title="Deactivate"><FiUserX size={16} /></button>
      </div>
    )}
  ];

  return (
    <div className="space-y-4">
      <div className="flex justify-between items-center">
        <h2 className="text-2xl font-bold text-white">Manage Users</h2>
        <button onClick={() => handleOpenModal()} className="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">Add User</button>
      </div>
      
      <div className="flex gap-4 mb-4">
        <SearchBar value={search} onChange={setSearch} placeholder="Search by name, email, ID..." />
        <select className="bg-gray-800 border border-gray-700 rounded-lg px-3 py-2 text-white" value={roleFilter} onChange={e => setRoleFilter(e.target.value)}>
          <option value="">All Roles</option>
          <option value="STUDENT">Student</option>
          <option value="LIBRARIAN">Librarian</option>
          <option value="ADMIN">Admin</option>
        </select>
      </div>

      <DataTable columns={columns} data={users} loading={loading} />

      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title={editData ? 'Edit User' : 'Add User'}>
        <form onSubmit={handleSubmit} className="space-y-4">
          <FormInput label="Full Name" name="fullName" value={formData.fullName} onChange={e => setFormData({...formData, fullName: e.target.value})} required />
          <FormInput type="email" label="Email" name="email" value={formData.email} onChange={e => setFormData({...formData, email: e.target.value})} required />
          <FormInput type="select" label="Role" name="role" value={formData.role} onChange={e => setFormData({...formData, role: e.target.value})} options={[{label: 'Student', value: 'STUDENT'}, {label: 'Librarian', value: 'LIBRARIAN'}, {label: 'Admin', value: 'ADMIN'}]} required />
          {formData.role === 'STUDENT' && (
            <>
              <FormInput label="Department" name="department" value={formData.department} onChange={e => setFormData({...formData, department: e.target.value})} required />
              <FormInput label="Student ID" name="studentId" value={formData.studentId} onChange={e => setFormData({...formData, studentId: e.target.value})} required />
            </>
          )}
          <FormInput label="Phone" name="phone" value={formData.phone} onChange={e => setFormData({...formData, phone: e.target.value})} />
          <FormInput type="password" label={editData ? "Password (leave blank to keep current)" : "Password"} name="password" value={formData.password} onChange={e => setFormData({...formData, password: e.target.value})} required={!editData} />
          <button type="submit" className="w-full py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700">Save User</button>
        </form>
      </Modal>

      <ConfirmDialog isOpen={showConfirm} onClose={() => setShowConfirm(false)} onConfirm={handleDeactivate} title="Deactivate User" message="Are you sure you want to deactivate this user? They will not be able to log in." />
    </div>
  );
};
export default AdminUsers;
