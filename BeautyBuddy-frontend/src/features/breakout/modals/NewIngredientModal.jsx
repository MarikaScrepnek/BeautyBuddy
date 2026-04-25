import { useEffect } from "react";
import { FaFlask } from "react-icons/fa";

import "./NewIngredientModal.css";

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
        <div className="new-ingredient-overlay" onClick={onClose}>
            <div className="new-ingredient-modal" onClick={(e) => e.stopPropagation()}>
                <button className="new-ingredient-close" onClick={onClose} aria-label="Close modal">x</button>

                <div className="new-ingredient-header">
                    <div className="new-ingredient-icon-wrap">
                        <FaFlask className="new-ingredient-icon" />
                    </div>
                    <h2>Request New Ingredient</h2>
                    <p>Missing an ingredient in our database? Send us a request and we will review it.</p>
                </div>

                <div className="new-ingredient-form">
                    <label>
                        Ingredient name
                        <input type="text" placeholder="e.g. Niacinamide" />
                    </label>

                    <label>
                        Additional details (optional)
                        <textarea rows={4} placeholder="Why this ingredient should be added, aliases, product examples..." />
                    </label>
                </div>

                <p className="new-ingredient-note">
                    We will review your request and add the ingredient if it meets our criteria. If we cannot complete the request,
                    we will contact you with more details.
                </p>

                <button className="new-ingredient-submit">Submit Request</button>
            </div>
        </div>
    );
}