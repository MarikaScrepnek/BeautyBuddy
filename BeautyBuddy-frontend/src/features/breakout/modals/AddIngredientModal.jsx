import { useEffect, useState } from "react";
import getIngredients from "../api/ingredientApi";
import { FaLeaf } from "react-icons/fa";

import "./AddIngredientModal.css";
import Tooltip from "../../../components/ui/Tooltip";
import Searchbar from "../../../components/ui/Searchbar";
import { addToBreakoutList } from "../api/breakoutListApi";

import NewIngredientModal from "./NewIngredientModal";

export default function AddIngredientModal({ onClose, setNewIngredientModalOpen }) {
    const [ingredients, setIngredients] = useState([]);
    const[searchQuery, setSearchQuery] = useState("");
    const[addIngredientModalOpen, setAddIngredientModalOpen] = useState(false);
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [hasNextPage, setHasNextPage] = useState(false);
    const pageSize = 8;

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
        getIngredients(currentPage, pageSize, searchQuery)
            .then(data => {
                const fetchedIngredients = Array.isArray(data?.content)
                    ? data.content
                    : Array.isArray(data)
                        ? data
                        : [];

                setIngredients(fetchedIngredients);

                const resolvedTotalPages = typeof data?.totalPages === "number" && data.totalPages > 0
                    ? data.totalPages
                    : typeof data?.totalElements === "number"
                        ? Math.ceil(data.totalElements / pageSize)
                        : 0;

                const resolvedHasNext = typeof data?.last === "boolean"
                    ? !data.last
                    : resolvedTotalPages > 0
                        ? currentPage < resolvedTotalPages - 1
                        : fetchedIngredients.length === pageSize;

                setTotalPages(resolvedTotalPages);
                setHasNextPage(resolvedHasNext);
            })
            .catch(error => {
                console.error("Error fetching ingredients:", error);
                setIngredients([]);
                setTotalPages(0);
                setHasNextPage(false);
            });
    }, [currentPage, searchQuery]);

    function handleSearch(query) {
        setSearchQuery(query);
        setCurrentPage(0);
    }

    function handlePreviousPage() {
        setCurrentPage((prev) => Math.max(prev - 1, 0));
    }

    function handleNextPage() {
        setCurrentPage((prev) => (hasNextPage ? prev + 1 : prev));
    }

    return (
        <div className="add-ingredient-overlay" onClick={onClose}>
            <div className="add-ingredient-modal" onClick={(e) => e.stopPropagation()}>
                <button className="add-ingredient-close" onClick={onClose} aria-label="Close modal">x</button>

                <div className="add-ingredient-header">
                    <div className="add-ingredient-icon-wrap">
                        <FaLeaf className="add-ingredient-icon" />
                    </div>
                    <h2>Add Ingredient to Breakout List</h2>
                    <p>Search and add ingredients you want to avoid.</p>
                </div>

                <div className="add-ingredient-search-wrap">
                    <Searchbar 
                        placeholder="Search ingredients..." 
                        onSearch={handleSearch}
                    />
                </div>

                {ingredients.length > 0 ? (
                    <ul className="add-ingredient-list">
                        {ingredients.map(ingredient => (
                            <li className="add-ingredient-item" key={ingredient.id}>
                                <p>{ingredient.name}</p>
                                <Tooltip message="Add to breakout list">
                                    <button className="add-ingredient-item-button" onClick={() => handleAddIngredient(ingredient.id)}>Add</button>
                                </Tooltip>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <div className="add-ingredient-empty">
                        <p>No ingredients found.</p>
                        <Tooltip message="Request new ingredient">
                            <button className="add-ingredient-empty-button" onClick={() => {setNewIngredientModalOpen(true); onClose()}}>
                                Request It
                            </button>
                        </Tooltip>
                    </div>
                )}

                {(currentPage > 0 || hasNextPage || totalPages > 1) && (
                    <div className="add-ingredient-pagination">
                        <button
                            type="button"
                            className="add-ingredient-page-btn"
                            onClick={handlePreviousPage}
                            disabled={currentPage === 0}
                        >
                            Previous
                        </button>
                        <span className="add-ingredient-page-label">
                            {totalPages > 0 ? `Page ${currentPage + 1} of ${totalPages}` : `Page ${currentPage + 1}`}
                        </span>
                        {hasNextPage && (
                            <button
                                type="button"
                                className="add-ingredient-page-btn"
                                onClick={handleNextPage}
                            >
                                Next
                            </button>
                        )}
                    </div>
                )}

            </div>
            {addIngredientModalOpen && (
                <NewIngredientModal 
                    onClose={() => setAddIngredientModalOpen(false)}
                />
            )}
        </div>
    );
}
