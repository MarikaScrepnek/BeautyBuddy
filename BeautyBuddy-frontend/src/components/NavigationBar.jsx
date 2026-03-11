import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { FaSearch } from "react-icons/fa";
import { NavLink } from 'react-router-dom';
import { useEffect, useState } from 'react';

import './NavigationBar.css';

import AuthModal from './AuthModal';
import { getCurrentUser, logoutUser } from '../api/authApi';

export default function NavigationBar({ searchQuery, setSearchQuery }) {
  const navigate = useNavigate();
  const location = useLocation();

  const [showAuth, setShowAuth] = useState(false);

  const[isLoggedIn, setIsLoggedIn] = useState(false);

  const handleChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);

    if (location.pathname === "/products") {
      navigate(`/?q=${encodeURIComponent(value)}`, { replace: true });
    }
  }

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      navigate(`/?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  const handleSearch = () => {
    navigate(`/?q=${encodeURIComponent(searchQuery)}`);
  };

  
  useEffect(() => {
    getCurrentUser()
      .then(() => setIsLoggedIn(true))
      .catch(() => setIsLoggedIn(false));
    const handleAuthLogin = () => {
      getCurrentUser()
        .then(() => setIsLoggedIn(true))
        .catch(() => setIsLoggedIn(false));
    };

    window.addEventListener("auth:login", handleAuthLogin);

    return () => {
      window.removeEventListener("auth:login", handleAuthLogin);
    };
  }, []);

  return (
    <div>
      <header className="navigation-bar">
        <div className='nav-container'>
          <h1 className='logo' onClick={() => navigate('/')}>BeautyBuddy</h1>

          <nav className='nav-bar-links'>
            <NavLink to="/products" className={({ isActive }) => isActive ? 'active-link' : undefined}>Products</NavLink>
            <NavLink to="/discussions" className={({ isActive }) => isActive ? 'active-link' : undefined}>Discussions</NavLink>
            <NavLink to="/my-routines" className={({ isActive }) => isActive ? 'active-link' : undefined}>Routines</NavLink>
            <NavLink to="/my-feed" className={({ isActive }) => isActive ? 'active-link' : undefined}>Feed</NavLink>
          </nav>

          <div className="search-container">
            <input
              type="text"
              className="search-bar"
              placeholder="Search products..."
              value={searchQuery}
              onChange={handleChange}
              onKeyDown={handleKeyDown}
            />

            <button
              type="button"
              className="search-button"
              onClick={handleSearch}
              aria-label="Search"
            >
              <FaSearch />
            </button>
          </div>

          {isLoggedIn ? (
            <div className="user-section">
              <button
                className="logout-button"
                onClick={() => {
                  logoutUser()
                    .then(() => {
                      setIsLoggedIn(false);
                      navigate("/");
                    })
                    .catch((error) => console.error("Logout failed:", error));
                }}
              >
                Logout
              </button>
            </div>
          ) : (
            <button
              className="login-button"
              onClick={() => setShowAuth(true)}
            >
              Login / Create Account
            </button>
          )}

          {showAuth && (
            <AuthModal
             onClose={() => setShowAuth(false)}
             onLoginSuccess={async () => {
              try {
                await getCurrentUser();
                setIsLoggedIn(true);
              } catch {
                setIsLoggedIn(false);
              }
              setShowAuth(false);
             }}
            />
          )}
          
        </div>
      </header>

      <main className='page-container'>
        <Outlet />
      </main>

    </div>
  );
}