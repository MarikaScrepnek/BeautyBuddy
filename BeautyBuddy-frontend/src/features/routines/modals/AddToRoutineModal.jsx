import { useState, useEffect } from "react";

import { getMakeupRoutines, getSkincareRoutines, getHaircareRoutine, addProductToRoutine, deleteRoutineItem } from "../api/routineApi";

import Toast from "../../../components/ui/Toast";

import "./AddToRoutineModal.css";
import Tooltip from "../../../components/ui/Tooltip";

export default function AddToRoutineModal({ baseCategoryName, productName, productId, shadeName = null, onClose, onRoutineChange }) {
    const [makeupRoutines, setMakeupRoutines] = useState([]);
    const [skincareRoutines, setSkincareRoutines] = useState([]);
    const [haircareRoutine, setHaircareRoutine] = useState([]);

    const [showToast, setShowToast] = useState(false);
    const [toastMessage, setToastMessage] = useState("");
    const [toastType, setToastType] = useState("info");

    const[isOtherRoutinesOpen, setIsOtherRoutinesOpen] = useState(false);

    function isProductInRoutine(routine) {
        if (!routine.items) return false;
        return routine.items.some(p => p.productId === productId && (!shadeName || p.shadeName === shadeName));
    }

    async function handleAddToRoutine(routineId, productId, shadeName) {
        try {
            await addProductToRoutine(routineId, productId, shadeName);
            setToastMessage("Product added to routine!");
            setToastType("success");
            setShowToast(true);

            // Optimistically update local routines
            setMakeupRoutines(prev =>
                prev.map(r =>
                    r.routineId === routineId
                        ? { ...r, items: [...(r.items || []), { productId, shadeName }] }
                        : r
                )
            );
            setSkincareRoutines(prev =>
                prev.map(r =>
                    r.routineId === routineId
                        ? { ...r, items: [...(r.items || []), { productId, shadeName }] }
                        : r
                )
            );
            setHaircareRoutine(prev =>
                prev && prev.routineId === routineId
                    ? { ...prev, items: [...(prev.items || []), { productId, shadeName }] }
                    : prev
            );

            if (onRoutineChange) {
                onRoutineChange("added", productId);
            }
        } catch (error) {
            console.error("Error adding product to routine:", error);
            setToastMessage("Error adding product to routine");
            setToastType("error");
            setShowToast(true);
        }
    }

    async function removeFromRoutine(routineId, productId, shadeName) {
        try {
            await deleteRoutineItem(routineId, productId, shadeName);
            setToastMessage("Product removed from routine!");
            setToastType("success");
            setShowToast(true);

            // Optimistically update local routines
            setMakeupRoutines(prev =>
                prev.map(r =>
                    r.routineId === routineId
                        ? { ...r, items: (r.items || []).filter(item => !(item.productId === productId && (!shadeName || item.shadeName === shadeName))) }
                        : r
                )
            );
            setSkincareRoutines(prev =>
                prev.map(r =>
                    r.routineId === routineId
                        ? { ...r, items: (r.items || []).filter(item => !(item.productId === productId && (!shadeName || item.shadeName === shadeName))) }
                        : r
                )
            );
            setHaircareRoutine(prev =>
                prev && prev.routineId === routineId
                    ? { ...prev, items: (prev.items || []).filter(item => !(item.productId === productId && (!shadeName || item.shadeName === shadeName))) }
                    : prev
            );

            if (onRoutineChange) {
                onRoutineChange("removed", productId);
            }
        } catch (error) {
            console.error("Error removing product from routine:", error);
            setToastMessage("Error removing product from routine");
            setToastType("error");
            setShowToast(true);
        }
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
                    <h4>to a {baseCategoryName} Routine</h4>
                </div>
                {baseCategoryName === "Makeup" && (
                    <>
                    {makeupRoutines.map(routine => (
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px' }} key={routine.routineId} className="routine-option">
                            <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                                <p>{routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}</p>
                                {isProductInRoutine(routine) && (
                                    <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                                )}
                            </div>
                                <div className="button-group">
                                {isProductInRoutine(routine) && (
                                    <Tooltip message="Remove from routine">
                                        <button className="add-button"
                                        onClick={() => removeFromRoutine(routine.routineId, productId, shadeName)}
                                        >
                                            -
                                        </button>
                                    </Tooltip>
                                )}
                                <Tooltip message="Add to routine">
                                <button className="add-button"
                                onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                                >
                                    +
                                </button>
                                </Tooltip>
                            </div>
                        </div>
                    ))}
                    </>
                )}
                {baseCategoryName === "Skincare" && (
                    <>
                    {skincareRoutines.map(routine => (
                        <div key={routine.routineId} className="routine-option">
                            <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                            <p>{routine.name || routine.timeOfDay}</p>
                            {isProductInRoutine(routine) && (
                                <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                            )}
                            </div>
                            <div className="button-group">
                            {isProductInRoutine(routine) && (
                                <Tooltip message="Remove from routine">
                                    <button className="add-button"
                                    onClick={() => removeFromRoutine(routine.routineId, productId, shadeName)}
                                    >
                                        -
                                    </button>
                                </Tooltip>
                            )}
                            <Tooltip message="Add to routine">
                            <button className="add-button"
                            onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                            >
                                +
                            </button>
                            </Tooltip>
                        </div>
                        </div>
                    ))}
                    </>
                )}
                {baseCategoryName === "Haircare" && haircareRoutine.routineId && (
                    <div key={haircareRoutine.routineId} className="routine-option">
                        <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                        <p>{haircareRoutine.name || "Haircare Routine"}</p>
                        {isProductInRoutine(haircareRoutine) && (
                            <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                        )}
                        </div>
                        <div className="button-group">
                        {isProductInRoutine(haircareRoutine) && (
                            <Tooltip message="Remove from routine">
                                <button className="add-button"
                                onClick={() => removeFromRoutine(haircareRoutine.routineId, productId, shadeName)}
                                >
                                    -
                                </button>
                            </Tooltip>
                        )}
                        <Tooltip message="Add to routine">
                        <button className="add-button"
                        onClick={() => handleAddToRoutine(haircareRoutine.routineId, productId, shadeName)}
                        >
                            +
                        </button>
                        </Tooltip>
                    </div>
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
                        {baseCategoryName !== "Makeup" && (
                        <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Makeup Routines</h1>
                            {makeupRoutines.map(routine => (
                                <div key={routine.routineId} className="routine-option">
                                    <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                                    <p>{routine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}</p>
                                    {isProductInRoutine(routine) && (
                                        <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                                    )}
                                    </div>
                                    <div className="button-group">
                                        {isProductInRoutine(routine) && (
                                            <Tooltip message="Remove from routine">
                                                <button className="add-button"
                                                onClick={() => removeFromRoutine(routine.routineId, productId, shadeName)}
                                                >
                                                    -
                                                </button>
                                            </Tooltip>
                                        )}
                                    <Tooltip message="Add to routine">
                                    <button className="add-button"
                                    onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                                    >
                                        +
                                    </button>
                                    </Tooltip>
                                </div>
                                </div>
                            ))}
                        </>
                        )}
                        {baseCategoryName !== "Skincare" && (
                        <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Skincare Routines</h1>
                            {skincareRoutines.map(routine => (
                                <div key={routine.routineId} className="routine-option">
                                    <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                                    <p>{routine.name || routine.timeOfDay}</p>
                                    {isProductInRoutine(routine) && (
                                        <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                                    )}
                                    </div>
                                    <div className="button-group">
                                    {isProductInRoutine(routine) && (
                                        <Tooltip message="Remove from routine">
                                            <button className="add-button"
                                            onClick={() => removeFromRoutine(routine.routineId, productId, shadeName)}
                                            >
                                                -
                                            </button>
                                        </Tooltip>
                                    )}
                                    <Tooltip message="Add to routine">
                                    <button className="add-button"
                                    onClick={() => handleAddToRoutine(routine.routineId, productId, shadeName)}
                                    >
                                        +
                                    </button>
                                    </Tooltip>
                                </div>
                                </div>
                            ))}
                        </>
                        )}
                        {baseCategoryName !== "Haircare" && haircareRoutine.routineId && (
                            <>
                            <h1 style={{ margin: "20px 0 10px 0" }}>Haircare Routine</h1>
                            <div key={haircareRoutine.routineId} className="routine-option">
                            <div style={{ display: 'flex', flexDirection: 'row', alignItems: 'center', gap: '8px' }}>
                                <p>{haircareRoutine.name || "Haircare Routine"}</p>
                                {isProductInRoutine(haircareRoutine) && (
                                    <span style={{color:'grey',fontSize:'14px', fontStyle:'italic'}}>(Already in this routine)</span>
                                )}
                            </div>
                                <div className="button-group">
                                {isProductInRoutine(haircareRoutine) && (
                                    <Tooltip message="Remove from routine">
                                        <button className="add-button"
                                        onClick={() => removeFromRoutine(haircareRoutine.routineId, productId, shadeName)}
                                        >
                                            -
                                        </button>
                                    </Tooltip>
                                )}
                                <Tooltip message="Add to routine">
                                <button className="add-button"
                                onClick={() => handleAddToRoutine(haircareRoutine.routineId, productId, shadeName)}
                                >
                                    +
                                </button>
                                </Tooltip>
                            </div>
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