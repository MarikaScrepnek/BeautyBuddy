import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";

import './ProductDetails.css';

export default function ProductDetails() {
  const { productId } = useParams();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedShade, setSelectedShade] = useState(null);
  const [ingredientsOpen, setIngredientsOpen] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    fetch(`http://localhost:8080/api/products/${productId}`)
      .then(res => res.json())
      .then(setData)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [productId]);

    useEffect(() => {
    if (data?.shades?.length) {
        setSelectedShade(data.shades[0]);
    }
    }, [data]);

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
                    <span
                        className="product-link"
                        onClick={() =>
                            window.open(
                            selectedShade?.productLink ?? data.product_link
                            )
                        }
                        >
                        view product on site
                    </span>
                </div>
            </div>

            {/* Main section: Image on left, Price + Rating on right */}
            <div className="product-main">
                <img
                    src={selectedShade?.imageLink ?? data.image_link}
                    alt={data.name}
                    className="product-image"
                />

                <div className="product-meta">
                    <p className="price">
                        <span>Price:</span> {data.price ? `$${data.price}` : "N/A"}
                    </p>
                    <p className="rating">
                        <span>Rating:</span> {data.rating ? `${data.rating}/5` : "Be the first to review!"}
                    </p>

                    <div className="shade-selector">
                        <label htmlFor={`shade-${data.id}`}>Shade:</label>
                        <div className="custom-select">
                            <select
                            id={`shade-${data.id}`}
                            value={selectedShade?.shadeName ?? data.shades[0]?.shadeName}
                            onChange={(e) => {
                                const shade = data.shades.find(s => s.shadeName === e.target.value);
                                setSelectedShade(shade);
                            }}
                            >
                            {data.shades.map((s) => (
                                <option key={s.shadeName} value={s.shadeName}>
                                {s.shadeName}
                                </option>
                            ))}
                            </select>
                        </div>
                        </div>

                    <div className="product-actions">
                        <div className="action-icon">
                        <span className="icon">♥</span>
                        <span className="tooltip">Add to Wishlist</span>
                        </div>

                        <div className="action-icon">
                        <span className="icon">+</span>
                        <span className="tooltip">Add to Makeup Routine</span>
                        </div>
                    </div>
                    
                </div>
            </div>

            {/* Ingredients */}
            <section className="ingredients-section">
                <h2
                    onClick={() => setIngredientsOpen(!ingredientsOpen)}
                    className="ingredient-dropdown-header"
                >
                    Ingredients {ingredientsOpen ? "▲" : "▼"}
                </h2>

                {ingredientsOpen && (
                    <>
                    <div className="tags">
                        {data.ingredients.map((i, idx) => (
                        <span key={idx} className="tag">{i.name}</span>
                        ))}
                    </div>

                    <h3 className="may-contain-header">May Contain</h3>
                    <div className="tags">
                        {data.mayContainIngredients.map((i, idx) => (
                        <span key={idx} className="tag may-contain">{i.name}</span>
                        ))}
                    </div>
                    </>
                )}
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
