import { useEffect, useRef } from "react";
import { useState } from "react";

import { getWishlist, removeFromWishlist, searchWishlist } from "../../../api/wishlistApi";

import Toast from "../../../components/Toast";
import Tooltip from "../../../components/common/Tooltip";
import Searchbar from "../../../components/common/Searchbar";

import "./Wishlist.css";

export default function Wishlist({isLoggedIn}) {
    const [showToast, setShowToast] = useState(false);
    const toastTime = 1000;

    const [wishlist, setWishlist] = useState([]);
    const [wishlistLoading, setWishlistLoading] = useState(false);
    const [wishlistError, setWishlistError] = useState("");

    const wishlistContainerRef = useRef(null);

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

    async function handleSearch(query) {
        if (query.trim() === "") {
            loadWishlist();
        } else {
            try {
                const results = await searchWishlist(query);    
                setWishlist(results);
            } catch (err) {
                setWishlistError("Search failed.");
            }
        }
    }

    useEffect(() => {
        loadWishlist();
    }, []);

    useEffect(() => {
        const container = wishlistContainerRef.current;
        if (!container) return;
        const handleWheel = (e) => {
            if (e.deltaY !== 0) {
                e.preventDefault();
                container.scrollBy({
                    left: e.deltaY * 3, // adjust multiplier for sensitivity
                    behavior: 'smooth'
                });
            }
        };
        container.addEventListener('wheel', handleWheel, { passive: false });
        return () => {
            container.removeEventListener('wheel', handleWheel);
        };
    }, []);

    return (
        <>
        {showToast && 
        <Toast message="Item removed from wishlist" type="success" duration={toastTime} onClose={() => setShowToast(false)} />
        }

        <div className="wishlist-header">
            <h1>Wishlist♥</h1>
            {isLoggedIn && (
                <span className="wishlist-search">
                    <Searchbar placeholder="Search wishlist..." onSearch={handleSearch} />
                </span>
            )}
        </div>

        {!isLoggedIn ? (
            <p>Please log in to see your wishlist.</p>
        ) : (
            <div
                className="wishlist-container"
                ref={wishlistContainerRef}
                style={{scrollBehavior: 'smooth'}}
            >
                {wishlist.map((item) => (
                    <div className="wishlist-item-card" key={item.id}>

                    <div className="wishlist-item-header">
                        <h2 style={{fontSize: "1.25rem", textAlign: "center", justifyContent: "center"}}>
                            {item.productName}
                        </h2>
                        <p style={{textAlign: "center"}}>
                            {item.brandName}
                        </p>
                        <p style={{textAlign: "center"}}>
                            in {item.shadeName}
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
        </>
    );
}