import { useState, useEffect } from 'react';

import { getCurrentUser } from '../../api/authApi';

import Wishlist from './components/Wishlist';

import "./MyRoutines.css";

export default function MyRoutines() {
  // log in
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState("");

  // on mount
  useEffect(() => {

    getCurrentUser()
      .then((user) => {
        setIsLoggedIn(true);
        setUsername(user.username);
      })
      .catch(() => setIsLoggedIn(false));
  }, []);

  return (
  <div className='routines-page-container'>
    <div className='routines-page-card'>

      <div style={{textAlign: "center", textDecoration: "underline", textDecorationColor: "#f0cef0" }}>
        {isLoggedIn ? <p>Welcome back, {username}!</p> : <p>Please log in to view your routines.</p>}
      </div>

      <div className='wishlist-container'>
          <Wishlist isLoggedIn={isLoggedIn} />
      </div>

      <div className='routine-container'>
        <h1>Routines</h1>
      </div>

    </div>
  </div>
  );
}