import { useEffect } from "react";
import { useState } from "react";

import { getWishlist, removeFromWishlist } from "../../../api/wishlistApi";

import "./Wishlist.css";
import Toast from "../../../components/Toast";

export default function Wishlist(isLoggedIn) {
    const [showToast, setShowToast] = useState(false);
    const toastTime = 1000;

    const [wishlist, setWishlist] = useState([]);
    const [wishlistLoading, setWishlistLoading] = useState(false);
    const [wishlistError, setWishlistError] = useState("");

    async function loadWishlist() {
    setWishlistLoading(true);
    setWishlistError("");
    try {
        const items = await getWishlist();
        setWishlist(items);
    } catch (err) {
        setWishlistError("Failed to load wishlist.");
        setWishlist([]);
    } finally {
        setWishlistLoading(false);
    }
    };

    useEffect(() => {
        loadWishlist();
    }, []);

  return (
    <div>
    {showToast && 
    <Toast message="Item removed from wishlist" type="success" duration={toastTime} onClose={() => setShowToast(false)} />
    }
      <h1>Wishlist♥</h1>
      {!isLoggedIn ? (
        <p>Please log in to see your wishlist.</p>
      ) : (
        <div className="wishlist-container">
            {wishlist.map((item) => (
            <div key={item.id}>
                <div className="wishlist-item-card">

                    <div className="wishlist-item-header">
                        <h2 style={{fontSize: "1.25rem"}}>
                            {item.productName}
                        </h2>
                        <p style={{textAlign: "center"}}>
                            {item.brandName}
                        </p>
                    </div>

                    <img src={item.imageLink} alt="Product" style={{"height": "250px"}} />

                    <div className="wishlist-item-footer">

                        <div className="wishlist-item-meta">
                            <span>Rating: {item.rating}</span>
                            <span>Price: ${item.price}</span>
                        </div>

                        <button
                            className="remove-button"
                            onClick={() => {
                                removeFromWishlist(item.productId, item.shadeName).then(() => {
                                    loadWishlist();
                                    setShowToast(true);
                                    setTimeout(() => setShowToast(false), toastTime);
                                });
                            }}
                        >
                            -
                        </button>

                    </div>

                </div>
            </div>
            ))}
        </div>
      )}
    </div>
  );
}