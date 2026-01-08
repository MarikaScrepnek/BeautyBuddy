import ProductList from './components/ProductList';

import userSettingsIcon from './assets/images/user-settings-icon.png';

import './App.css';

function App() {
  return (
    <div>
      <header className="navigation-bar">
        <h1 className='logo'>BeautyBuddy</h1>

        <nav className='nav-bar-links'>
          <span>Home</span>
          <span>My Routines</span>
          <span>Community</span>
        </nav>

        <input
          type="text"
          className="search-bar"
          placeholder="Search products..."
        />

        <img
          className="user-settings-icon"
          src={userSettingsIcon}
          alt="User settings"
        />
      </header>
      <ProductList />
    </div>
  );
}

export default App


/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/