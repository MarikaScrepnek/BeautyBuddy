import { useState, useEffect } from 'react';

import { getCurrentUser } from '../../api/authApi';

import Wishlist from './components/Wishlist';
import Routines from './components/Routines';

import "./MyRoutines.css";
import Searchbar from '../../components/common/Searchbar';
import MakeupRoutines from './components/MakeupRoutines';
import { getMakeupRoutines } from '../../api/routineApi';

import CreateRoutineModal from './components/CreateRoutineModal';

export default function MyRoutines() {
  // log in
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState("");

  // sidebar state
  const [selected, setSelected] = useState("Wishlist");
  const [selectedRoutine, setSelectedRoutine] = useState(null);
  const [makeupRoutines, setMakeupRoutines] = useState([]);

  const[createModalOpen, setCreateModalOpen] = useState(false);

  // on mount
  useEffect(() => {

    getCurrentUser()
      .then((user) => {
        setIsLoggedIn(true);
        setUsername(user.username);
      })
      .catch(() => setIsLoggedIn(false));

    getMakeupRoutines()
      .then((data) => {
        console.log("Makeup routines:", data);
        setMakeupRoutines(data);
      })
      .catch((err) => console.error("Error fetching makeup routines:", err));
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
            <li
              onClick={() => { setSelected("Wishlist"); setSelectedRoutine(null); }}
              style={{
                padding: "1rem",
                cursor: "pointer",
                background: selected === "Wishlist" ? "#e0e0e0" : "transparent",
                fontWeight: selected === "Wishlist" ? "bold" : "normal",
                borderLeft: selected === "Wishlist" ? "4px solid #6c63ff" : "4px solid transparent"
              }}
            >
              Wishlist
            </li>
            <li style={{padding: "1rem", fontWeight: "bold", fontSize: "1.1rem", color: "#6c63ff", background: "transparent"}}>
              Makeup
            </li>
            {makeupRoutines.map((routine) => (
              <li
                key={routine.routineId}
                onClick={() => { setSelected(routine.routineId); setSelectedRoutine(routine); }}
                style={{
                  padding: "0.5rem 1.5rem",
                  cursor: "pointer",
                  color: "#333",
                  textDecoration: "underline",
                  background: selected === routine.routineId ? "#e0e0e0" : "transparent",
                  borderLeft: selected === routine.routineId ? "4px solid #6c63ff" : "4px solid transparent"
                }}
              >
                {routine.name || routine.occasion}
              </li>
            ))}
            <button className="create-button" onClick={() => setCreateModalOpen(true)} style={{margin: "1rem", width: "calc(100% - 2rem)"}}>
                + New Routine
            </button>
            <li
              onClick={() => setSelected("Skincare")}
              style={{
                padding: "1rem",
                cursor: "pointer",
                background: selected === "Skincare" ? "#e0e0e0" : "transparent",
                fontWeight: selected === "Skincare" ? "bold" : "normal",
                borderLeft: selected === "Skincare" ? "4px solid #6c63ff" : "4px solid transparent"
              }}
            >
              Skincare
            </li>
            <li
              onClick={() => setSelected("Haircare")}
              style={{
                padding: "1rem",
                cursor: "pointer",
                background: selected === "Haircare" ? "#e0e0e0" : "transparent",
                fontWeight: selected === "Haircare" ? "bold" : "normal",
                borderLeft: selected === "Haircare" ? "4px solid #6c63ff" : "4px solid transparent"
              }}
            >
              Haircare
            </li>
        </ul>
      </aside>
      
      <main style={{ flex: 1, padding: "2rem" }}>

        {selected === "Haircare" && 
        <div>
          Haircare routine content goes here.
        </div>
        }

        {makeupRoutines.some(r => r.routineId === selected) && selectedRoutine &&
          <MakeupRoutines userName={username} routine={selectedRoutine} />
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

    {createModalOpen && <CreateRoutineModal onClose={() => setCreateModalOpen(false)} />}

  </div>
  );
}