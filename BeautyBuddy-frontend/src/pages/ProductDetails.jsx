import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";

import './ProductDetails.css';

export default function ProductDetails() {
  const { productId } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  const navigate = useNavigate();

  useEffect(() => {
    fetch(`http://localhost:8080/api/products/${productId}`)
      .then(res => res.json())
      .then(setData)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading) return <p className="loading">Loading product details...</p>;
  if (!data) return <p className="error">Product not found</p>;

  return (
    <div className="product-details-container">
        <div className="product-card">
            {/* Header: Name + Brand + Category */}
            <div className="product-header">
                <h1 className="product-name">{data.name}</h1>
                <div className="product-brand-and-link">
                    <span className="product-brand">by {data.brand.name}</span>
                    <span className="product-link" onClick={() => window.open(data.product_link)}>view product on site</span>
                </div>
            </div>

            {/* Main section: Image on left, Price + Rating on right */}
            <div className="product-main">
            <img src={data.image_link} alt={data.name} className="product-image" />

            <div className="product-meta">
                <p className="price">
                <strong>Price:</strong> {data.price ? `$${data.price}` : "N/A"}
                </p>
                <p className="rating">
                <strong>Rating:</strong> {data.rating ? `${data.rating}/5` : "No rating yet"}
                </p>
            </div>
            </div>

            {/* Ingredients */}
            <section className="ingredients-section">
            <h2>Ingredients</h2>
            <div className="tags">
                {data.ingredients.map((i, idx) => (
                <span key={idx} className="tag">{i.name}</span>
                ))}
            </div>

            <h2>May Contain</h2>
            <div className="tags">
                {data.mayContainIngredients.map((i, idx) => (
                <span key={idx} className="tag may-contain">{i.name}</span>
                ))}
            </div>
            </section>

            {/* Reviews / Questions */}
            <section className="reviews-section">
            <h2>Reviews</h2>
            </section>

            <section className="questions-section">
            <h2>Q&A</h2>
            </section>
        </div>
    </div>
  );
}
