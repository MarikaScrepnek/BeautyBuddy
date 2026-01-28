import React, { useState, useEffect } from 'react';
import { getCurrentUser } from '../api/authApi';

export default function MyRoutines() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
  }, []);

  return (
    <div>
      {isLoggedIn ? <p>Welcome back, user!</p> : <p>Please log in to view your routines.</p>}
      <div className='wishlist-container'>
        <h1>Wishlist♥</h1>
      </div>
      <div className='routine-container'>
        <h1>Routines</h1>
      </div>
    </div>
  );
}