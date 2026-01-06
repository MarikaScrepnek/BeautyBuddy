export async function getProducts() {
    const res = await fetch("http://localhost:8080/api/products");
    return res.json();
  }