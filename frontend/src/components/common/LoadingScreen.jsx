import React from 'react';
import { FiLoader } from 'react-icons/fi';

const LoadingScreen = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-900 text-white">
      <FiLoader className="animate-spin text-5xl text-blue-500 mb-4" />
      <h1 className="text-2xl font-bold">The Reading Room</h1>
      <p className="text-gray-400 mt-2">Loading...</p>
    </div>
  );
};

export default LoadingScreen;
