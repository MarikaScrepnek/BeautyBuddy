export async function addToWishlist(productId, shadeName) {
    const email = localStorage.getItem("user");
    const res = await fetch("http://localhost:8080/api/wishlist/add", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, productId, shadeName }),
    });
    return res.ok;
}