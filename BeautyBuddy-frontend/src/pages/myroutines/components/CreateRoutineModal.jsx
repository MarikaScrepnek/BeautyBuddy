import { useEffect } from 'react';
import { useState } from 'react';

import './CreateRoutineModal.css';
import { createMakeupRoutine } from '../../../api/routineApi';

export default function CreateRoutineModal( {onClose} ) {
    const [occasion, setOccasion] = useState("CASUAL");
    const [name, setName] = useState("");
    const [notes, setNotes] = useState("");

    async function handleCreateRoutine() {
        if (!name) {
            alert("Please enter a routine name.");
            return;
        }
        createMakeupRoutine(occasion, name, notes)
            .then((data) => {
                console.log("Routine created:", data);
                onClose();
            })
            .catch((error) => {
                console.error("Error creating routine:", error);
            });
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

                <h2>Create a Makeup Routine</h2>

                <div className='modal-input-container'>

                    <div className='modal-input-section'>
                        <p className='modal-header-text'>Occasion: </p>
                        <select className="modal-selector" value={occasion} onChange={(e) => setOccasion(e.target.value)}>
                            <option value="">Select occasion...</option>
                            <option value="EVENT">Event</option>
                            <option value="OTHER">Other</option>
                        </select>
                    </div>

                    <div className='modal-input-section'>
                        <p className='modal-header-text'>Routine name: </p>
                        <input className="modal-input" type="text" placeholder="Enter routine name" value={name} onChange={(e) => setName(e.target.value)}/>
                    </div>

                    <div className='modal-input-section'>
                        <p className='modal-header-text'>Notes (optional): </p>
                        <textarea className="modal-textarea" placeholder="Additional notes about your routine..." value={notes} onChange={(e) => setNotes(e.target.value)}></textarea>
                    </div>

                </div>
                    
                <div style={{display: 'flex', justifyContent: 'center'}}>
                    <button className="save-button" onClick={handleCreateRoutine}>Save Routine</button>
                </div>

            </div>
        </div>
    );
}