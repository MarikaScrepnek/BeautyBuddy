import { getBreakoutListProducts, removeFromBreakoutList } from "../api/breakoutListApi";
import { GiTrashCan } from "react-icons/gi";
import "./BreakoutList.css";

import { useEffect, useState } from "react";
import React from "react";
import Tooltip from "../../../components/ui/Tooltip";
import AddIngredientModal from "../modals/AddIngredientModal";

export default function BreakoutList() {
    const [ingredientsSelected, setIngredientsSelected] = useState(true);
    const [productsSelected, setProductsSelected] = useState(false);
    const [breakoutListProducts, setBreakoutListProducts] = useState([]);
    const [addIngredientModalOpen, setAddIngredientModalOpen] = useState(false);

    useEffect(() => {
        getBreakoutListProducts()
            .then(data => {
                console.log("Breakout List:", data);
                setBreakoutListProducts(data);
            })
            .catch(error => {
                console.error("Error fetching breakout list:", error);
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
                <p>Ingredients will be displayed here in the future.</p>
                <Tooltip message="Add Ingredient">
                    <button className="add-ingredient" onClick={() => setAddIngredientModalOpen(true)}>
                        +
                    </button>
                </Tooltip>
                </>
            )}

            {productsSelected && (
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
        </div>
        {addIngredientModalOpen && (
            <AddIngredientModal onClose={() => setAddIngredientModalOpen(false)} />
        )}
    </div> 
    );
}