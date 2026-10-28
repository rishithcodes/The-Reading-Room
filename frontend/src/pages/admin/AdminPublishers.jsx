import React from 'react';
import AdminReferenceManager from './AdminReferenceManager';
import { publisherAPI } from '../../services/apiService';
export default function AdminPublishers(){ return <AdminReferenceManager kind="publishers" api={publisherAPI} />; }
