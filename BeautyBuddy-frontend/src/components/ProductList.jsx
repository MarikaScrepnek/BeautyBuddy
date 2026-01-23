import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import './ProductList.css';

export default function ProductList({ searchQuery, onLoadingChange }) {
  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    const query = searchQuery || "";
    setLoading(true);
    onLoadingChange?.(true);
    fetch(`http://localhost:8080/api/products/search?q=${query}`)
      .then(res => res.json())
      .then(data => {
          setProducts(data);
          setLoading(false);
          onLoadingChange?.(false);
      })
      .catch(err => {
        console.error(err);
        setLoading(false);
        onLoadingChange?.(false);
      });
  }, [searchQuery]);

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

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