import { useEffect, useState } from "react";
import getIngredients from "../api/ingredientApi";

import "./AddIngredientModal.css";
import Tooltip from "../../../components/ui/Tooltip";
import Searchbar from "../../../components/ui/Searchbar";
import { addToBreakoutList } from "../api/breakoutListApi";

export default function AddIngredientModal({ onClose }) {
    const [ingredients, setIngredients] = useState([]);

    async function handleAddIngredient(ingredientId) {
        addToBreakoutList("ingredient", ingredientId)
            .then(success => {
                if (success) {
                    alert("Ingredient added to breakout list!");
                } else {
                    alert("Failed to add ingredient. Please try again.");
                }
            })
            .catch(error => {
                console.error("Error adding ingredient to breakout list:", error);
                alert("An error occurred. Please try again.");
            });
    }

    useEffect(() => {
        getIngredients(0, 8)
            .then(data => {
                setIngredients(data.content);
            })
            .catch(error => {
                console.error("Error fetching ingredients:", error);
            });
    }, []);

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button className="modal-close" onClick={onClose}>X</button>

                <h2>Add Ingredient to Breakout List</h2>

                <Searchbar placeholder="Search ingredients..." onSearch={query => console.log("Search for:", query)} />

                {ingredients.length > 0 && (
                    <ul>
                        {ingredients.map(ingredient => (
                            <div className="ingredient-card" key={ingredient.id}>
                                <p>{ingredient.name}</p>
                                <Tooltip message={`Add to breakout list`}>
                                    <button onClick={() => handleAddIngredient(ingredient.id)}>+</button>
                                </Tooltip>
                            </div>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
}
