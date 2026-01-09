import ProductList from './components/ProductList';

import userSettingsIcon from './assets/images/user-settings-icon.png';

import './App.css';

function App() {
  return (
    <div>
      <header className="navigation-bar">
        <div className='page-container nav-container'>
          <h1 className='logo'>BeautyBuddy</h1>

          <nav className='nav-bar-links'>
            <span>Home</span>
            <span>My Routines</span>
            <span>Community</span>
            <span>Discussions</span>
          </nav>

          <input
            type="text"
            className="search-bar"
            placeholder="Search products..."
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
        <ProductList />
      </div>

    </div>
  );
}

export default App


/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/