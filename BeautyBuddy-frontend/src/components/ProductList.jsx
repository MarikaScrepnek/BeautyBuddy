import { useEffect, useState } from "react";
import { getProducts } from "../api/productApi";

import './ProductList.css';

export default function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    getProducts().then(setProducts);
  }, []);

  return (
    <div className="product-grid">
      {products.map(p => (
        <div className="product-card" key={p.product_id}>
          {p.image_link && (
            <img src={p.image_link} alt={p.name} className="product-image" />
          )}
          <h3>{p.name} - {p.brand?.name}</h3>
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