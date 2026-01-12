import { useEffect, useState } from "react";

import './ProductList.css';

export default function ProductList({ searchQuery }) {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    const query = searchQuery || "";
    fetch(`http://localhost:8080/api/products/search?q=${query}`)
      .then(res => res.json())
      .then(setProducts)
      .catch(console.error);
  }, [searchQuery]);

  return (
    <div className="product-grid">
      {products.map(p => (
        <div className="product-card" key={p.product_id}>
          {p.image_link && (
            <img src={p.image_link} alt={p.name} className="product-image" />
          )}
          <h3>{p.name} by {p.brand?.name}</h3>
          <p>Rating: {p.rating ? `${p.rating}/5` : "N/A"}</p>
          <p>Price: {p.price ? `$${p.price}` : "N/A"}</p>
          <a href={p.product_link} target="_blank" rel="noopener noreferrer">
            View Product
          </a>
        </div>
      ))}
    </div>
  );
}