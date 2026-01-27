import React, { useState, useEffect } from 'react';

export default function MyRoutines() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const user = localStorage.getItem('user');
    if (user) {
      setIsLoggedIn(true);
    }
    else {
      setIsLoggedIn(false);
    }
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