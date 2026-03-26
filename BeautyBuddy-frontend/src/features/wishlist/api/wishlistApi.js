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

export async function getWishlist({ sort, category, priceRange, query } = {}) {
  const params = new URLSearchParams();
  if (sort) params.set("sort", sort);
  if (category) params.set("category", category);
  if (priceRange) params.set("priceRange", priceRange);
  if (query) params.set("query", query);

  const qs = params.toString();
  const url = qs
    ? `http://localhost:8080/api/wishlist?${qs}`
    : "http://localhost:8080/api/wishlist";

  const res = await fetch(url, {
    method: "GET",
    credentials: "include",
  });
  if (!res.ok) throw new Error("Failed to fetch wishlist");
  return res.json();
}