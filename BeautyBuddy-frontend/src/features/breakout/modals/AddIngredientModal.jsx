export default function AddIngredientModal({ onClose }) {

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
