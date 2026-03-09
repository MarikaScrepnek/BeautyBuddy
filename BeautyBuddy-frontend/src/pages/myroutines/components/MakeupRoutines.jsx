import { useEffect, useState } from 'react';

import { updateRoutine } from '../../../api/routineApi';

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const [isEditingName, setIsEditingName] = useState(false);
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

    function handleDrop(dropIndex) {
        if (dragIndex === null || dragIndex === dropIndex) return;

        const newItems = [...editedRoutine.items];
        const draggedItem = newItems.splice(dragIndex, 1)[0];
        newItems.splice(dropIndex, 0, draggedItem);

        const reordered = newItems.map((item, i) => ({
            ...item,
            order: i
        }));

        setEditedRoutine(prev => ({
            ...prev,
            items: reordered
        }));

        setDragIndex(null);
        setDragEnabled(false);
    }

    function handleDeleteItem(itemId) {
        setEditedRoutine(prev => ({
            ...prev,
            items: prev.items.filter(item => item.id !== itemId)
        }));
    }

    async function handleSaveChanges() {
        const updatedRoutine = await updateRoutine(editedRoutine);
        setEditedRoutine(updatedRoutine);
        setIsEditingRoutine(false);
    }

    useEffect(() => {
        setEditedRoutine(routine);
        setIsEditingRoutine(false);
    }, [routine]);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name">
                    <p>{userName}'s</p>
                    <div className='routine-name-container' style={{ display: 'flex', alignItems: 'center' }}>
                        <div style={{ flex: 1, textAlign: 'center' }}>
                            {isEditingRoutine && (editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && isEditingName ? (
                                <input
                                    className="inline-edit-name-input"
                                    type="text"
                                    value={editedRoutine.name || ""}
                                    autoFocus
                                    style={{
                                        fontSize: '2rem',
                                        fontWeight: 'bold',
                                        textAlign: 'center',
                                        border: 'none',
                                        background: 'transparent',
                                        outline: 'none',
                                        width: '100%',
                                        textTransform: 'uppercase'
                                    }}
                                    onChange={e => handleNameChange(e.target.value)}
                                    onBlur={() => {
                                        setIsEditingName(false);
                                    }}
                                    onKeyDown={e => {
                                        if (e.key === 'Enter') {
                                            setIsEditingName(false);
                                        }
                                    }}
                                />
                            ) : (
                                <h1 style={{ fontSize: '2rem', fontWeight: 'bold', textAlign: 'center', textTransform: 'uppercase' }}>
                                    {editedRoutine.name || editedRoutine.occasion}
                                </h1>
                            )}
                        </div>
                        {(editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && isEditingRoutine ? (
                            <button className="edit-name-button" onClick={() => setIsEditingName(true)}>Edit Routine Name</button>
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
                    <>
                        <button className="edit-button" onClick={handleSaveChanges}>Save Changes</button>
                        <button className="cancel-button" onClick={() => {setEditedRoutine(routine); setIsEditingRoutine(false);}}>Undo Changes</button>
                    </>
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
                editedRoutine.items.map((item, index) => (
                    <li
                        className="routine-item"
                        key={item.id}
                        draggable={isEditingRoutine && dragEnabled}
                        onDragStart={() => handleDragStart(index)}
                        onDragOver={(e) => e.preventDefault()}
                        onDrop={() => handleDrop(index)}
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
                        <img draggable="false" src={item.productImageUrl} alt={item.productName} className="routine-item-img" />
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
                            {item.notes ? (
                                <>
                                    <p className="item-notes">Notes: {item.notes}</p>
                                    {isEditingRoutine && (
                                        <button className="add-item-notes-button" onClick={(e) => {e.stopPropagation(); /* Open modal to edit notes for this item */}}>
                                            Edit Notes
                                        </button>
                                    )}
                                </>
                            ) : (
                                isEditingRoutine && (
                                    <button className="add-item-notes-button" onClick={(e) => {e.stopPropagation(); /* Open modal to add notes for this item */}}>
                                        Add Notes
                                    </button>
                                )
                            )}
                    </li>
                ))
            ) : (
                <p style={{justifyContent: "center"}}>No items have been added to this routine.</p>
            )}
            </ul>
        </div>
    );
}