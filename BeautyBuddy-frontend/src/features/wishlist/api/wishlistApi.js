export async function addToWishlist(productId, shadeName) {
    const res = await fetch("http://localhost:8080/api/wishlist/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ productId, shadeName }),
    });
    return res.ok;
}

export async function removeFromWishlist(productId, shadeName) {
    const res = await fetch("http://localhost:8080/api/wishlist/remove", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify({ productId, shadeName }),
    });
    return res.ok;
}

export async function getWishlist() {
    const res = await fetch("http://localhost:8080/api/wishlist", {
        method: "GET",
        credentials: "include",
    });
    if (!res.ok) {
        throw new Error("Failed to fetch wishlist");
    }
    return res.json();
}

export async function searchWishlist(query) {
    const res = await fetch(`http://localhost:8080/api/wishlist/search?query=${encodeURIComponent(query)}`, {
        method: "GET",
        credentials: "include",
    });
    if (!res.ok) {
        throw new Error("Failed to search wishlist");
    }
    return res.json();
}