import { useState, useEffect } from "react";

import { getMakeupRoutines, getSkincareRoutines, getHaircareRoutine, addProductToRoutine } from "../../api/routineApi";

import Toast from "../../components/Toast";

export default function AddToRoutineModal({ baseCategory, productName, productId, shadeName, onClose }) {
    const [makeupRoutines, setMakeupRoutines] = useState([]);
    const [skincareRoutines, setSkincareRoutines] = useState([]);
    const [haircareRoutine, setHaircareRoutine] = useState([]);

    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState("");
    const [toastType, setToastType] = useState("info");

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
                <h2>Add</h2>
                <h2>{productName}</h2>
                <h2>to a {baseCategory} Routine</h2>
                {baseCategory === "Makeup" && (
                    <>
                    {makeupRoutines.map(routine => (
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={routine.routineId} className="routine-option">
                            <p>{routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}</p>
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
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={routine.routineId} className="routine-option">
                            <p>{routine.name || routine.timeOfDay}</p>
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
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={haircareRoutine.routineId} className="routine-option">
                        <p>{haircareRoutine.name || "Haircare Routine"}</p>
                        <button className="add-button"
                        onClick={() => handleAddToRoutine(haircareRoutine.routineId, productId, shadeName)}
                        >
                            +
                        </button>
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