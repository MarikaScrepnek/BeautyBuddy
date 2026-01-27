import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { FaSearch } from 'react-icons/fa';

import AuthModal from "../components/AuthModal";
import { addToWishlist } from "../api/wishlistApi";

import './ProductDetails.css';

export default function ProductDetails() {
  const { productId } = useParams();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedShade, setSelectedShade] = useState(null);
  const [ingredientsOpen, setIngredientsOpen] = useState(false);

  const [showLoginModal, setShowLoginModal] = useState(false);

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

    const isLoggedIn = () => {
    return Boolean(localStorage.getItem("user"));
  }

  const handleAddToWishlist = async () => {
    if (!isLoggedIn()) {
        setShowLoginModal(true);
        return;
    }
    const success = await addToWishlist(data.id, selectedShade?.id);
    if (success) {
        alert("Added to wishlist!");
    } else {
        alert("Failed to add to wishlist.");
    }
  };

    const handleAddToRoutine = async () => {
    if (!isLoggedIn()) {
        setShowLoginModal(true);
        return;
    }
    // Logic to add to routine
    };

  if (loading) return <p className="loading">Loading product details...</p>;
  if (!data) return <p className="error">Product not found</p>;

  return (
    <div className="product-details-container">
        {showLoginModal && (
        <AuthModal
            onClose={() => setShowLoginModal(false)}
            onLoginSuccess={() => {
                setShowLoginModal(false);
            }}
        />
        )}
        <div className="product-card">
            {/* Header: Name + Brand + Category */}
            <div className="product-header">
                <h1 className="product-name">{data.name}</h1>
                <p className="product-brand">by {data.brand.name}</p>
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
                        <p htmlFor={`shade-${data.id}`}>Shade:</p>
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
                        <div className="action-icon" onClick={handleAddToWishlist}>
                            <span className="icon">♥</span>
                            <span className="tooltip">Add to Wishlist</span>
                        </div>

                        <div className="action-icon" onClick={handleAddToRoutine}>
                            <span className="icon">+</span>
                            <span className="tooltip">Add to Makeup Routine</span>
                        </div>
                    </div>

                    <p
                        className="product-link"
                        onClick={() =>
                            window.open(
                            selectedShade?.productLink ?? data.product_link
                            )
                        }
                        >
                        view product on site
                    </p>
                    
                </div>
            </div>

            {/* Ingredients */}
            <section className="ingredients-section">
                <h2 className="ingredient-dropdown-header">
                    Ingredients{" "}
                    <span
                        className="ingredients-toggle"
                        onClick={() => setIngredientsOpen(!ingredientsOpen)}
                    >
                        {ingredientsOpen ? "▲" : "▼"}
                    </span>
                </h2>

                {ingredientsOpen && (
                    <>
                    <div className="tags">
                        {data.ingredients.map((i, idx) => (
                        <span key={idx} className="tag">{i.name}</span>
                        ))}
                    </div>

                    <div className="may-contain-list">
                        <div className="may-contain-header">May Contain</div>
                        {data.mayContainIngredients.map((i, idx) => (
                        <span key={i.name}>
                            {i.name}
                            {idx < data.mayContainIngredients.length - 1 && ", "}
                        </span>
                        ))}
                    </div>
                    </>
                )}
            </section>
                

            {/* Reviews / Questions */}
            <section className="reviews-and-questions-section">
                <section className="reviews-and-questions-header-section">
                        <span className="reviews-and-questions-header">Reviews</span>
                        <span className="reviews-and-questions-header">Q&A</span>
                </section>

                <div className="reviews-search-container">
                    <input
                        type="text"
                        className="reviews-search-bar"
                        placeholder="Search reviews and questions..."
                    />
        
                    <button
                        type="button"
                        className="reviews-search-button"
                        aria-label="Search"
                    >
                        <FaSearch />
                    </button>
                </div>
            </section>
        </div>
    </div>
  );
}
