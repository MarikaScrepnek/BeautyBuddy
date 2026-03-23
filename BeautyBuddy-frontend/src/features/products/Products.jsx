import ProductList from './components/ProductList';
import { useSearchParams } from 'react-router-dom';
import { useState } from 'react';

import './Products.css';

import RequestModal from './modals/RequestModal';

export default function Products() {
  const [params] = useSearchParams();
  const searchQuery = params.get("q") || "";

  const [showRequest, setShowRequest] = useState(false);
  const [isLoading, setIsLoading] = useState(false);

  return (
    <div>
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