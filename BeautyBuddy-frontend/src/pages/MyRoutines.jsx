import React, { useState, useEffect } from 'react';
import { getCurrentUser } from '../api/authApi';
import { getWishlist } from '../api/wishlistApi';

export default function MyRoutines() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [wishlist, setWishlist] = useState([]);
  const [wishlistLoading, setWishlistLoading] = useState(false);
  const [wishlistError, setWishlistError] = useState("");

  const loadWishlist = async () => {
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
    getCurrentUser()
      .then(() => {
        setIsLoggedIn(true);
        loadWishlist();
      })
      .catch(() => {
        setIsLoggedIn(false);
        setWishlist([]);
      });
    const handleAuthLogin = () => {
      getCurrentUser()
        .then(() => {
          setIsLoggedIn(true);
          loadWishlist();
        })
        .catch(() => {
          setIsLoggedIn(false);
          setWishlist([]);
        });
    };

    window.addEventListener("auth:login", handleAuthLogin);

    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
    };
  }, []);

  return (
    <div>
      {isLoggedIn ? <p>Welcome back, user!</p> : <p>Please log in to view your routines.</p>}
      <div className='wishlist-container'>
        <h1>Wishlist♥</h1>
        {!isLoggedIn && <p>Log in to see your wishlist.</p>}
        {isLoggedIn && wishlistLoading && <p>Loading wishlist...</p>}
        {isLoggedIn && wishlistError && <p>{wishlistError}</p>}
        {isLoggedIn && !wishlistLoading && !wishlistError && wishlist.length === 0 && (
          <p>Your wishlist is empty.</p>
        )}
        {isLoggedIn && !wishlistLoading && !wishlistError && wishlist.length > 0 && (
          <ul>
            {wishlist.map((item) => (
              <li key={item.id}>
                {item.imageLink && (
                  <img src={item.imageLink} alt={item.productName} width="48" height="48" />
                )}
                <span>{item.productName}</span>
                {item.shadeName && <span> — {item.shadeName}</span>}
              </li>
            ))}
          </ul>
        )}
      </div>
      <div className='routine-container'>
        <h1>Routines</h1>
      </div>
    </div>
  );
}