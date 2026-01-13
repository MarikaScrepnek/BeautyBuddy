import ProductList from '../components/ProductList';
import { useSearchParams } from 'react-router-dom';

import './Home.css';

export default function Home() {
  const [params] = useSearchParams();
  const searchQuery = params.get("q") || "";

  return (
    <div>
      <ProductList searchQuery={searchQuery} />

      <div className="product-not-found">
        <p>Don't see the product you're looking for? Send us a request!</p>
        <nav className="request-product-button">
          <span>Request Product</span>
        </nav>
      </div>
    </div>
  );
}



/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/