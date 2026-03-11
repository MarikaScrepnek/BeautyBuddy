import { useEffect, useRef } from "react";
import { useState } from "react";

import { getWishlist, removeFromWishlist, searchWishlist } from "../../../api/wishlistApi";

import Toast from "../../../components/Toast";
import Tooltip from "../../../components/common/Tooltip";
import Searchbar from "../../../components/common/Searchbar";

import "./Wishlist.css";
import ReviewStars from "../../../components/common/ReviewStars";

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
            <>
            {wishlist.length === 0 && !wishlistLoading && (
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center"}}>
                    <p style={{ fontStyle: "italic", color: "#888" }}>Your wishlist is empty.</p>
                </div>
            )}
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
                        <div style={{display: "flex", flexDirection: "row", justifyContent: "center", alignItems: "center", gap: "4px"}}>
                            <p style={{textAlign: "center"}}>
                                {item.brandName} 
                            </p>
                            {item.shadeName && (
                                <p style={{textAlign: "center"}}>
                                    • {item.shadeName}
                                </p>
                            )}
                        </div>
                    </div>

                    <img src={item.imageLink} alt="Product" style={{"height": "250px"}} />

                    <div className="wishlist-item-footer">

                        <div className="wishlist-item-meta">
                            <span style={{marginBottom: "6px"}}>${item.price}</span>
                            {item.rating ? (
                                <ReviewStars productId={item.productId} shadeName={item.shadeName} rating={item.rating} disabled={true} />
                            ) : (
                                <p style={{fontStyle: "italic", color: "#888"}}>No ratings yet</p>
                            )}
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
            </>
        )
        }
        </>
    );
}