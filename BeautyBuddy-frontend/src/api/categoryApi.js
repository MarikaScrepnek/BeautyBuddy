export async function getCategories() {
    const res = await fetch("http://localhost:8080/api/categories");
    return res.json();
  }