import { useEffect } from 'react';
import './RequestModal.css';

export default function RequestModal({ onClose }) {
    useEffect(() => {
        const handleEsc = (e) => {
            if (e.key === "Escape") onClose();
        };

        window.addEventListener("keydown", handleEsc);

        return () => {
            window.removeEventListener("keydown", handleEsc);
        };
    }, [onClose]);

      return (
        <div className="modal-overlay">
            <div className="modal" onClick={(e) => e.stopPropagation()}>
                <h2>Request a Product</h2>
                <input type="text" placeholder="Product Name" />
                <input type="text" placeholder="Brand" />
                <input type="text" placeholder="Additional Details (optional)" />
                <button className="modal-request-button">Submit Request</button>
                <button
                    className="modal-close"
                    onClick={() => onClose()}
                >
                    ✕
                </button>
                <h2 className='request-product-info'>
                    Our team will review your request and add the product within the next business day. 
                    We will notify you once it's available on BeautyBuddy.
                    If we have trouble finding the product, we'll contact you via email. 
                    Thank you for helping us improve BeautyBuddy! We appreciate your input.
                </h2>
            </div>
        </div>
      );
}