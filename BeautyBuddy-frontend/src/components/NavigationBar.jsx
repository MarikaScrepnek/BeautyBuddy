import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';
import { FaSearch } from "react-icons/fa";
import { NavLink } from 'react-router-dom';
import { useEffect, useState } from 'react';

import userSettingsIcon from '../assets/images/user-settings-icon.png';

import './NavigationBar.css';

import AuthModal from './AuthModal';

export default function NavigationBar({ searchQuery, setSearchQuery }) {
  const navigate = useNavigate();
  const location = useLocation();

  const [showAuth, setShowAuth] = useState(false);

  const[isLoggedIn, setIsLoggedIn] = useState(false);

  const handleChange = (e) => {
    const value = e.target.value;
    setSearchQuery(value);

    if (location.pathname === "/") {
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
    const user = localStorage.getItem('user');
    if (user) {
      setIsLoggedIn(!!user);
    }

  const handleStorageChange = () => {
    const user = localStorage.getItem('user');
    if (user) {
      setIsLoggedIn(true);
    } else {
      setIsLoggedIn(false);
    }
  };
  
  window.addEventListener('storage', handleStorageChange);
  return () => window.removeEventListener('storage', handleStorageChange);
}, []);



  return (
    <div>
      <header className="navigation-bar">
        <div className='nav-container'>
          <h1 className='logo' onClick={() => navigate('/')}>BeautyBuddy</h1>

          <nav className='nav-bar-links'>
            <NavLink to="/" className={({ isActive }) => isActive ? 'active-link' : undefined}>Home</NavLink>
            <NavLink to="/my-routines" className={({ isActive }) => isActive ? 'active-link' : undefined}>My Routines</NavLink>
            <NavLink to="/community" className={({ isActive }) => isActive ? 'active-link' : undefined}>Community</NavLink>
            <NavLink to="/discussions" className={({ isActive }) => isActive ? 'active-link' : undefined}>Discussions</NavLink>
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
                  localStorage.removeItem("user");
                  setIsLoggedIn(false);
                  navigate("/");
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
              Login / Sign Up
            </button>
          )}

          {showAuth && (
            <AuthModal
             onClose={() => setShowAuth(false)}
             onLoginSuccess={() => {
               setIsLoggedIn(true);
               setShowAuth(false);
             }}
              />)}

          {/* optional user settings image
          <img
            className="user-settings-icon"
            src={userSettingsIcon}
            alt="User settings"
          /> 
          */}
          
        </div>
      </header>

      <main className='page-container'>
        <Outlet />
      </main>

    </div>
  );
}