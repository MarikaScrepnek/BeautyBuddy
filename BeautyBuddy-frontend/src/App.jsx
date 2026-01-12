import ProductList from './components/ProductList';

import { useState } from "react";

import userSettingsIcon from './assets/images/user-settings-icon.png';

import './App.css';

import { Routes, Route, Link } from "react-router-dom";
import Home from './pages/Home';
import MyRoutines from './pages/MyRoutines';
import Community from './pages/Community';
import Discussions from './pages/Discussions';

function App() {
  const [searchQuery, setSearchQuery] = useState("");

  return (
    <div>
      <header className="navigation-bar">
        <div className='page-container nav-container'>
          <h1 className='logo'>BeautyBuddy</h1>

          <nav className='nav-bar-links'>
            <Link to="/">Home</Link>
            <Link to="/myroutines">MyRoutines</Link>
            <Link to="/community">Community</Link>
            <Link to="/discussions">Discussions</Link>
          </nav>

          <input
            type="text"
            className="search-bar"
            placeholder="Search products..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />

          <nav className='login-button'>
            <span>Login</span>
          </nav>
          
          {/* page will either show login button or this if user is logged in, with users username to the left
          <img
            className="user-settings-icon"
            src={userSettingsIcon}
            alt="User settings"
          />
          */}
        </div>
      </header>

      <div className='page-container'>
        <ProductList searchQuery={searchQuery} />
      </div>

      <div className="product-not-found">
        <p>Don't see the product you're looking for? Send us a request!</p>
        <nav className='request-product-button'>
          <span>Request Product</span>
        </nav>
      </div>

      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/myroutines" element={<MyRoutines />} />
        <Route path="/community" element={<Community />} />
        <Route path="/discussions" element={<Discussions />} />
      </Routes>

    </div>
  );
}

export default App


/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/