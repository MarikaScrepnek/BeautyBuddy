import { useState, useEffect } from 'react';

import { getMakeupRoutines } from '../../../api/routineApi';
import CreateRoutineModal from './CreateRoutineModal';

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const[createModalOpen, setCreateModalOpen] = useState(false);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name" style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                    <p>{userName}'s</p>
                    <h1> {routine.name || routine.occasion} </h1>
                    <p>Makeup Routine</p>
                </div>
                <p style={{ margin: 'auto 0' }}>Updated at {new Date(routine.updatedAt).toLocaleString()}</p>

                <button className="edit-button" onClick={() => setCreateModalOpen(true)}>Edit Routine</button>

                {routine.notes && (
                    <div className="routine-notes">
                        <h3>Notes:</h3>
                        <p>{routine.notes}</p>
                    </div>
                )}
            </div>
            

            {routine.items && routine.items.length > 0 ? (
                <ul>
                    {routine.items.map(item => (
                        <div className="routine-item" key={item.id}>
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


            {createModalOpen && <CreateRoutineModal onClose={() => setCreateModalOpen(false)} />}
        </div>
    );
}