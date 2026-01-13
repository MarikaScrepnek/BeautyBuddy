import { Routes, Route } from 'react-router-dom';

import Home from './pages/Home';
import MyRoutines from './pages/MyRoutines';
import Community from './pages/Community';
import Discussions from './pages/Discussions'; 

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/myroutines" element={<MyRoutines />} />
      <Route path="/community" element={<Community />} />
      <Route path="/discussions" element={<Discussions />} />
    </Routes>
  );
}

export default App