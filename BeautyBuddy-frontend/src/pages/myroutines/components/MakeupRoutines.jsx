import { useState, useEffect } from 'react';

import { getMakeupRoutines } from '../../../api/routineApi';
import CreateRoutineModal from './CreateRoutineModal';

import './MakeupRoutines.css';

export default function MakeupRoutines() {
    const[createModalOpen, setCreateModalOpen] = useState(false);
    const[makeupRoutines, setMakeupRoutines] = useState([]);

    useEffect(() => {
        getMakeupRoutines()
            .then(data => {
                setMakeupRoutines(data);
            })
            .catch(error => {
                console.error("Error fetching makeup routines:", error);
            });
    }, []);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <h1>Makeup Routines</h1>
                <button
                className="create-button"
                onClick={() => setCreateModalOpen(true)}
                >
                    +
                </button>
            </div>
            
            {makeupRoutines.map(routine => (
                <div key={routine.routineId} className="routine-item">

                    <h3>{routine.name || routine.occasion}</h3>
                    {routine.items && routine.items.length > 0 ? (
                        <ul>
                            {routine.items.map(item => (
                                <div className="routine-item" key={item.productId}>
                                    <p>{item.productName}</p>
                                    <p>{item.productShadeName}</p>
                                    <p>{item.productBrand}</p>
                                    <p>{item.category}</p>
                                    <img src={item.productImageUrl} alt={item.productName} style={{ width: '50px', height: '50px' }} />
                                </div>
                            ))}
                        </ul>
                    ) : (
                        <p>No items in this routine.</p>
                    )}
                </div>
            ))}


            {createModalOpen && <CreateRoutineModal onClose={() => setCreateModalOpen(false)} />}
        </div>
    );
}