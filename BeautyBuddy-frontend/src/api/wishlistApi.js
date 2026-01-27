export async function addToWishlist(productId, shadeId) {
    const res = await fetch("http://localhost:8080/api/wishlist/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ productId, shadeId }),
        credentials: "include"
    });
    return res.ok;
}