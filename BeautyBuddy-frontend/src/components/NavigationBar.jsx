import { Outlet, Link, useNavigate, useLocation } from 'react-router-dom';

import userSettingsIcon from '../assets/images/user-settings-icon.png';

import './NavigationBar.css';

export default function NavigationBar({ searchQuery, setSearchQuery }) {
  const navigate = useNavigate();
  const location = useLocation();

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      navigate(`/?q=${encodeURIComponent(searchQuery)}`);
    }
  };

  return (
    <div>
      <header className="navigation-bar">
        <div className='page-container nav-container'>
          <h1 className='logo'>BeautyBuddy</h1>

          <nav className='nav-bar-links'>
            <Link to="/">Home</Link>
            <Link to="/myroutines">My Routines</Link>
            <Link to="/community">Community</Link>
            <Link to="/discussions">Discussions</Link>
          </nav>

          <input
            type="text"
            className="search-bar"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            onKeyDown={handleKeyDown}
          />

          <nav className='login-button'>
            <span>Login</span>
          </nav>

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