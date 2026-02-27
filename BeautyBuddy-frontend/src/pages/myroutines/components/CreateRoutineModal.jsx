import { useEffect } from 'react';
import { useState } from 'react';

import './CreateRoutineModal.css';

export default function CreateRoutineModal( {onClose}) {
    const [category, setCategory] = useState("skincare");

    async function handleCreateRoutine() {
        onClose();
    }

    //escape to close modal
    useEffect(() => {
        const handleEsc = (e) => {
            if (e.key === 'Escape') {
                onClose();
            }
        };
        window.addEventListener('keydown', handleEsc);
        return () => {
            window.removeEventListener('keydown', handleEsc);
        };
    }, [onClose]);

    return (
        <div className="modal-overlay">
            <div className="modal-card">

                <button className="close-button" onClick={onClose}>
                    &times;
                </button>

                <h2>Create a Routine</h2>

                <p>Routine name: </p>
                <input type="text" placeholder="Enter routine name" />

                <p>Category: </p>
                <select value={category} onChange={(e) => setCategory(e.target.value)}>
                    <option value="">Select category</option>
                    <option value="skincare">Skincare</option>
                    <option value="makeup">Makeup</option>
                    <option value="haircare">Haircare</option>
                    <option value="bodycare">Bodycare</option>
                </select>

                {category === "skincare" && (
                    <>
                    <p>Time of Day: </p>
                    <select>
                        <option value="">Select time of day</option>
                        <option value="morning">Morning</option>
                        <option value="afternoon">Afternoon</option>
                        <option value="evening">Evening</option>
                    </select>
                    </>
                )}

                {category === "makeup" && (
                    <>
                    <p>Occasion: </p>
                    <select>
                        <option value="">Select occasion</option>
                        <option value="work">Everyday</option>
                        <option value="casual">Casual</option>
                        <option value="formal">Glam</option>
                    </select>
                    </>
                )}

                <p>Notes: </p>
                <textarea placeholder="Additional notes about your routine..."></textarea>

                <button className="save-button" onClick={handleCreateRoutine}>Save Routine</button>

            </div>
        </div>
    );
}