import { useEffect, useState } from "react";
import { getExchangeRate } from "../api/productApi";
import { useNavigate } from "react-router-dom";

import './ProductList.css';
import ReviewStars from "../../../components/ui/ReviewStars";

export default function ProductList({ searchQuery, onLoadingChange }) {

  const [setSelectedItemRoutine] = useState(null);
  const [setSelectedItemWishlist] = useState(null);

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
            <h3 className="product-card-title">{p.name}</h3>
            <p>{p.brand?.name}</p>
            <div className="product-card-bottom">
              <p className="shade-count">
                {p.shades?.length > 1 ? `${p.shades.length} shades` : "\u00A0"}
              </p>
              <ReviewStars rating={p.rating} disabled={true} />
              <p className="product-card-price">
                Price: {p.price ? `≈ ${priceMap[p.id]}` : "N/A"}
              </p>
              <div className="wishlist-actions">
              
              <div className="action-icon" onClick={() => setSelectedItemRoutine(p)}>
                  <span style={{ color: "#1a8ec4" }} className="icon">+</span>
                  <span className="tooltip">Add to routine</span>
              </div>

              <div
                  className="action-icon" onClick={() => setSelectedItemWishlist(p)}
              >
                  <span className="icon">♥</span>
                  <span className="tooltip">Add to wishlist</span>
              </div>

          </div>
            </div>
          </div>
        </div>
      ))}
      {setSelectedItemRoutine && (
        <AddToRoutineModal
          productId={setSelectedItemRoutine.id}
          productName={setSelectedItemRoutine.name}
          baseCategory={setSelectedItemRoutine.category}
          onClose={() => setSelectedItemRoutine(null)}
        />
      )}
    </div>
  );
}