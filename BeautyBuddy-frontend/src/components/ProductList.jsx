import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import './ProductList.css';

export default function ProductList({ searchQuery }) {
  const [products, setProducts] = useState([]);

  const navigate = useNavigate();

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
        <div className="product-card-home" key={p.id} onClick={() => navigate(`/${p.id}`)}>
          {p.image_link && (
            <img src={p.image_link} alt={p.name} className="product-image-home" />
          )}
          <h3>{p.name} by {p.brand?.name}</h3>
          <p className="shade-count">
            {p.shades?.length > 1 ? `${p.shades.length} shades` : "\u00A0"}
          </p>
          <p>Rating: {p.rating ? `${p.rating}/5` : "N/A"}</p>
          <p>Price: {p.price ? `$${p.price}` : "N/A"}</p>
        </div>
      ))}
    </div>
  );
}