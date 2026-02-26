import { useEffect } from "react";
import { useState } from "react";
import { FaSearch } from "react-icons/fa";

import { getWishlist, removeFromWishlist } from "../../../api/wishlistApi";

import Toast from "../../../components/Toast";
import Tooltip from "../../../components/common/Tooltip";
import Searchbar from "../../../components/common/Searchbar";

import "./Wishlist.css";

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

    <div className="wishlist-header">
        <h1>Wishlist♥</h1>
        <span className="wishlist-search">
            <Searchbar placeholder="Search wishlist..." />
        </span>
    </div>

    {!isLoggedIn ? (

        <p>Please log in to see your wishlist.</p>
    
    ) : (
        <div className="wishlist-container">
            {wishlist.map((item) => (
            <div className="wishlist-item-card" key={item.id}>

                    <div className="wishlist-item-header">
                        <h2 style={{fontSize: "1.25rem", textAlign: "center", justifyContent: "center"}}>
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

                        <Tooltip message="Remove from wishlist" position="top">
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
                        </Tooltip>

                    </div>
                    
                </div>
            ))}
        </div>
      )}
    </div>
  );
}