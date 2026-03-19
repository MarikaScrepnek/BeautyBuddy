import { useEffect } from "react";
import getIngredients from "../api/ingredientApi";

export default function AddIngredientModal({ onClose }) {

    useEffect(() => {
        getIngredients()
            .then(data => {
                console.log("Ingredients:", data);
            })
            .catch(error => {
                console.error("Error fetching ingredients:", error);
            });
    }, []);

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2>Add Ingredient</h2>
                <p>This feature is coming soon! In the meantime, you can add products to your breakout list and view their ingredients.</p>
                <button onClick={onClose}>Close</button>
            </div>
        </div>
    );
}
