import { useState, useEffect } from 'react';

import { getCurrentUser } from '../../api/authApi';

import Wishlist from './components/Wishlist';
import Routines from './components/Routines';

import "./MyRoutines.css";
import Searchbar from '../../components/common/Searchbar';
import MakeupRoutines from './components/MakeupRoutines';

export default function MyRoutines() {
  // log in
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState("");

  // sidebar state
  const [selected, setSelected] = useState("Wishlist");
  const menuItems = ["Wishlist", "Makeup", "Skincare", "Haircare"];

  // on mount
  useEffect(() => {

    getCurrentUser()
      .then((user) => {
        setIsLoggedIn(true);
        setUsername(user.username);
      })
      .catch(() => setIsLoggedIn(false));
  }, []);

  return (
  <div className='routines-page-container'>

    {isLoggedIn && (
      <p style={{textAlign: "center", textDecoration: "underline", textDecorationColor: "#f0cef0"}}>Welcome back, {username}!</p>
    )}

    <div style={{display: "flex", flexDirection: "row", minHeight: "80vh"}}>
      
      <aside className='routines-sidebar' style={{ width: "200px", background: "#f7f7f7", padding: "1rem 0", borderRight: "1px solid #eee" }}>
        <h2 style={{textAlign: "center"}}>All Lists</h2>
        <ul style={{ listStyle: "none", padding: 0, margin: 0 }}>
          {menuItems.map((item) => (
            <li
              key={item}
              onClick={() => setSelected(item)}
              style={{
                padding: "1rem",
                cursor: "pointer",
                background: selected === item ? "#e0e0e0" : "transparent",
                fontWeight: selected === item ? "bold" : "normal",
                borderLeft: selected === item ? "4px solid #6c63ff" : "4px solid transparent"
              }}
            >
              {item}
            </li>
          ))}
          <li>
            <Searchbar placeholder="Enter a date..." onSearch={(query) => console.log("Searching for:", query)} />
            <p>Enter a date and we will show what products were in your routine:</p>
          </li>
        </ul>
      </aside>
      
      <main style={{ flex: 1, padding: "2rem" }}>

        {selected === "Haircare" && 
        <div>
          Haircare routine content goes here.
        </div>
        }

        {selected === "Makeup" &&
          <MakeupRoutines />
        }

        {selected === "Skincare" && 
        <div>
          Skincare routine content goes here.
        </div>
        }

        {selected === "Wishlist" && 
        <Wishlist isLoggedIn={isLoggedIn} />
        }

      </main>
    </div>

  </div>
  );
}