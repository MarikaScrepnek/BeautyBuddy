import { useState, useEffect } from 'react';

import { getCurrentUser } from '../../api/authApi';

import Wishlist from './components/Wishlist';
import Routines from './components/Routines';

import "./MyRoutines.css";
import Searchbar from '../../components/common/Searchbar';
import SelectedRoutine from './components/SelectedRoutine';
import { getHaircareRoutine, getMakeupRoutines, getSkincareRoutines } from '../../api/routineApi';

import CreateRoutineModal from './components/CreateRoutineModal';

export default function MyRoutines() {
  // log in
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [username, setUsername] = useState("");

  // sidebar state
  const [selected, setSelected] = useState("Wishlist");
  const [selectedRoutine, setSelectedRoutine] = useState(null);

  const [makeupRoutines, setMakeupRoutines] = useState([]);
  const [skincareRoutines, setSkincareRoutines] = useState([]);
  const [haircareRoutine, setHaircareRoutine] = useState([]);

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

      getSkincareRoutines()
      .then((data) => {
        console.log("Skincare routines:", data);
        setSkincareRoutines(data);
      })
      .catch((err) => console.error("Error fetching skincare routines:", err));

      getHaircareRoutine()
      .then((data) => {
        console.log("Haircare routine:", data);
        setHaircareRoutine(data);
      })
      .catch((err) => console.error("Error fetching haircare routine:", err));
  }, []);

  return (
    <>
    {!isLoggedIn ? (

      <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh'}}>
          <h1 style={{fontSize: '2rem', color: '#555'}}>Login to view your routines...</h1>
      </div>
            
    ) : (

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
                {routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}
              </li>
            ))}
            <button className="create-button" onClick={() => setCreateModalOpen(true)} style={{margin: "1rem", width: "calc(100% - 2rem)"}}>
                + New Routine
            </button>

            <li style={{padding: "1rem", fontWeight: "bold", fontSize: "1.1rem", color: "#6c63ff", background: "transparent"}}>
              Skincare
            </li>
            {skincareRoutines.map((routine) => (
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
                {routine.timeOfDay}
              </li>
            ))}

            <li style={{padding: "1rem", fontWeight: "bold", fontSize: "1.1rem", color: "#6c63ff", background: "transparent"}}>
              Haircare
            </li>
              <li
                onClick={() => { setSelected(haircareRoutine.routineId); setSelectedRoutine(haircareRoutine); }}
                style={{
                  padding: "0.5rem 1.5rem",
                  cursor: "pointer",
                  color: "#333",
                  textDecoration: "underline",
                  background: selected === haircareRoutine.routineId ? "#e0e0e0" : "transparent",
                  borderLeft: selected === haircareRoutine.routineId ? "4px solid #6c63ff" : "4px solid transparent"
                }}
              >
                Haircare Routine
              </li>
        </ul>
      </aside>
      
      <main style={{ flex: 1, padding: "0 2rem" }}>

        {selected === "Wishlist" && 
        <Wishlist isLoggedIn={isLoggedIn} />
        }

        {makeupRoutines.some(r => r.routineId === selected) && selectedRoutine &&
          <SelectedRoutine userName={username} routine={selectedRoutine} routineType="Makeup" />
        }

        {skincareRoutines.some(r => r.routineId === selected) && selectedRoutine &&
          <SelectedRoutine userName={username} routine={selectedRoutine} routineType="Skincare" />
        }

        {haircareRoutine.routineId === selected && selectedRoutine &&
          <SelectedRoutine userName={username} routine={selectedRoutine} routineType="Haircare" />
        }

      </main>
    </div>

    {createModalOpen && <CreateRoutineModal onClose={() => setCreateModalOpen(false)} />}

  </div>
  )}
  </>
  );
}