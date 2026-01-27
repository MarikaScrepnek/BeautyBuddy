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
      <h1>My Routines Page</h1>
      {isLoggedIn ? <p>Welcome back, user!</p> : <p>Please log in to view your routines.</p>}
    </div>
  );
}