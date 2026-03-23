import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';
import { Navigate } from 'react-router-dom';

import Products from '../features/products/Products';
import MyRoutines from '../features/routines/MyRoutines';
import Discussions from '../features/discussions/Discussions'; 
import NavigationBar from '../components/NavigationBar';
import ProductDetails from '../features/products/ProductDetails';
import Feed from '../features/feed/Feed';

function App() {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <>
      <Routes>
        <Route element={<NavigationBar searchQuery={searchQuery} setSearchQuery={setSearchQuery} />}>
          <Route path="/" element={<Navigate to="/products" />} />
          <Route path="/products" element={<Products />} />
          <Route path="/discussions" element={<Discussions />} />
          <Route path="/profile" element={<MyRoutines />} />
          <Route path="/my-feed" element={<Feed />} />
          <Route path='/products/:productId' element={<ProductDetails />} />
          <Route path="/settings" element={<div>Settings Page (to be implemented)</div>} />
        </Route>
      </Routes>
    </>
  );
}

export default App

//$env:JWT_SECRET_KEY="RDf2fJEaXHWQWtu9zFu59E9Ncgsg1aAUJGbFYc1dMc5yCxA8sdno8HWu9WsuGL51KnA0AGXrauixJ3ZNLAnE3rB9Pdq6s7i427ac"
//mvn spring-boot:run

//npm.cmd run dev