import { useState, useEffect } from 'react';

import { getCurrentUser } from '../../api/authApi';

import Wishlist from './components/Wishlist';
import Routines from './components/Routines';

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

      <div style={{textAlign: "center", textDecoration: "underline", textDecorationColor: "#f0cef0"}}>
        {isLoggedIn ? <p>Welcome back, {username}!</p> : <p>Please log in to view your routines.</p>}
      </div>
      
      <Wishlist isLoggedIn={isLoggedIn} />

      <Routines isLoggedIn={isLoggedIn} />

  </div>
  );
}