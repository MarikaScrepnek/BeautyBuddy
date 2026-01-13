import ProductList from '../components/ProductList';
import { useState } from "react";

import './Home.css';

export default function Home() {
  const [searchQuery, setSearchQuery] = useState("");

  return (
    <div>
        <ProductList searchQuery={searchQuery} />

        <div className="product-not-found">
        <p>Don't see the product you're looking for? Send us a request!</p>
        <nav className='request-product-button'>
            <span>Request Product</span>
        </nav>
        </div>
    </div>
  );
}



/*attribution for user setting icon (put at bottom of page): <a href="https://www.flaticon.com/free-icons/setting" title="setting icons">Setting icons created by Tanah Basah - Flaticon</a>*/