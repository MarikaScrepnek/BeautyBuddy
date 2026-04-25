import { getBreakoutListIngredients, getBreakoutListProducts, removeFromBreakoutList } from "../api/breakoutListApi";
import { GiTrashCan } from "react-icons/gi";
import { FaFlask, FaBoxOpen } from "react-icons/fa";
import "./BreakoutList.css";

import { useEffect, useState } from "react";
import React from "react";
import Tooltip from "../../../components/ui/Tooltip";
import AddIngredientModal from "../modals/AddIngredientModal";

import NewIngredientModal from "../modals/NewIngredientModal";

export default function BreakoutList() {
    const [ingredientsSelected, setIngredientsSelected] = useState(true);
    const [productsSelected, setProductsSelected] = useState(false);
    const [breakoutListProducts, setBreakoutListProducts] = useState([]);
    const [breakoutListIngredients, setBreakoutListIngredients] = useState([]);
    const [addIngredientModalOpen, setAddIngredientModalOpen] = useState(false);
    const [newIngredientModalOpen, setNewIngredientModalOpen] = useState(false);

    useEffect(() => {
        getBreakoutListProducts()
            .then(data => {
                console.log("Breakout List:", data);
                setBreakoutListProducts(data);
            })
            .catch(error => {
                console.error("Error fetching breakout list:", error);
            });
        getBreakoutListIngredients()
            .then(data => {
                setBreakoutListIngredients(data);
            })
            .catch(error => {
                console.error("Error fetching breakout list ingredients:", error);
            });
    }, []);

    async function handleDeleteProduct(productId) {
        removeFromBreakoutList("product", productId)
            .then(() => {
                setBreakoutListProducts(prevProducts => prevProducts.filter(product => product.productId !== productId));
            })
            .catch(error => {
                console.error("Error removing item from breakout list:", error);
            });
    }

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

            {ingredientsSelected && (
                <>
                {breakoutListIngredients.length === 0 ? (
                    <div className="breakout-empty-state">
                        <div className="breakout-empty-icon-wrap">
                            <FaFlask className="breakout-empty-icon" />
                        </div>
                        <h3>Your ingredient list is empty</h3>
                        <p>Add ingredients you want to avoid.</p>
                        <button className="breakout-empty-button" onClick={() => setAddIngredientModalOpen(true)}>
                            Add Ingredients
                        </button>
                    </div>
                ) : (
                    <ul>
                        {breakoutListIngredients.map((ingredient) => (
                            <div 
                            className="ingredient-card"
                            style={{width: "450px"}}
                            key={ingredient.id}
                            >
                                <span>{ingredient.name}</span>
                            </div>
                        ))}
                    </ul>
                )}
                </>
            )}

            {productsSelected && (
                <>
                {breakoutListProducts.length === 0 ? (
                    <div className="breakout-empty-state">
                        <div className="breakout-empty-icon-wrap breakout-empty-icon-wrap--products">
                            <FaBoxOpen className="breakout-empty-icon" />
                        </div>
                        <h3>Your product list is empty</h3>
                        <p>Mark products that have broken you out in the past.</p>
                        <p className="breakout-empty-note">Use the <span className="breakout-empty-note-exclamation">!</span> button on product pages to add items to this list.</p>
                    </div>
                ) : (
                <ul>
                    {breakoutListProducts.map((product, index) => (
                        <React.Fragment key={product.id}>
                            <li
                            className="routine-item"
                            onClick={() => {
                                window.open(`/products/${product.productId}`, '_blank')
                            }}
                        >
                            <div className="delete-item-button" onClick={(e) => {e.stopPropagation(); handleDeleteProduct(product.productId);}}>
                                <GiTrashCan />
                            </div>
                            <img draggable="false" src={product.productImageUrl} alt={product.productName} className="routine-item-img" />
                            <div className="routine-item-main">
                                <div className='routine-item-info-row'>
                                    <div className="routine-item-info">
                                        <p className="routine-item-name">{product.productName}</p>
                                        <div className='routine-item-meta'>
                                            <span>{product.productBrand}</span>
                                        </div>
                                        {product.ingredients.length > 0 && (
                                            <div className="routine-item-ingredients">
                                                {product.ingredients.map(ingredient => (
                                                    <span key={ingredient.name} className="routine-item-ingredient">
                                                        {ingredient.name}
                                                        {index < product.ingredients.length - 1 && " • "}
                                                    </span>
                                                ))}
                                            </div>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </li>
                        </React.Fragment>
                    ))}
                </ul>
                )}
                </>
            )}
        </div>
        {addIngredientModalOpen && (
            <AddIngredientModal onClose={() => setAddIngredientModalOpen(false)} setNewIngredientModalOpen={setNewIngredientModalOpen} />
        )}
        {newIngredientModalOpen && (
            <NewIngredientModal onClose={() => setNewIngredientModalOpen(false)} />
        )}
    </div> 
    );
}