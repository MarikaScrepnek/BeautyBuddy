import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';

import Home from './pages/Home';
import MyRoutines from './pages/myroutines/MyRoutines';
import Discussions from './pages/Discussions'; 
import NavigationBar from './components/NavigationBar';
import ProductDetails from './pages/ProductDetails';

function App() {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <>
      <Routes>
        <Route element={<NavigationBar searchQuery={searchQuery} setSearchQuery={setSearchQuery} />}>
          <Route path="/" element={<Home />} />
          <Route path="/my-routines" element={<MyRoutines />} />
          <Route path="/discussions" element={<Discussions />} />
          <Route path='/:productId' element={<ProductDetails />} />
        </Route>
      </Routes>
    </>
  );
}

export default App