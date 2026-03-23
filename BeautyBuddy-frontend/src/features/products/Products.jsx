import ProductList from './components/ProductList';
import { useSearchParams } from 'react-router-dom';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

import './Products.css';

import RequestModal from './modals/RequestModal';
import { FaSearch } from 'react-icons/fa';

export default function Products() {
  const [inputValue, setInputValue] = useState("");
  const [params] = useSearchParams();
  const [searchQuery, setSearchQuery] = useState(params.get("q") || "");

  const [showRequest, setShowRequest] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  function onChange(e) {
    setInputValue(e.target.value);
  }

  const handleKeyDown = (e) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const handleSearch = () => {
    setSearchQuery(inputValue);
  };

  return (
    <div>
      <div className="search-container">
        <input
          type="text"
          className="search-bar"
          placeholder="Search products..."
          value={inputValue}
          onChange={onChange}
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

      <ProductList searchQuery={searchQuery} onLoadingChange={setIsLoading} />

      {!isLoading && (
      <div className="product-not-found">
        <p>Don't see the product you're looking for? Send us a request!</p>
        <button
        className="request-product-button"
        onClick={() => setShowRequest(true)}
        >
          Request Product
        </button>
      </div>
      )}
      {showRequest && (
        <RequestModal onClose={() => setShowRequest(false)} />
      )}

    </div>
  );
}



/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/