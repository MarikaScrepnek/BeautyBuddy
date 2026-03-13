import { useState, useEffect } from "react";

import { getMakeupRoutines, getSkincareRoutines, getHaircareRoutine, addProductToRoutine } from "../api/routineApi";

import Toast from "../../../components/ui/Toast";

import "./AddToRoutineModal.css";

export default function AddToRoutineModal({ baseCategory, productName, productId, shadeName, onClose }) {
    const [makeupRoutines, setMakeupRoutines] = useState([]);
    const [skincareRoutines, setSkincareRoutines] = useState([]);
    const [haircareRoutine, setHaircareRoutine] = useState([]);

    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState("");
    const [toastType, setToastType] = useState("info");

    const[isOtherRoutinesOpen, setIsOtherRoutinesOpen] = useState(false);

    // Helper to check if product is in routine
    function isProductInRoutine(routine) {
        if (!routine.items) return false;
        return routine.items.some(p => p.productId === productId);
    }

    async function handleAddToRoutine(routineId, productId, shadeName) {
        addProductToRoutine(routineId, productId, shadeName)
            .then(() => {
                console.log("Product added to routine successfully");
                setToastMessage("Product added to routine!");
                setToastType("success");
                setShowToast(true);
                setTimeout(onClose, 1000);
            })
            .catch((error) => {
                console.error("Error adding product to routine:", error);
                setToastMessage("Error adding product to routine");
                setToastType("error");
                setShowToast(true);
            });
    }

    useEffect(() => {
        getMakeupRoutines()
            .then(data => {
                setMakeupRoutines(data);
                console.log("Fetched makeup routines:", data);
            })
            .catch(error => {
                console.error("Error fetching makeup routines:", error);
            });
        getSkincareRoutines()
            .then(data => {
                setSkincareRoutines(data);
                console.log("Fetched skincare routines:", data);
            })
            .catch(error => {
                console.error("Error fetching skincare routines:", error);
            });
        getHaircareRoutine()
            .then(data => {
                setHaircareRoutine(data);
                console.log("Fetched haircare routine:", data);
            })
            .catch(error => {
                console.error("Error fetching haircare routine:", error);
            });
    }, []);

    useEffect(() => {
        const handleEsc = (e) => {
            if (e.key === 'Escape') {
                onClose();
            }
        };
        window.addEventListener('keydown', handleEsc);
        return () => {
            window.removeEventListener('keydown', handleEsc);
        }
    }, [onClose]);

    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <button className="close-button" onClick={onClose}>
                    &times;
                </button>
                <div className="modal-header">
                    <h2>Add</h2>
                    <h3>{productName}</h3>
                    <h4>to a {baseCategory} Routine</h4>
                </div>
                {baseCategory === "Makeup" && (
                    <>
                    {makeupRoutines.map(routine => (
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={routine.routineId} className="routine-option">
                            <p>{routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())} {isProductInRoutine(routine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                            <button className="add-button"
                            onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                            >
                                +
                            </button>
                        </div>
                    ))}
                    </>
                )}
                {baseCategory === "Skincare" && (
                    <>
                    {skincareRoutines.map(routine => (
                        <div key={routine.routineId} className="routine-option">
                            <p>{routine.name || routine.timeOfDay} {isProductInRoutine(routine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                            <button className="add-button"
                            onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                            >
                                +
                            </button>
                        </div>
                    ))}
                    </>
                )}
                {baseCategory === "Haircare" && haircareRoutine.routineId && (
                    <div key={haircareRoutine.routineId} className="routine-option">
                        <p>{haircareRoutine.name || "Haircare Routine"} {isProductInRoutine(haircareRoutine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                        <button className="add-button"
                        onClick={() => handleAddToRoutine(haircareRoutine.routineId, productId, shadeName)}
                        >
                            +
                        </button>
                    </div>
                )}
                <div className="other-routines-header">
                    <h1 className="modal-footer">Other Routines</h1>
                    <button
                        className="dropdown-button"
                        onClick={() => setIsOtherRoutinesOpen(!isOtherRoutinesOpen)}
                    >
                        {isOtherRoutinesOpen ? "▲" : "▼"}
                    </button>
                </div>

                {isOtherRoutinesOpen && (
                    <div className="other-routines-list">
                        {baseCategory !== "Makeup" && (
                        <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Makeup Routines</h1>
                            {makeupRoutines.map(routine => (
                                <div key={routine.routineId} className="routine-option">
                                    <p>{routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())} {isProductInRoutine(routine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                                    <button className="add-button"
                                    onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                                    >
                                        +
                                    </button>
                                </div>
                            ))}
                        </>
                        )}
                        {baseCategory !== "Skincare" && (
                        <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Skincare Routines</h1>
                            {skincareRoutines.map(routine => (
                                <div key={routine.routineId} className="routine-option">
                                    <p>{routine.name || routine.timeOfDay} {isProductInRoutine(routine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                                    <button className="add-button"
                                    onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                                    >
                                        +
                                    </button>
                                </div>
                            ))}
                        </>
                        )}
                        {baseCategory !== "Haircare" && haircareRoutine.routineId && (
                            <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Haircare Routine</h1>
                            <div key={haircareRoutine.routineId} className="routine-option">
                                <p>{haircareRoutine.name || "Haircare Routine"} {isProductInRoutine(haircareRoutine) && <span style={{color:'grey',fontSize:'14px', textDecoration:'italic'}}>(Already in this routine)</span>}</p>
                                <button className="add-button"
                                onClick={() => handleAddToRoutine(haircareRoutine.routineId, productId, shadeName)}
                                >
                                    +
                                </button>
                            </div>
                        </>
                        )}
                    </div>
                )}
                
            </div>
            <Toast
                message={showToast ? toastMessage : ""}
                type={toastType}
                onClose={() => setShowToast(false)}
            />
        </div>
    );
}