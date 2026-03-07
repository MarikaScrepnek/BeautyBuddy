import { useState, useEffect } from 'react';

import { getMakeupRoutines } from '../../../api/routineApi';

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const[editModalOpen, setEditModalOpen] = useState(false);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name">
                    <p>{userName}'s</p>
                    <h1>{routine.name || routine.occasion}</h1>
                    <p>Makeup Routine</p>
                </div>
            </div>

            <div className="routine-meta">
                <p>
                    Updated {new Date(routine.updatedAt).toLocaleString('en-US', {
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric',
                        hour: 'numeric',
                        minute: '2-digit',
                        hour12: true
                    })}
                </p>
                <button className="edit-button" onClick={() => setEditModalOpen(true)}>Edit Routine</button>
            </div>

            {routine.notes && (
                <div className="routine-notes">
                    <h3>Notes:</h3>
                    <p>{routine.notes}</p>
                </div>
            )}

            <ul className="routine-items-list">
            {routine.items && routine.items.length > 0 ? (
                routine.items
                    .slice() // copy array to avoid mutating original
                    .sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
                    .map(item => (
                    <li className="routine-item" key={item.id}>
                        <img src={item.productImageUrl} alt={item.productName} className="routine-item-img" />
                        <div className="routine-item-info">
                            <p className="routine-item-name">{item.productName}</p>
                            <p>{item.productBrand}</p>
                            {item.productShadeName && (
                                <p>in {item.productShadeName}</p>
                            )}
                        </div>
                    </li>
                ))
            ) : (
                <p>No items in this routine.</p>
            )}
            </ul>

            {/* {editModalOpen && <CreateRoutineModal onClose={() => setEditModalOpen(false)} />} */}
        </div>
    );
}