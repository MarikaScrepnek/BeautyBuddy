import { useParams } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { FaSearch } from 'react-icons/fa';

import AuthModal from "../components/AuthModal";
import { addToWishlist, removeFromWishlist, getWishlist } from "../api/wishlistApi";

import AskQuestionModal from "../components/AskQuestionModal";
import QuestionCard from "../components/QuestionCard";
import SubmitReviewModal from "../components/SubmitReviewModal";
import ReviewList from "../components/ReviewList";
import ReportModal from "../components/ReportModal";
import AddToRoutineModal from "./productdetails/AddToRoutineModal";

import { searchReviews } from "../api/reviewApi";
import { searchQuestions } from "../api/qaApi";

import ReviewCard from "../components/ReviewCard";

import './ProductDetails.css';
import { getCurrentUser } from "../api/authApi";
import Toast from "../components/Toast";

import { deleteReview, getReviews, submitReview, editReview } from "../api/reviewApi";
import { reportProduct } from "../api/productApi";
import { getQuestionsForProduct, submitQuestion } from "../api/qaApi";

export default function ProductDetails() {

    // --- All hooks at the very top ---
    const { productId } = useParams();
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [selectedShade, setSelectedShade] = useState(null);
    const [ingredientsOpen, setIngredientsOpen] = useState(true);
    const [reviewsOpen, setReviewsOpen] = useState(true);
    const [questionsOpen, setQuestionsOpen] = useState(true);
    const [isLoggedIn, setIsLoggedIn] = useState(false);
    const [currentUser, setCurrentUser] = useState(null);
    const [wishlistItems, setWishlistItems] = useState([]);
    const [isTogglingWishlist, setIsTogglingWishlist] = useState(false);
    const [showLoginModal, setShowLoginModal] = useState(false);
    const [toast, setToast] = useState(null);
    const [questions, setQuestions] = useState([]);
    const [questionsPage, setQuestionsPage] = useState(0);
    const [questionsTotalPages, setQuestionsTotalPages] = useState(0);
    const [askOpen, setAskOpen] = useState(false);
    const [reviewOpen, setReviewOpen] = useState(false);
    const [reviewRefreshKey, setReviewRefreshKey] = useState(0);
    const [editingReview, setEditingReview] = useState(null);
    const [userReviewsByShade, setUserReviewsByShade] = useState({});
    const [shadeRatingValue, setShadeRatingValue] = useState(null);
    const [allReviews, setAllReviews] = useState([]);
    const [isShadeOpen, setIsShadeOpen] = useState(false);
    const shadeSelectRef = useRef(null);
    const [reportOpen, setReportOpen] = useState(false);
    const [quickRating, setQuickRating] = useState(0);
    const [quickHoverRating, setQuickHoverRating] = useState(null);
    const [isSubmittingQuickRating, setIsSubmittingQuickRating] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState({ reviews: [], questions: [] });
    const [exchangeRate, setExchangeRate] = useState(1);
    const [formattedPrice, setFormattedPrice] = useState("");

    const[addToRoutineOpen, setAddToRoutineOpen] = useState(false);

    // --- Currency localization logic below all hooks ---
    const localeCurrencyMap = {
        "en-US": "USD",
        "en-GB": "GBP",
        "en-CA": "CAD",
        "en-AU": "AUD",
        "fr-FR": "EUR",
        "de-DE": "EUR",
        "ja-JP": "JPY",
        "zh-CN": "CNY",
        // Add more mappings as needed
    };
    const userLocale = navigator.language || "en-US";
    const userCurrency = localeCurrencyMap[userLocale] || "USD";

    useEffect(() => {
        async function fetchRateAndFormat() {
            if (!data?.price) {
                setFormattedPrice("");
                return;
            }
            try {
                const rate = await getExchangeRate(userCurrency);
                setExchangeRate(rate);
                const priceCad = Number(data.price);
                const priceInUserCurrency = priceCad * (Number(rate) || 1);
                const formatted = new Intl.NumberFormat(userLocale, {
                    style: "currency",
                    currency: userCurrency,
                }).format(priceInUserCurrency);
                setFormattedPrice(formatted);
            } catch {
                setFormattedPrice("");
            }
        }
        fetchRateAndFormat();
    }, [data, userCurrency, userLocale]);

  const showToast = (message, type = "success") => {
    setToast({ message, type});
  }

  useEffect(() => {
    getCurrentUser()
            .then((user) => {
                setIsLoggedIn(true);
                setCurrentUser(user);
            })
            .catch(() => {
                setIsLoggedIn(false);
                setCurrentUser(null);
            });
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
                .then((user) => {
                    setIsLoggedIn(true);
                    setCurrentUser(user);
                    loadWishlist();
                })
                .catch(() => {
                    setIsLoggedIn(false);
                    setCurrentUser(null);
                });
        };

        const handleAuthLogout = () => {
            setIsLoggedIn(false);
            setCurrentUser(null);
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

    const loadProduct = () => {
        setLoading(true);
        fetch(`http://localhost:8080/api/products/${productId}`)
            .then(res => res.json())
            .then(setData)
            .catch(console.error)
            .finally(() => setLoading(false));
    };

    useEffect(() => {
        loadProduct();
    }, [productId]);

    const normalizeReviews = (response) => {
        if (Array.isArray(response?.content)) return response.content;
        if (Array.isArray(response)) return response;
        return [];
    };

    const loadAllReviews = async () => {
        if (!productId) {
            setAllReviews([]);
            return;
        }
        try {
            const response = await getReviews(productId, 0, 200);
            setAllReviews(normalizeReviews(response));
        } catch {
            setAllReviews([]);
        }
    };

    const loadQuestions = async (page = 0) => {
        try {
            const response = await getQuestionsForProduct(productId, page, 10);
            setQuestions(response?.content ?? []);
            setQuestionsPage(response?.number ?? page);
            setQuestionsTotalPages(response?.totalPages ?? 0);
        } catch {
            setQuestions([]);
            setQuestionsPage(page);
            setQuestionsTotalPages(0);
        }
    };

    useEffect(() => {
        loadQuestions(0);
    }, [productId]);

    useEffect(() => {
        loadAllReviews();
    }, [productId, reviewRefreshKey]);

    useEffect(() => {
        if (!currentUser?.username) {
            setUserReviewsByShade({});
            return;
        }
        const nextByShade = {};
        allReviews.forEach((review) => {
            if (review?.reviewerName !== currentUser.username) return;
            const key = review?.shadeName ?? "";
            nextByShade[key] = review;
        });
        setUserReviewsByShade(nextByShade);
    }, [allReviews, currentUser]);

    useEffect(() => {
        if (!selectedShade?.shadeName) {
            setShadeRatingValue(null);
            return;
        }
        const matching = allReviews.filter(
            (review) => review?.shadeName === selectedShade.shadeName
        );
        const total = matching.reduce(
            (sum, review) => sum + Number(review?.rating ?? 0),
            0
        );
        const average = matching.length ? total / matching.length : null;
        setShadeRatingValue(average);
    }, [allReviews, selectedShade?.shadeName]);

    useEffect(() => {
    if (data?.shades?.length) {
        setSelectedShade(data.shades[0]);
    }
    }, [data]);

    useEffect(() => {
        setQuickRating(0);
        setQuickHoverRating(null);
    }, [productId, selectedShade?.shadeName]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (!shadeSelectRef.current) return;
            if (shadeSelectRef.current.contains(event.target)) return;
            setIsShadeOpen(false);
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, []);

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
        setAddToRoutineOpen(true);
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

    const handleReportProduct = async (payload) => {
        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        const reason = payload?.reason?.trim();
        if (!reason) return;

        const success = await reportProduct(data.id, reason);
        if (success) {
            showToast("Report submitted. Thanks for the feedback.", "success");
            setReportOpen(false);
        } else {
            showToast("Unable to report product right now.", "error");
        }
    };

    const ratingValue = Number(data?.rating ?? 0);
    const hasRating = Boolean(data?.rating);
    const reviewCount = allReviews.length;
    let ratingText = "";
    if (hasRating) {
        ratingText = Number.isInteger(ratingValue)
            ? `${ratingValue}/5`
            : `${ratingValue.toFixed(1)}/5`;
    }
    const ratingAria = hasRating ? `Rated ${ratingValue.toFixed(1)} out of 5` : "No ratings yet";
    const displayedRating = quickHoverRating ?? ratingValue;
    const ratingDisplayText = ratingText;
    const ratingTooltipValue = quickHoverRating ?? quickRating;
    const ratingTooltipText = ratingTooltipValue
        ? `Submit ${ratingTooltipValue.toFixed(1)} star review for the shade ${selectedShade?.shadeName ? ` ${selectedShade.shadeName}` : ""}`
        : "";
    const ratingFill = (index) => {
        const delta = displayedRating - (index - 1);
        const pct = Math.min(1, Math.max(0, delta)) * 100;
        return `${pct}%`;
    };

    const getStarValueFromEvent = (e, starIndex1to5) => {
        const rect = e.currentTarget.getBoundingClientRect();
        const x = e.clientX - rect.left;
        const isHalf = x < rect.width / 2;
        return isHalf ? starIndex1to5 - 0.5 : starIndex1to5;
    };

    const handleQuickRating = async (e, index) => {
        const nextRating = getStarValueFromEvent(e, index);
        setQuickRating(nextRating);

        if (!isLoggedIn) {
            setShowLoginModal(true);
            return;
        }
        if (!data || isSubmittingQuickRating) return;

        setIsSubmittingQuickRating(true);

        const shadeName = selectedShade?.shadeName ?? null;
        const existingReview = userReviewsByShade?.[shadeName ?? ""] ?? null;
        const existingReviewId = existingReview?.reviewId ?? existingReview?.id ?? null;
        const existingTitle = existingReview?.reviewTitle ?? existingReview?.title ?? null;
        const existingText = existingReview?.reviewText ?? existingReview?.text ?? null;
        const existingImages = existingReview?.imageLinks ?? existingReview?.images ?? [];

        const success = existingReviewId
            ? await editReview(
                existingReviewId,
                shadeName,
                nextRating,
                existingTitle,
                existingText,
                existingImages
            )
            : await submitReview(
                productId,
                shadeName,
                nextRating,
                null,
                null,
                []
            );

        if (success) {
            showToast(
                existingReviewId
                    ? "Rating updated successfully!"
                    : "Rating submitted successfully!",
                "success"
            );
            loadProduct();
            setReviewRefreshKey((value) => value + 1);
            setQuickHoverRating(null);
        } else {
            showToast("Failed to submit rating. Please try again.", "error");
        }

        setIsSubmittingQuickRating(false);
    };

    useEffect(() => {
        if (!searchQuery) {
            setSearchResults({ reviews: [], questions: [] });
            return;
        }
        async function performSearch() {
            try {
                const [reviewsResult, questionsResult] = await Promise.all([
                    searchReviews(productId, searchQuery, 0, 5),
                    searchQuestions(productId, searchQuery, 0, 5)
                ]);
                setSearchResults({
                    reviews: reviewsResult.content || [],
                    questions: questionsResult.content || []
                });
            } catch (error) {
                setSearchResults({ reviews: [], questions: [] });
            }
        }
        performSearch();
    }, [searchQuery, productId]);

    function highlightText(text, term) {
        if (!term) return text;
        const regex = new RegExp(`(${term.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, "gi");
        return text.split(regex).map((part, i) =>
            regex.test(part) ? <span key={i} style={{ background: "yellow" }}>{part}</span> : part
        );
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
        <ReportModal
            isOpen={reportOpen}
            onClose={() => setReportOpen(false)}
            title="Report product"
            subtitle={data?.name ? `About: ${data.name}` : ""}
            placeholder="Tell us what is incorrect about this product's info..."
            onSubmit={handleReportProduct}
        />
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
                    <div className="rating-group" aria-label={ratingAria}>
                        <span className="rating-label">Overall Rating:</span>
                        {hasRating && reviewCount > 0 && (
                            <p className="rating-count-left">
                                {reviewCount} review{reviewCount === 1 ? "" : "s"}
                            </p>
                        )}
                        <div className="rating-stars" role="radiogroup" aria-label="Rate this product">
                            {[1, 2, 3, 4, 5].map((i) => (
                                <button
                                    key={i}
                                    type="button"
                                    className="rating-star-btn"
                                    aria-label={`Rate ${i} star${i > 1 ? "s" : ""}`}
                                    onMouseMove={(e) => setQuickHoverRating(getStarValueFromEvent(e, i))}
                                    onMouseEnter={(e) => setQuickHoverRating(getStarValueFromEvent(e, i))}
                                    onMouseLeave={() => setQuickHoverRating(null)}
                                    onClick={(e) => handleQuickRating(e, i)}
                                    disabled={isSubmittingQuickRating}
                                >
                                    <span className="rating-star" style={{ "--fill": ratingFill(i) }}>
                                        <svg viewBox="0 0 24 24" aria-hidden="true">
                                            <path
                                                className="rating-star-outline"
                                                d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"
                                            />
                                            <g className="rating-star-fill">
                                                <path d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z" />
                                            </g>
                                        </svg>
                                    </span>
                                    <span className="tooltip rating-tooltip">
                                        {ratingTooltipText}
                                    </span>
                                </button>
                            ))}
                        </div>
                        <span className="rating-text">{ratingDisplayText}</span>
                        {!hasRating && (
                            <p className="rating-empty">Be the first to review!</p>
                        )}
                    </div>

                    <div className="shade-selector">
                        <p htmlFor={`shade-${data.id}`}>Shade:</p>
                        <div
                            className={`shade-dropdown ${isShadeOpen ? "is-open" : ""}`}
                            ref={shadeSelectRef}
                        >
                            <button
                                type="button"
                                className="shade-trigger"
                                aria-haspopup="listbox"
                                aria-expanded={isShadeOpen}
                                onClick={() => setIsShadeOpen((open) => !open)}
                            >
                                <span
                                    className="shade-swatch"
                                    style={{ backgroundColor: selectedShade?.hexCode ?? "#eee" }}
                                    aria-hidden="true"
                                />
                                <span className="shade-label-text">
                                    {selectedShade?.shadeName ?? "Select shade"}
                                </span>
                                <span className="shade-caret">▼</span>
                            </button>
                            {isShadeOpen && (
                                <div className="shade-menu" role="listbox">
                                    {data.shades.map((shade) => (
                                        <button
                                            key={shade.shadeName}
                                            type="button"
                                            className="shade-option"
                                            role="option"
                                            aria-selected={shade.shadeName === selectedShade?.shadeName}
                                            onClick={() => {
                                                setSelectedShade(shade);
                                                setIsShadeOpen(false);
                                            }}
                                        >
                                            <span
                                                className="shade-swatch"
                                                style={{ backgroundColor: shade?.hexCode ?? "#eee" }}
                                                aria-hidden="true"
                                            />
                                            <span className="shade-option-text">{shade.shadeName}</span>
                                        </button>
                                    ))}
                                </div>
                            )}
                        </div>
                        <p className="shade-rating">
                            {shadeRatingValue !== null
                                ? `Shade rating: ${Number.isInteger(shadeRatingValue) ? shadeRatingValue : shadeRatingValue.toFixed(1)}/5`
                                : "No ratings for this shade yet"}
                        </p>
                    </div>

                    <p className="price">
                        <span>Price: ≈</span> {formattedPrice ? `≈ ${formattedPrice}` : (data.price ? `$${data.price}` : "N/A")}
                    </p>

                    <div className="product-actions">
                        <div className="action-icon" onClick={handleToggleWishlist}>
                            <span className={`icon ${isInWishlist ? "icon--active" : ""}`}>♥</span>
                            <span className="tooltip">
                              {isInWishlist ? "Remove from Wishlist" : "Add to Wishlist"}
                            </span>
                        </div>

                        <div className="action-icon" onClick={handleAddToRoutine}>
                            <span className="icon">+</span>
                            <span className="tooltip">Add to {data.category.baseCategoryName} Routine</span>
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
                    <div className="product-report">
                        <div className="action-icon" onClick={() => setReportOpen(true)}>
                            <span className="icon report-icon" aria-hidden="true">
                                <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="feather feather-flag">
                                  <path d="M4 22V4a2 2 0 0 1 2-2h11.5a1.5 1.5 0 0 1 1.34 2.22L17 7l1.84 2.78A1.5 1.5 0 0 1 17.5 12H6" />
                                </svg>
                            </span>
                            <span className="tooltip">Report something about this product's info</span>
                        </div>
                    </div>
                    
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
                    
                    {data.mayContainIngredients?.length > 0 && (
                        <div className="may-contain-list">
                            <div className="may-contain-header">May Contain</div>
                            {data.mayContainIngredients.map((i, idx) => (
                                <span key={i.name}>
                                    {i.name}
                                    {idx < data.mayContainIngredients.length - 1 && ", "}
                                </span>
                            ))}
                        </div>
                    )
                    }
                    </>
                )}
            </section>

            <hr className="section-divider" />

            <h2 className="reviews-and-questions-header">Search Questions and Reviews</h2>

            <div className="reviews-search-container">
                <input
                    type="text"
                    className="reviews-search-bar"
                    placeholder="Enter terms here..."
                    value = {searchQuery}
                    onChange={e => setSearchQuery(e.target.value)}
                />
    
                <button
                    type="button"
                    className="reviews-search-button"
                    aria-label="Search"
                >
                    <FaSearch />
                </button>
            </div>

            {searchQuery && (
                <div className="search-results">
                    {searchResults.questions.map(q => (
                    <QuestionCard key={q.id} question={q} searchTerm={searchQuery} /* other props */ />
                    ))}
                    {searchResults.reviews.map(review => (
                    <ReviewCard
                        key={review.reviewId || review.id}
                        review={review}
                        reviewId={review.reviewId || review.id}
                        reviewerName={review.reviewerName}
                        reviewTitle={review.reviewTitle}
                        reviewText={review.reviewText}
                        reviewDate={review.createdAt}
                        avatar={review.reviewerProfilePicture}
                        upvoteCount={review.upvoteCount}
                        isUpvoted={review.hasUpvoted}
                        searchTerm={searchQuery}
                        // ...other props as needed
                    />
                    ))}
                    {searchResults.reviews.length === 0 && searchResults.questions.length === 0 && (
                    <div>No results found.</div>
                    )}
                </div>
            )}

            <hr className="section-divider" />  

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
                                    const success = await submitQuestion(productId, payload.body);
                                    if (success) {
                                        showToast("Question submitted!", "success");
                                        await loadQuestions(0);
                                        setAskOpen(false);
                                    } else {
                                        showToast("Failed to submit question.", "error");
                                    }
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
                                        <QuestionCard
                                            key={q.id}
                                            question={q}
                                            onRefresh={() => loadQuestions(questionsPage)}
                                            currentUserName={currentUser?.username}
                                            isLoggedIn={isLoggedIn}
                                            onRequireLogin={() => setShowLoginModal(true)}
                                            onToast={showToast}
                                        />
                                    ))}
                                </div>
                            )}
                            {questionsTotalPages > 1 && (
                                <div className="questions-pagination">
                                    <button
                                        type="button"
                                        className="questions-page-button"
                                        onClick={() => loadQuestions(Math.max(0, questionsPage - 1))}
                                        disabled={questionsPage <= 0}
                                    >
                                        Previous
                                    </button>
                                    <span className="questions-page-info">
                                        Page {questionsPage + 1} of {questionsTotalPages}
                                    </span>
                                    <button
                                        type="button"
                                        className="questions-page-button"
                                        onClick={() => loadQuestions(Math.min(questionsTotalPages - 1, questionsPage + 1))}
                                        disabled={questionsPage >= questionsTotalPages - 1}
                                    >
                                        Next
                                    </button>
                                </div>
                            )}
                        </div>
                    )}
            </section>

            <hr className="section-divider" />

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
                                onToast={showToast}
                                onDelete={async (reviewId) => {
                                    const success = await deleteReview(reviewId);
                                    if (success) {
                                        showToast("Review deleted successfully.", "success");
                                        loadProduct();
                                        setReviewOpen(false);
                                        setEditingReview(null);
                                        setReviewRefreshKey((value) => value + 1);
                                    } else {
                                        showToast("Failed to delete review. Please try again.", "error");
                                    }
                                }}
                                onSubmit={async (payload) => {
                                    const reviewId = payload.existingReviewId ?? editingReview?.reviewId ?? editingReview?.id;
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
                                        loadProduct();
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
                                existingReviewsByShade={userReviewsByShade}
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
                            onRequireLogin={() => setShowLoginModal(true)}
                        />
                    </div>
                )}
            </section>

        </div>
        {addToRoutineOpen && (
            <AddToRoutineModal
                baseCategory={data.category}
                productName={data.name}
                productId={data.id}
                shadeName={selectedShade?.shadeName ?? ""}
                onClose={() => setAddToRoutineOpen(false)}
            />
        )}
    </div>
  );
}
