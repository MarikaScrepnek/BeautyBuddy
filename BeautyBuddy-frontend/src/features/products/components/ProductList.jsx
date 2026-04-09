import { useEffect, useState } from "react";
import { getExchangeRate } from "../api/productApi";
import { useNavigate } from "react-router-dom";
import AuthModal from "../../auth/modals/AuthModal";
import { getCurrentUser } from "../../auth/api/authApi";

import './ProductList.css';
import ReviewStars from "../../../components/ui/ReviewStars";
import AddToRoutineModal from "../../routines/modals/AddToRoutineModal";
import { addToWishlist, getWishlist, removeFromWishlist } from "../../wishlist/api/wishlistApi";
import Toast from "../../../components/ui/Toast";
import { addToBreakoutList, getBreakoutListProducts, removeFromBreakoutList } from "../../breakout/api/breakoutListApi";
import { getAllRoutineItems } from "../../routines/api/routineApi";

export default function ProductList({ searchQuery, sortKey, filterOption, onLoadingChange }) {

  const [selectedItemRoutine, setSelectedItemRoutine] = useState(null);
  const [wishlistItems, setWishlistItems] = useState([]);
  const [breakoutListItems, setBreakoutListItems] = useState([]);
  const [routineItems, setRoutineItems] = useState([]);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const[showToast, setShowToast] = useState(false);
  const[toastMessage, setToastMessage] = useState("");

  async function fetchWishlistItems() {
    getWishlist()
      .then((data) => {
        const productIds = data.map(item => item.productId);
        setWishlistItems(productIds);
      })
      .catch((error) => {
        console.error("Error fetching wishlist items:", error);
      });
  }

  async function fetchBreakoutListItems() {
    getBreakoutListProducts()
      .then((data) => {
        const productIds = data.map(item => item.productId);
        setBreakoutListItems(productIds);
      })
      .catch((error) => {
        console.error("Error fetching breakout list items:", error);
      });
  }

  async function fetchRoutineItems() {
    getAllRoutineItems()
      .then((data) => {
        setRoutineItems(data);
      })
      .catch((error) => {
        console.error("Error fetching routine items:", error);
      }); 
  }

  function isInWishlist(productId) {
    return wishlistItems.includes(productId);
  }

  function isInBreakoutList(productId) {
    return breakoutListItems.includes(productId);
  }

  function isInRoutine(productId) {
    return routineItems.includes(productId);
  }

  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));

    const handleAuthLogin = () => {
      setIsLoggedIn(true);
    };

    const handleAuthLogout = () => {
      setIsLoggedIn(false);
      setWishlistItems([]);
      setBreakoutListItems([]);
      setRoutineItems([]);
    };

    window.addEventListener("auth:login", handleAuthLogin);
    window.addEventListener("auth:logout", handleAuthLogout);
    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
      window.removeEventListener("auth:logout", handleAuthLogout);
    };
  }, []);

  function handleRoutineChange(action, productId) {
    setRoutineItems(prev => {
      if (action === "added") {
        if (prev.includes(productId)) return prev;
        return [...prev, productId];
      }
      if (action === "removed") {
        return prev.filter(id => id !== productId);
      }
      return prev;
    });
  }

  async function handleToggleWishlist(productId) {
    if (isInWishlist(productId)) {
      removeFromWishlist(productId)
        .then(() => {
          setToastMessage("Product removed from wishlist!");
          setShowToast(true);
        })
        .catch((error) => {
          setToastMessage("Error removing product from wishlist");
          setShowToast(true);
        });
      wishlistItems.splice(wishlistItems.indexOf(productId), 1);
    }
    else {
    addToWishlist(productId)
      .then(() => {;
        setToastMessage("Product added to wishlist!");
        setShowToast(true);
      })
      .catch((error) => {
        setToastMessage("Error adding product to wishlist");
        setShowToast(true);
      });
    wishlistItems.push(productId);
    }
  }

  async function handleToggleBreakoutList(productId) {
    if (isInBreakoutList(productId)) {
      removeFromBreakoutList(productId)
        .then(() => {
          setToastMessage("Product removed from breakout list!");
          setShowToast(true);
        })
        .catch((error) => {
          setToastMessage("Error removing product from breakout list");
          setShowToast(true);
        });
      breakoutListItems.splice(breakoutListItems.indexOf(productId), 1);
    }
    else {
      addToBreakoutList("product", productId)
        .then(() => {
          setToastMessage("Product added to breakout list!");
          setShowToast(true);
        })
        .catch((error) => {
          setToastMessage("Error adding product to breakout list");
          setShowToast(true);
        });
      breakoutListItems.push(productId);
    }
  }

  // --- All hooks at the top ---
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();
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
  const [exchangeRate, setExchangeRate] = useState(1);
  const [priceMap, setPriceMap] = useState({});

  function getFilterParams(option) {
    switch (option) {
      case "Skincare":
      case "Makeup":
      case "Haircare":
        return { category: option };
      default:
        return { category: null };
    }
  }

  async function handleInlineReviewSubmitted(productId) {
    try {
      const res = await fetch(`/api/products/${productId}`);
      if (!res.ok) return;
      const updated = await res.json();
      setProducts(prev =>
        prev.map(p =>
          p.id === productId ? { ...p, rating: updated.rating } : p
        )
      );
    } catch (err) {
      console.error("Failed to refresh product rating", err);
    }
  }

  useEffect(() => {
    const query = searchQuery || "";
    setLoading(true);
    onLoadingChange?.(true);
    const params = new URLSearchParams();
    if (query) params.set("q", query);
    if (sortKey) params.set("sort", sortKey);
    const { category } = getFilterParams(filterOption);
    if (category) params.set("category", category);

    const base = `/api/products/search`;
    const url = params.toString()
      ? `${base}?${params.toString()}`
      : base;

    fetch(url)
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
  }, [searchQuery, sortKey, filterOption]);

  useEffect(() => {
    async function fetchRate() {
      const rate = await getExchangeRate(userCurrency);
      setExchangeRate(rate);
    }
    fetchRate();
  }, [userCurrency]);

  useEffect(() => {
    // Calculate formatted prices for all products
    const newPriceMap = {};
    products.forEach((p) => {
      if (!p.price) return;
      const priceCad = Number(p.price);
      const priceInUserCurrency = priceCad * (Number(exchangeRate) || 1);
      const formatted = new Intl.NumberFormat(userLocale, {
        style: "currency",
        currency: userCurrency,
      }).format(priceInUserCurrency);
      newPriceMap[p.id] = formatted;
    });
    setPriceMap(newPriceMap);
  }, [products, exchangeRate, userLocale, userCurrency]);

  useEffect(() => {
    if (!isLoggedIn) {
      setWishlistItems([]);
      setBreakoutListItems([]);
      setRoutineItems([]);
      return;
    }

    fetchWishlistItems();
    fetchBreakoutListItems();
    fetchRoutineItems();
  }, [isLoggedIn]);

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

  return (
    <div className="product-grid">
      {showLoginModal && (
        <AuthModal
          onClose={() => setShowLoginModal(false)}
          onLoginSuccess={() => {
            setIsLoggedIn(true);
            setShowLoginModal(false);
          }}
        />
      )}
      {products.map(p => (
        <div className="product-card-home" key={p.id} onClick={() => navigate(`/products/${p.id}`)}>
          {p.image_link && (
            <img src={p.image_link} alt={p.name} className="product-image-home" />
          )}
          <div className="product-card-content">
              <div className="product-card-header">
              <h3 className="product-card-title">{p.name}</h3>
              <p>{p.brand?.name}</p>
              <p className="shade-count">
                {p.shades.length > 1 ? `${p.shades.length} shades` : "\u00A0"}
              </p>
            </div>
            <div className="product-card-bottom">
              <p className="product-card-price">
                {p.price ? `≈ ${priceMap[p.id]}` : "Price: N/A"}
              </p>
              <div onClick={(e) => e.stopPropagation()}>
                <ReviewStars
                  rating={p.rating}
                  productId={p.id}
                  shadeName={p.shadeName}
                  locked={!isLoggedIn}
                  onRequireLogin={() => setShowLoginModal(true)}
                  onReviewSubmitted={() => handleInlineReviewSubmitted(p.id)}
                  
                />
              </div>
              <div className="wishlist-actions">

                <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      if (!isLoggedIn) {
                        setShowLoginModal(true);
                        return;
                      }
                      handleToggleWishlist(p.id);
                    }}
                  >
                    {isInWishlist(p.id) ? (
                      <>
                      <span style={{ color: "rgb(231, 155, 219)" }} className="icon">♥</span>
                      <span className="tooltip">Remove from wishlist</span>
                      </>
                    ) : (
                      <>
                      <span className="icon">♥</span>
                      <span className="tooltip">Add to wishlist</span>
                      </>
                    )}
                </div>
              
                  <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      if (!isLoggedIn) {
                        setShowLoginModal(true);
                        return;
                      }
                      setSelectedItemRoutine(p);
                    }}
                  >
                    {isInRoutine(p.id) ? (
                      <>
                      <span style={{ color: "#1a8ec4" }} className="icon">+</span>
                      <span className="tooltip">Add to another routine</span>
                      </>
                    ) : (
                      <>
                      <span className="icon">+</span>
                      <span className="tooltip">Add to routine</span>
                      </>
                    )}
                </div>

                  <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      if (!isLoggedIn) {
                        setShowLoginModal(true);
                        return;
                      }
                      handleToggleBreakoutList(p.id);
                    }}
                  >
                    {isInBreakoutList(p.id) ? (
                      <>
                      <span style={{ color: "red" , fontSize: "0.95em"}} className="icon">!</span>
                      <span className="tooltip">Remove from breakout list</span>
                      </>
                    ) : (
                      <>
                      <span style={{ fontSize: "0.95em"}} className="icon">!</span>
                      <span className="tooltip">Add to breakout list</span>
                      </>
                    )}
                </div>

              </div>
            </div>
          </div>
        </div>
      ))}
      {selectedItemRoutine && (
        <AddToRoutineModal
          productId={selectedItemRoutine.id}
          productName={selectedItemRoutine.name}
          baseCategoryName={selectedItemRoutine.category.baseCategoryName}
          onRoutineChange={handleRoutineChange}
          onClose={() => setSelectedItemRoutine(null)}
        />
      )}
      {showToast && (
        <Toast message={toastMessage} />
      )}
    </div>
  );
}