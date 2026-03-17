export async function addToBreakoutList(type, id) {
    if (type === "ingredient") {
        const response = await fetch(`http://localhost:8080/api/breakout-list/add`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ ingredientId: id, productId: null })
    });
    return response.json();
    }
    else if (type === "product") {
        const response = await fetch(`http://localhost:8080/api/breakout-list/add`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ productId: id, ingredientId: null })
    });
    return response.json();
    }
}