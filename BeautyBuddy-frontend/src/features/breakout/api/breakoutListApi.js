import { API_BASE_URL } from '../../../config/apiBase';

export async function addToBreakoutList(type, id) {
    if (type === "ingredient") {
        const response = await fetch(`${API_BASE_URL}/api/breakout-list/add`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ ingredientId: id, productId: null })
    });
    return response.ok;
    }
    else if (type === "product") {
        const response = await fetch(`${API_BASE_URL}/api/breakout-list/add`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId: id, ingredientId: null })
    });
    return response.ok;
    }
}

export async function getBreakoutListProducts() {
    const response = await fetch(`${API_BASE_URL}/api/breakout-list/products`, {
        method: "GET",
        credentials: "include"
    });
    return response.json();
}

export async function getBreakoutListIngredients() {
    const response = await fetch(`${API_BASE_URL}/api/breakout-list/ingredients`, {
        method: "GET",
        credentials: "include"
    });
    return response.json();
}

export async function removeFromBreakoutList(type, id) {
    if (type === "ingredient") {
        const response = await fetch(`${API_BASE_URL}/api/breakout-list/remove`, {
        method: "DELETE",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"        },
        body: JSON.stringify({ ingredientId: id, productId: null })
    });
    return response.ok;
    }
    else if (type === "product") {
        const response = await fetch(`${API_BASE_URL}/api/breakout-list/remove`, {
        method: "DELETE",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"        },
        body: JSON.stringify({ productId: id, ingredientId: null })
    });
    return response.ok;
    }
}