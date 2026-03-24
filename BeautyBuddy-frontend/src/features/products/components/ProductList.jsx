import { useEffect, useState } from "react";
import { getExchangeRate } from "../api/productApi";
import { useNavigate } from "react-router-dom";

import './ProductList.css';
import ReviewStars from "../../../components/ui/ReviewStars";
import AddToRoutineModal from "../../routines/modals/AddToRoutineModal";
import { addToWishlist } from "../../wishlist/api/wishlistApi";
import Toast from "../../../components/ui/Toast";
import { addToBreakoutList } from "../../breakout/api/breakoutListApi";

export default function ProductList({ searchQuery, onLoadingChange }) {

  const [selectedItemRoutine, setSelectedItemRoutine] = useState(null);

  const[showToast, setShowToast] = useState(false);
  const[toastMessage, setToastMessage] = useState("");

  async function isInWishlist(productId) {
  }

  async function isInBreakoutList(productId) {
  }

  async function isInRoutine(productId) {
  }

  async function handleToggleWishlist(productId) {
    addToWishlist(productId)
      .then(() => {;
        setToastMessage("Product added to wishlist!");
        setShowToast(true);
      })
      .catch((error) => {
        setToastMessage("Error adding product to wishlist");
        setShowToast(true);
      });
  }

  async function handleToggleBreakoutList(productId) {
    addToBreakoutList("product", productId)
      .then(() => {
        setToastMessage("Product added to breakout list!");
        setShowToast(true);
      })
      .catch((error) => {
        setToastMessage("Error adding product to breakout list");
        setShowToast(true);
      });
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

  if (loading) {
    return <div className="loading">Loading products...</div>;
  }

  return (
    <div className="product-grid">
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
                <ReviewStars rating={p.rating} />
              </div>
              <div className="wishlist-actions">
              
                  <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      setSelectedItemRoutine(p);
                    }}
                  >
                    <span style={{ color: "#1a8ec4" }} className="icon">+</span>
                    <span className="tooltip">Add to routine</span>
                </div>

                  <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleToggleWishlist(p.id);
                    }}
                  >
                    <span className="icon">♥</span>
                    <span className="tooltip">Add to wishlist</span>
                </div>

                  <div
                    className="action-icon"
                    onClick={(e) => {
                      e.stopPropagation();
                      handleToggleBreakoutList(p.id);
                    }}
                  >
                    <span className="icon">!</span>
                    <span className="tooltip">Add to breakout list</span>
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
          onClose={() => setSelectedItemRoutine(null)}
        />
      )}
      {showToast && (
        <Toast message={toastMessage} />
      )}
    </div>
  );
}