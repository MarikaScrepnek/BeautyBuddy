export async function getBrands() {
    const res = await fetch("http://localhost:8080/api/brands");
    return res.json();
  }