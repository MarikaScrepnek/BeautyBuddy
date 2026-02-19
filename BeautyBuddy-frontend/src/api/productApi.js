export async function getProducts() {
    const res = await fetch("http://localhost:8080/api/products");
    return res.json();
  }

export async function reportProduct(productId, reason) {
  const response = await fetch(`http://localhost:8080/api/products/${productId}/report`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ reason })
  });
  return response.ok;
}