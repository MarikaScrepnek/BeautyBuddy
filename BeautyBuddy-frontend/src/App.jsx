import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';
import { Navigate } from 'react-router-dom';

import Products from './pages/Products';
import MyRoutines from './pages/myroutines/MyRoutines';
import Discussions from './pages/Discussions'; 
import NavigationBar from './components/NavigationBar';
import ProductDetails from './pages/ProductDetails';
import Feed from './pages/feed/Feed';

function App() {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <>
      <Routes>
        <Route element={<NavigationBar searchQuery={searchQuery} setSearchQuery={setSearchQuery} />}>
          <Route path="/" element={<Navigate to="/products" />} />
          <Route path="/products" element={<Products />} />
          <Route path="/discussions" element={<Discussions />} />
          <Route path="/my-routines" element={<MyRoutines />} />
          <Route path="/my-feed" element={<Feed />} />
          <Route path='/products/:productId' element={<ProductDetails />} />
        </Route>
      </Routes>
    </>
  );
}

export default App

//$env:JWT_SECRET_KEY="RDf2fJEaXHWQWtu9zFu59E9Ncgsg1aAUJGbFYc1dMc5yCxA8sdno8HWu9WsuGL51KnA0AGXrauixJ3ZNLAnE3rB9Pdq6s7i427ac"
//mvn spring-boot:run

//npm.cmd run dev