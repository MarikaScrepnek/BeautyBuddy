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
                <p>We will review your request and add the ingredient if it meets our criteria. We will contact you if there are any questions.</p>
            </div>
        </div>
    );
}