import React, { useEffect, useRef } from 'react';
import Modal from './Modal';
import QRCode from 'qrcode';
import { FiDownload } from 'react-icons/fi';

const QrModal = ({ isOpen, onClose, book }) => {
  const canvasRef = useRef(null);

  useEffect(() => {
    if (isOpen && book && canvasRef.current) {
      QRCode.toCanvas(canvasRef.current, book.id.toString(), {
        width: 250,
        margin: 2,
        color: { dark: '#000000', light: '#ffffff' }
      }, (error) => {
        if (error) console.error(error);
      });
    }
  }, [isOpen, book]);

  const handleDownload = () => {
    if (canvasRef.current && book) {
      const url = canvasRef.current.toDataURL('image/png');
      const a = document.createElement('a');
      a.href = url;
      a.download = `QR_${book.isbn || book.id}.png`;
      a.click();
    }
  };

  if (!book) return null;

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Book QR Code" size="sm">
      <div className="flex flex-col items-center p-6 bg-white rounded-lg mb-4">
        <canvas ref={canvasRef}></canvas>
        <div className="mt-4 text-center">
          <p className="font-bold text-gray-900">{book.title}</p>
          <p className="text-sm text-gray-600">ID: {book.id}</p>
        </div>
      </div>
      <button onClick={handleDownload} className="w-full flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white rounded-lg">
        <FiDownload /> Download QR
      </button>
    </Modal>
  );
};

export default QrModal;
