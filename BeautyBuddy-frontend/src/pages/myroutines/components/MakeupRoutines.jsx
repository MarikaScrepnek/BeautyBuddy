import { useEffect, useState } from 'react';

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const[isEditingRoutine, setIsEditingRoutine] = useState(false);
    const[editedRoutine, setEditedRoutine] = useState(routine);
    
    const[editNameModalOpen, setEditNameModalOpen] = useState(false);
    const[editNotesModalOpen, setEditNotesModalOpen] = useState(false);

    const[dragEnabled, setDragEnabled] = useState(false);
    const[dragIndex, setDragIndex] = useState(null);

    function handleNameChange(newName) {
        setEditedRoutine(prev => ({ ...prev, name: newName }));
    }

    function handleNotesChange(newNotes) {
        setEditedRoutine(prev => ({ ...prev, notes: newNotes }));
    }

    function handleDragStart(index) {
        setDragIndex(index);
    }

    function handleDragOver(e) {
        e.preventDefault(); // allows drop
    }

    function handleDrop(dropIndex) {
        if (dragIndex === null || dragIndex === dropIndex) return;

        const newItems = [...editedRoutine.items].sort((a,b) => (a.order ?? 0) - (b.order ?? 0));

        const draggedItem = newItems.splice(dragIndex, 1)[0];
        newItems.splice(dropIndex, 0, draggedItem);

        const reordered = newItems.map((item, i) => ({
            ...item,
            order: i
        }));

        handleReorderItems(reordered);
        setDragIndex(null);
    }

    function handleReorderItems(reorderedItems) {
        setEditedRoutine(prev => ({
            ...prev,
            items: reorderedItems
        }));
    }

    function handleDeleteItem(itemId) {
        setEditedRoutine(prev => ({
            ...prev,
            items: prev.items.filter(item => item.id !== itemId)
        }));
    }

    async function handleSaveChanges() {
        //api call to save changes
        setIsEditingRoutine(false);
        //reload routine data from backend to ensure we have the latest info (including updatedAt)
    }

    useEffect(() => {
        setEditedRoutine(routine);
    }, [routine]);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name">
                    <p>{userName}'s</p>
                    <div className='routine-name-container' style={{ display: 'flex', alignItems: 'center' }}>
                        <h1 style={{ flex: 1, textAlign: 'center' }}>
                            {editedRoutine.name ? editedRoutine.name.toUpperCase() : editedRoutine.occasion}
                        </h1>
                        {(editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && isEditingRoutine ? (
                            <button className="edit-name-button" onClick={() => setEditNameModalOpen(true)}>Edit Routine Name</button>
                        ) : (
                            <div style={{ width: '120px' }} />
                        )}
                    </div>
                    <p>Makeup Routine</p>
                </div>
            </div>

            <div className="routine-meta">
                <p>
                    Updated {new Date(editedRoutine.updatedAt).toLocaleString('en-US', {
                        month: 'long',
                        day: 'numeric',
                        year: 'numeric',
                        hour: 'numeric',
                        minute: '2-digit',
                        hour12: true
                    })}
                </p>
                {isEditingRoutine ? (
                    <button className="edit-button" onClick={handleSaveChanges}>Save Changes</button>
                ) : (
                    <button className="edit-button" onClick={() => setIsEditingRoutine(true)}>Edit Routine</button>
                )}
            </div>

            {editedRoutine.notes ? (
                <div className="routine-notes">
                    <h3>Notes:</h3>
                    <p>{editedRoutine.notes}</p>
                    {isEditingRoutine && (
                        <button className="edit-notes-button" onClick={() => setEditNotesModalOpen(true)}>Edit Notes</button>
                    )}
                </div>
            ) : (
                isEditingRoutine && (
                    <button className="edit-notes-button" onClick={() => setEditNotesModalOpen(true)}>Add Notes</button>
                )
            )}

            <ul className="routine-items-list">
            {editedRoutine.items && editedRoutine.items.length > 0 ? (
                editedRoutine.items
                    .slice() // copy array to avoid mutating original
                    .sort((a, b) => (a.order ?? 0) - (b.order ?? 0))
                    .map(item => (
                    <li
                        className="routine-item"
                        key={item.id}
                        draggable={isEditingRoutine && dragEnabled}
                        onDragStart={() => handleDragStart(item.order)}
                        onDragOver={handleDragOver}
                        onDrop={() => handleDrop(item.order)}
                        onClick={() => {
                            if (!isEditingRoutine) {
                                window.open(`/${item.productId}`, '_blank')
                            }
                        }}
                    >
                        {isEditingRoutine && (
                            <div className="delete-item-button" onClick={(e) => {e.stopPropagation(); handleDeleteItem(item.id);}}>
                                <span>Delete</span>
                            </div>
                        )}
                        <img src={item.productImageUrl} alt={item.productName} className="routine-item-img" />
                        <div className="routine-item-info">
                            <p className="routine-item-name">{item.productName}</p>
                            <p>{item.productBrand}</p>
                            {item.productShadeName && (
                                <p>in {item.productShadeName}</p>
                            )}
                        </div>
                        {isEditingRoutine && (
                            <div
                                className="reorder-item-button"
                                onMouseDown={() => setDragEnabled(true)}
                                onMouseUp={() => setDragEnabled(false)}
                                onMouseLeave={() => setDragEnabled(false)}
                                onClick={(e) => e.stopPropagation()}
                            >
                                <span>Reorder</span>
                            </div>
                        )}
                    </li>
                ))
            ) : (
                <p>No items in this routine.</p>
            )}
            </ul>
        </div>
    );
}