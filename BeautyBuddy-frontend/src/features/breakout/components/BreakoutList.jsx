import "./BreakoutList.css";

import { useState } from "react";

export default function BreakoutList() {
    const [ingredientsSelected, setIngredientsSelected] = useState(false);
    const [productsSelected, setProductsSelected] = useState(true);

    return (
    <div className="card-container">
        <div className="card-header">
            <h2>Breakout List</h2>
            <div className="card-buttons">
                <button 
                    className={ingredientsSelected ? "selected" : ""}
                    onClick={() => {setIngredientsSelected(true); setProductsSelected(false)}}>
                    Ingredients
                </button>
                <button 
                    className={productsSelected ? "selected" : ""}
                    onClick={() => {setProductsSelected(true); setIngredientsSelected(false)}}>
                    Products
                </button>
            </div>
        </div>
        <div className="card-body">

            {ingredientsSelected && <p>Ingredients breakout list will be displayed here.</p>}

            {productsSelected && <p>Products breakout list will be displayed here.</p>}
        </div>
    </div> 
    );
}