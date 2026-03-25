import { Outlet, useNavigate } from 'react-router-dom';
import { NavLink } from 'react-router-dom';
import { useEffect, useState } from 'react';

import './NavigationBar.css';

import { getCurrentUser, logoutUser } from '../features/auth/api/authApi';
import AuthModal from '../features/auth/modals/AuthModal';
import Tooltip from './ui/Tooltip';
import UserDropdown from './UserDropdown';

export default function NavigationBar() {
  const navigate = useNavigate();

  const [showAuth, setShowAuth] = useState(false);

  const[isLoggedIn, setIsLoggedIn] = useState(false);

  const [currentUser, setCurrentUser] = useState(null);

  useEffect(() => {
    getCurrentUser()
      .then((user) => {
        setCurrentUser(user);
        setIsLoggedIn(true);
      })
      .catch(() => {
        setCurrentUser(null);
        setIsLoggedIn(false);
      });
    const handleAuthLogin = () => {
      getCurrentUser()
        .then((user) => {
          setCurrentUser(user);
          setIsLoggedIn(true);
        })
        .catch(() => {
          setCurrentUser(null);
          setIsLoggedIn(false);
        });
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
            {!isLoggedIn ? (
              <>
                <Tooltip message="Please log in to view your routines" position="bottom">
                  <p className="not-logged-in-links">Routines</p>
                </Tooltip>
                <Tooltip message={"Please log in to view your feed"} position="bottom">
                  <p className="not-logged-in-links">Feed</p>
                </Tooltip>
              </>
            ) : (
              <>
              <NavLink to="/profile" className={({ isActive }) => isActive ? 'active-link' : undefined}>Profile</NavLink>
              <NavLink to="/my-feed" className={({ isActive }) => isActive ? 'active-link' : undefined}>Feed</NavLink>
              </>
            )}
          </nav>

          {isLoggedIn && currentUser?.username ? (
            <div className="user-section">
              <UserDropdown
                user={currentUser}
                onSignOut={() => {
                  logoutUser()
                    .then(() => {
                      setCurrentUser(null);
                      setIsLoggedIn(false);
                      navigate("/");
                    })
                    .catch((error) => console.error("Logout failed:", error));
                }}
              />
            </div>
          ) : (
            <button
              className="login-button"
              onClick={() => setShowAuth(true)}
            >
              Login / Create Account
            </button>
          )}
          
        </div>
      </header>

      <main className='page-container'>
        <Outlet />
      </main>

      {showAuth && (
        <AuthModal
          onClose={() => setShowAuth(false)}
          onLoginSuccess={async () => {
          try {
            const user = await getCurrentUser();
            setCurrentUser(user);
            setIsLoggedIn(true);
          } catch {
            setCurrentUser(null);
            setIsLoggedIn(false);
          }
          setShowAuth(false);
          }}
        />
      )}

    </div>
  );
}