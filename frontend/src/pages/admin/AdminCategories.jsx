import React from 'react';
import AdminReferenceManager from './AdminReferenceManager';
import { categoryAPI } from '../../services/apiService';
export default function AdminCategories(){ return <AdminReferenceManager kind="categories" api={categoryAPI} />; }
