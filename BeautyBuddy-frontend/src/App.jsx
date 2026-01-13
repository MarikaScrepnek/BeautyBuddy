import { Routes, Route } from 'react-router-dom';
import { useState } from 'react';

import Home from './pages/Home';
import MyRoutines from './pages/MyRoutines';
import Community from './pages/Community';
import Discussions from './pages/Discussions'; 
import NavigationBar from './components/NavigationBar';

function App() {
  const [searchQuery, setSearchQuery] = useState('');

  return (
    <>
      <Routes>
        <Route element={<NavigationBar searchQuery={searchQuery} setSearchQuery={setSearchQuery} />}>
          <Route path="/" element={<Home />} />
          <Route path="/myroutines" element={<MyRoutines />} />
          <Route path="/community" element={<Community />} />
          <Route path="/discussions" element={<Discussions />} />
        </Route>
      </Routes>
    </>
  );
}

export default App