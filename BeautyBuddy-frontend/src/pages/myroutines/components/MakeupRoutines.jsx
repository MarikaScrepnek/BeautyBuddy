import React, { useEffect, useState } from 'react';

import { updateRoutine } from '../../../api/routineApi';

import { GiTrashCan } from "react-icons/gi";
import { IoReorderThreeOutline } from "react-icons/io5";
import { CiEdit } from "react-icons/ci";

import './MakeupRoutines.css';

export default function MakeupRoutines( { userName, routine } ) {
    const[isEditingRoutine, setIsEditingRoutine] = useState(false);
    const[editedRoutine, setEditedRoutine] = useState(routine);

    const[isEditingName, setIsEditingName] = useState(false);

    const[editingItemNotes, setEditingItemNotes] = useState({});

    const[dragEnabled, setDragEnabled] = useState(false);
    const[dragIndex, setDragIndex] = useState(null);

    function handleNameChange(newName) {
        setEditedRoutine(prev => ({ ...prev, name: newName }));
    }

    function handleNotesChange(newNotes) {
        setEditedRoutine(prev => ({ ...prev, notes: newNotes }));
    }

    function handleItemNotesChange(itemId, newNotes) {
        setEditedRoutine(prev => ({
            ...prev,
            items: prev.items.map(item => item.id === itemId ? { ...item, productNotes: newNotes } : item)
        }));
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
        if ((editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && (!editedRoutine.name || editedRoutine.name.trim() === "")) {
            alert("Routine name cannot be empty.");
            return;
        }
        if (editedRoutine === routine) {
            setIsEditingRoutine(false);
            return;
        }
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
                        {isEditingRoutine && (editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && isEditingName ? (
                            <input
                                className="inline-edit-name-input"
                                type="text"
                                value={editedRoutine.name || ""}
                                autoFocus
                                style={{
                                    fontSize: '2rem',
                                    fontWeight: 'bold',
                                    color: '#6c63ff',
                                    textAlign: 'center',
                                    border: 'none',
                                    background: 'transparent',
                                    outline: 'none',
                                    minWidth: '120px',
                                    maxWidth: '300px'
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
                            <>
                                {(editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && isEditingRoutine ? (
                                    <>
                                    <div style={{width:"8px"}}></div>
                                    <h1 style={{
                                        fontSize: '2rem',
                                        fontWeight: 'bold',
                                        color: '#6c63ff',
                                        textAlign: 'center',
                                        border: 'none',
                                        background: 'transparent',
                                        outline: 'none'

                                    }}>
                                        {editedRoutine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}
                                    </h1>
                                    <button className="edit-name-button" onClick={() => setIsEditingName(true)}>
                                        <CiEdit />
                                    </button>
                                    </>
                                ) : 
                                    <h1 style={{
                                        fontSize: '2rem',
                                        fontWeight: 'bold',
                                        color: '#6c63ff',
                                        textAlign: 'center',
                                        border: 'none',
                                        background: 'transparent',
                                        outline: 'none'
                                    }}>
                                        {editedRoutine.name || (routine.occasion.charAt(0).toUpperCase() + routine.occasion.slice(1).toLowerCase())}
                                    </h1>
                                }
                            </>
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
                    <div style={{display: "flex"}}>
                        <div style={{width:"120px"}}></div>
                        <button className="edit-button" style={{ margin: '0 auto', marginTop: '8px' }} onClick={handleSaveChanges}>Save Changes</button>
                        <button className="cancel-button" style={{width:"120px", marginTop: '18px'}} onClick={() => {setEditedRoutine(routine); setIsEditingRoutine(false);}}>Undo Changes</button>
                    </div>
                ) : (
                    <button className="edit-button" onClick={() => setIsEditingRoutine(true)}>Edit Routine</button>
                )}
            </div>

            {editedRoutine.notes && !isEditingRoutine ? (
                <div style={{marginTop: '20px'}} className="routine-notes">
                    <h3>Notes:</h3>
                    <p>{editedRoutine.notes}</p>
                </div>
            ) : (
                isEditingRoutine && (
                    <div style={{ marginTop: '35px' }}>
                        <textarea
                            value={editedRoutine.notes || ''}
                            onChange={e => handleNotesChange(e.target.value)}
                            style={{
                                width: '400px',
                                minHeight: '80px',
                                fontSize: '16px',
                                padding: '8px',
                                borderRadius: '8px',
                                border: '1px solid #ccc',
                                resize: 'none'
                            }}
                            placeholder="Add routine notes..."
                        />
                    </div>
                )
            )}

            <ul className="routine-items-list">
            {editedRoutine.items && editedRoutine.items.length > 0 ? (
                editedRoutine.items.map((item, index) => (
                    <React.Fragment key={item.id}>
                    <li
                        className="routine-item"
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
                                <GiTrashCan />
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
                                <IoReorderThreeOutline />
                            </div>
                        )}
                    </li>
                    {(item.productNotes && !isEditingRoutine) && (
                        <div style={{ border: '1px solid #ccc', borderRadius: '8px', padding: '10px' }}>
                            <p>Product Notes: {item.productNotes}</p>
                        </div>
                    )}
                    <div>
                        {isEditingRoutine && (
                            <textarea
                                value={editedRoutine.items[index]?.productNotes || ''}
                                onChange={e => handleItemNotesChange(item.id, e.target.value)}
                                style={{
                                    width: '608px',
                                    minHeight: '40px',
                                    fontSize: '16px',
                                    padding: '8px',
                                    borderRadius: '8px',
                                    border: '1px solid #ccc',
                                    resize: 'none'
                                }}
                                placeholder="Add product notes..."
                            />
                        )}
                    </div>
                    </React.Fragment>
                    
                ))
            ) : (
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center"}}>
                    <p style={{ fontStyle: "italic", color: "#888" }}>No products have been added to this routine yet.</p>
                </div>
            )}
            </ul>
        </div>
    );
}