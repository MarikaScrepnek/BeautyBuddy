import ProductList from './components/ProductList';

import './App.css';

function App() {
  return (
    <div>
      <header className="navigation-bar">
        <h1>BeautyBuddy</h1>
      </header>
      <h2>Products</h2>
      <ProductList />
    </div>
  );
}

export default App
