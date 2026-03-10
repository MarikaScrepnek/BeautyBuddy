import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';

import Home from './pages/Home';
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
          <Route path="/" element={<Home />} />
          <Route path="/my-routines" element={<MyRoutines />} />
          <Route path="/discussions" element={<Discussions />} />
          <Route path="/feed" element={<Feed />} />
          <Route path='/:productId' element={<ProductDetails />} />
        </Route>
      </Routes>
    </>
  );
}

export default App

//$env:JWT_SECRET_KEY="RDf2fJEaXHWQWtu9zFu59E9Ncgsg1aAUJGbFYc1dMc5yCxA8sdno8HWu9WsuGL51KnA0AGXrauixJ3ZNLAnE3rB9Pdq6s7i427ac"
//mvn spring-boot:run

//npm.cmd run dev