import React from 'react';
import AdminReferenceManager from './AdminReferenceManager';
import { authorAPI } from '../../services/apiService';
export default function AdminAuthors(){ return <AdminReferenceManager kind="authors" api={authorAPI} />; }
