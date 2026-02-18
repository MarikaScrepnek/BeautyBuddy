import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";
import { FaSearch } from 'react-icons/fa';

import AuthModal from "../components/AuthModal";
import { addToWishlist, removeFromWishlist, getWishlist } from "../api/wishlistApi";

import AskQuestionModal from "../components/AskQuestionModal";
import SubmitReviewModal from "../components/SubmitReviewModal";
import ReviewList from "../components/ReviewList";

import './ProductDetails.css';
import { getCurrentUser } from "../api/authApi";
import Toast from "../components/Toast";

import {submitReview, editReview} from "../api/reviewApi";

export default function ProductDetails() {
  const { productId } = useParams();

  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [selectedShade, setSelectedShade] = useState(null);

  const [ingredientsOpen, setIngredientsOpen] = useState(false);
  const [reviewsOpen, setReviewsOpen] = useState(true);
  const [questionsOpen, setQuestionsOpen] = useState(true);

  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [wishlistItems, setWishlistItems] = useState([]);
  const [isTogglingWishlist, setIsTogglingWishlist] = useState(false);

  const [showLoginModal, setShowLoginModal] = useState(false);

  const [toast, setToast] = useState(null);

  const [questions, setQuestions] = useState([]);
  const [askOpen, setAskOpen] = useState(false);

  const [reviewOpen, setReviewOpen] = useState(false);
        const [reviewRefreshKey, setReviewRefreshKey] = useState(0);
    const [editingReview, setEditingReview] = useState(null);

  const showToast = (message, type = "success") => {
    setToast({ message, type});
  }

  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
  }, []);

    const loadWishlist = async () => {
        try {
            const items = await getWishlist();
            setWishlistItems(items);
        } catch {
            setWishlistItems([]);
        }
    };

    useEffect(() => {
        const handleAuthLogin = () => {
            getCurrentUser()
                .then(() => {
                    setIsLoggedIn(true);
                    loadWishlist();
                })
                .catch(() => setIsLoggedIn(false));
        };

        const handleAuthLogout = () => {
            setIsLoggedIn(false);
            setWishlistItems([]);
        };

        window.addEventListener("auth:login", handleAuthLogin);
        window.addEventListener("auth:logout", handleAuthLogout);
        return () => {
            window.removeEventListener("auth:login", handleAuthLogin);
            window.removeEventListener("auth:logout", handleAuthLogout);
        };
    }, []);

    useEffect(() => {
        if (isLoggedIn) {
            loadWishlist();
        } else {
            setWishlistItems([]);
        }
    }, [isLoggedIn]);

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

    const isInWishlist = Boolean(
        data && wishlistItems.some((item) => {
            if (item.productId !== data.id) return false;
            const shadeName = selectedShade?.shadeName ?? null;
            return (item.shadeName ?? null) === shadeName;
        })
    );

    const handleToggleWishlist = async () => {
        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        if (!data) return;

        const shadeName = selectedShade?.shadeName ?? null;
        const productKeyMatches = (item) => item.productId === data.id && (item.shadeName ?? null) === shadeName;

        if (isTogglingWishlist) return; // Prevent multiple clicks
        setIsTogglingWishlist(true);

        if (isInWishlist) {
            setWishlistItems((items) => items.filter(item => !productKeyMatches(item)));
            const success = await removeFromWishlist(data.id, shadeName);
            if (success) {
                showToast("Removed from wishlist", "info");
            } else {
                showToast("Failed to remove from wishlist", "error");
                await loadWishlist();
            }
        } else {
            setWishlistItems((items) => [...items, { productId: data.id, shadeName }]);
            const success = await addToWishlist(data.id, shadeName);
            if (success) {
                showToast("Added to wishlist", "success");
            } else {
                showToast("Failed to add to wishlist", "error");
                await loadWishlist();
            }
        }
        setIsTogglingWishlist(false);
    };

    const handleAddToRoutine = async () => {
        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        // Logic to add to routine
    };

    const handleAskQuestion = async () => {
        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        setAskOpen(true);
    }

    const handleWriteReview = async () => {
        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        setEditingReview(null);
        setReviewOpen(true);
    }

    const handleEditReview = (review) => {
        setEditingReview(review);
        setReviewOpen(true);
    }

  if (loading) return <p className="loading">Loading product details...</p>;
  if (!data) return <p className="error">Product not found</p>;

  return (
    <div className="product-details-container">
        {toast && (
            <Toast
                message={toast.message}
                type={toast.type}
                duration={750}
                onClose={() => setToast(null)}
            />
        )}
        {showLoginModal && (
                <AuthModal
                    onClose={() => setShowLoginModal(false)}
                    onLoginSuccess={() => {
                        setIsLoggedIn(true);
                        loadWishlist();
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
                    <p className="rating">
                        <span>Rating:</span> {data.rating ? `${data.rating}/5` : "Be the first to review!"}
                    </p>
                    <p className="price">
                        <span>Price:</span> {data.price ? `$${data.price}` : "N/A"}
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
                        <div className="action-icon" onClick={handleToggleWishlist}>
                            <span className={`icon ${isInWishlist ? "icon--active" : ""}`}>♥</span>
                            <span className="tooltip">
                              {isInWishlist ? "Remove from Wishlist" : "Add to Wishlist"}
                            </span>
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
                

            {/* Reviews / Questions */}
            <section className="questions-section">
                <h2 className="questions-dropdown-header">
                    Q&A{" "}
                    <span
                        className="questions-toggle"
                        onClick={() => setQuestionsOpen(!questionsOpen)}
                    >
                        {questionsOpen ? "▲" : "▼"}
                    </span>
                </h2>
                    {questionsOpen && (
                        <div className="questions-content">
                            <AskQuestionModal
                                isOpen={askOpen}
                                onClose={() => setAskOpen(false)}
                                onSubmit={async (payload) => {
                                    // call your API here
                                    // await createQuestion(productId, payload);
                                    setAskOpen(false);
                                }}
                                productName={data?.name}
                            />
                            <button
                                className="ask-question-button"
                                onClick={() => handleAskQuestion()}
                                > Ask a question about this product
                            </button>


                            {questions.length === 0 ? (
                                <div className="questions-empty-state">
                                    <p>
                                        No questions have been asked about this product yet.
                                    </p>
                                </div>
                            ) : (
                                <div>
                                    {questions.map(q => (
                                        <QuestionCard key={q.id} question={q} />
                                    ))}
                                </div>
                            )}
                        </div>
                    )}
            </section>

            <section className="reviews-section">
                <h2 className="reviews-dropdown-header">
                    Reviews{" "}
                    <span
                        className="reviews-toggle"
                        onClick={() => setReviewsOpen(!reviewsOpen)}
                    >
                        {reviewsOpen ? "▲" : "▼"}
                    </span>
                </h2>
                {reviewsOpen && (
                    <div className="reviews-content">
                        <SubmitReviewModal
                                isOpen={reviewOpen}
                                onClose={() => {
                                    setReviewOpen(false);
                                    setEditingReview(null);
                                }}
                                onSubmit={async (payload) => {
                                    const reviewId = editingReview?.reviewId ?? editingReview?.id;
                                    const isEditing = Boolean(reviewId);
                                    const success = isEditing
                                        ? await editReview(
                                            reviewId,
                                            payload.shadeName,
                                            payload.rating,
                                            payload.title,
                                            payload.text,
                                            payload.images
                                        )
                                        : await submitReview(
                                            productId,
                                            payload.shadeName,
                                            payload.rating,
                                            payload.title,
                                            payload.text,
                                            payload.images
                                        );

                                    if (success) {
                                        showToast(
                                            isEditing
                                                ? "Review updated successfully!"
                                                : "Review submitted successfully!",
                                            "success"
                                        );
                                        setReviewOpen(false);
                                        setEditingReview(null);
                                        setReviewRefreshKey((value) => value + 1);
                                    } else {
                                        showToast(
                                            isEditing
                                                ? "Failed to update review. Please try again."
                                                : "Failed to submit review. Please try again.",
                                            "error"
                                        );
                                    }
                                }}
                                productName={data?.name}
                                shades={data?.shades}
                                selectedShadeName={selectedShade?.shadeName ?? ""}
                                initialValues={
                                    editingReview
                                        ? {
                                            shadeName: editingReview?.shadeName ?? "",
                                            rating: Number(editingReview?.rating ?? 0),
                                            title: editingReview?.reviewTitle ?? "",
                                            text: editingReview?.reviewText ?? "",
                                            images: editingReview?.imageLinks ?? []
                                        }
                                        : null
                                }
                                modalTitle={
                                    editingReview ? "Edit your review" : "Submit a review"
                                }
                                submitLabel={editingReview ? "Save changes" : "Submit review"}
                            />
                        <button
                            className="submit-review-button"
                            onClick={() => handleWriteReview()}
                            > Write a review for this product
                        </button>
                        <ReviewList
                            productId={productId}
                            refreshKey={reviewRefreshKey}
                            onEditReview={handleEditReview}
                        />
                    </div>
                )}
            </section>

        </div>
    </div>
  );
}
