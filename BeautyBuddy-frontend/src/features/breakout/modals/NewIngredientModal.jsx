import { useEffect } from "react";

export default function NewIngredientModal({ onClose }) {
    useEffect(() => {
        const handleEsc = (event) => {
            if (event.key === "Escape") {
                onClose();
            }
        };
        window.addEventListener("keydown", handleEsc);
        return () => {
            window.removeEventListener("keydown", handleEsc);
        }
    }, [onClose]);

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button className="modal-close" onClick={onClose}>X</button>
                <h2>Request New Ingredient</h2>
                <input type="text" placeholder="Ingredient name" />
                <p>
                    We will review your request and add the ingredient if it meets our criteria. 
                    If we're unable to complete your request, we will contact you with a reason as to why.
                </p>
                <button className="submit-request">Submit Request</button>
            </div>
        </div>
    );
}