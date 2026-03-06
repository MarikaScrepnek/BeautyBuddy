import { useState, useEffect } from 'react';

import { getMakeupRoutines } from '../../../api/routineApi';

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const[editModalOpen, setEditModalOpen] = useState(false);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name" style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                    <p>{userName}'s</p>
                    <h1> {routine.name || routine.occasion} </h1>
                    <p>Makeup Routine</p>
                </div>
                <p style={{ margin: 'auto 0' }}>Updated at {new Date(routine.updatedAt).toLocaleString()}</p>

                <button className="edit-button" onClick={() => setEditModalOpen(true)}>Edit Routine</button>

                {routine.notes && (
                    <div className="routine-notes">
                        <h3>Notes:</h3>
                        <p>{routine.notes}</p>
                    </div>
                )}
            </div>
            

            {routine.items && routine.items.length > 0 ? (
                <ul>
                    {routine.items
                        .slice() // copy array to avoid mutating original
                        .sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
                        .map(item => (
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


            {/* {editModalOpen && <CreateRoutineModal onClose={() => setEditModalOpen(false)} />} */}
        </div>
    );
}