import React, { useEffect, useRef, useState } from 'react';

import { updateRoutine } from '../api/routineApi';

import { GiTrashCan } from "react-icons/gi";
import { IoReorderThreeOutline } from "react-icons/io5";
import { CiEdit } from "react-icons/ci";

import ReviewStars from '../../../components/ui/ReviewStars';

import './SelectedRoutine.css';

export default function MakeupRoutines( { userName, routine, routineType } ) {
    const notesRef = useRef(null);

    const[isEditingRoutine, setIsEditingRoutine] = useState(false);
    const[editedRoutine, setEditedRoutine] = useState(routine);

    const[isEditingName, setIsEditingName] = useState(false);

    const[dragEnabled, setDragEnabled] = useState(false);
    const[dragIndex, setDragIndex] = useState(null);

    function autoResizeTextarea(e) {
        e.target.style.height = "auto";
        e.target.style.height = e.target.scrollHeight + "px";
    }

    function getRoutineHeader() {
        if (routineType === "Makeup") {
            const occ = editedRoutine.occasion?.toLowerCase();
            if (occ === "glam" || occ === "casual") {
                return occ.charAt(0).toUpperCase() + occ.slice(1);
            } else if (occ === "event" || occ === "other") {
                return editedRoutine.name || "Routine";
            } else {
                return editedRoutine.name || "Routine";
            }
        } else if (routineType === "Skincare") {
            if (editedRoutine.timeOfDay?.toLowerCase() === "am") {
                return "AM";
            } else if (editedRoutine.timeOfDay?.toLowerCase() === "pm") {
                return "PM";
            } else {
                return "Skincare";
            }
        } else if (routineType === "Haircare") {
            return "Haircare";
        }
        return editedRoutine.name || "Routine";
    }

    function handleNameChange(newName) {
        if (newName.length > 128) {
            alert("Routine name cannot exceed 128 characters.");
            return;
        }
        setEditedRoutine(prev => ({ ...prev, name: newName }));
    }

    function handleNotesChange(newNotes) {
        if (newNotes.length > 400) {
            alert("Routine notes cannot exceed 400 characters.");
            return;
        }
        setEditedRoutine(prev => ({ ...prev, notes: newNotes }));
    }

    function handleItemNotesChange(itemId, newNotes) {
        if (newNotes.length > 126) {
            alert("Product notes cannot exceed 126 characters.");
            return;
        }
        setEditedRoutine(prev => ({
            ...prev,
            items: prev.items.map(item => item.id === itemId ? { ...item, productNotes: newNotes } : item)
        }));
    }

    function handleItemShadeChange(itemId, newShadeName) {
        setEditedRoutine(prev => ({
            ...prev,
            items: prev.items.map(item =>
                item.id === itemId
                    ? { ...item, productShadeName: newShadeName }
                    : item
            )
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
        if (routineType === "Makeup") {
            if ((editedRoutine.occasion !== 'CASUAL' && editedRoutine.occasion !== 'GLAM') && (!editedRoutine.name || editedRoutine.name.trim() === "")) {
                alert("Routine name cannot be empty.");
                return;
            }
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

    useEffect(() => {
        if (notesRef.current) {
            notesRef.current.style.height = "auto";
            notesRef.current.style.height = notesRef.current.scrollHeight + "px";
        }
    }, [isEditingRoutine, editedRoutine.notes]);

    return (
        <div className="routine-card">
            <div className="makeup-routine-header">
                <div className="routine-name">
                    <p>{userName}'s</p>
                    <div className='routine-name-container' style={{ display: 'flex', alignItems: 'center' }}>
                        {isEditingRoutine && routineType === "Makeup" && isEditingName ? (
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
                                <h1 style={{
                                    fontSize: '2rem',
                                    fontWeight: 'bold',
                                    color: '#6c63ff',
                                    textAlign: 'center',
                                    border: 'none',
                                    background: 'transparent',
                                    outline: 'none'
                                }}>
                                    {getRoutineHeader()}
                                </h1>
                                {isEditingRoutine && routineType === "Makeup" && (editedRoutine.occasion?.toLowerCase() === "event" || editedRoutine.occasion?.toLowerCase() === "other") && (
                                    <button className="edit-name-button" onClick={() => setIsEditingName(true)}>
                                        <CiEdit />
                                    </button>
                                )}
                            </>
                        )}
                    </div>
                    <p>{routineType === "Haircare" ? "" : routineType} Routine</p>
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
                        <button className="edit-button" onClick={handleSaveChanges}>Save Changes</button>
                        <button className="cancel-button" style={{width:"120px", marginTop: '18px'}} onClick={() => {setEditedRoutine(routine); setIsEditingRoutine(false);}}>Undo Changes</button>
                    </div>
                ) : (
                    <button className="edit-button" onClick={() => setIsEditingRoutine(true)}>Edit Routine</button>
                )}
            </div>

            {editedRoutine.notes && !isEditingRoutine ? (
                <div className="routine-notes">
                    <p>{editedRoutine.notes}</p>
                </div>
            ) : (
                isEditingRoutine && (
                    <div className="routine-notes">
                        <textarea
                            ref={notesRef}
                            className='routine-notes-input'
                            value={editedRoutine.notes || ''}
                            onChange={e => {handleNotesChange(e.target.value); autoResizeTextarea(e);}}
                            placeholder="Add routine notes..."
                            onInput={autoResizeTextarea}
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
                                window.open(`/products/${item.productId}`, '_blank')
                            }
                        }}
                    >
                        {isEditingRoutine && (
                            <div className="delete-item-button" onClick={(e) => {e.stopPropagation(); handleDeleteItem(item.id);}}>
                                <GiTrashCan />
                            </div>
                        )}
                        <img draggable="false" src={item.productImageUrl} alt={item.productName} className="routine-item-img" />
                        <div className="routine-item-main">
                            <div className='routine-item-info-row'>
                                <div className="routine-item-info">
                                    <p className="routine-item-name">{item.productName}</p>
                                    <div className='routine-item-meta'>
                                        <span>{item.productBrand}</span>
                                        {isEditingRoutine && item.allShades && item.allShades.length > 0 ? (
                                            <>
                                                <span>• </span>
                                                <select
                                                    className="routine-item-shade-select"
                                                    value={item.productShadeName || ""}
                                                    onChange={(e) => {
                                                        e.stopPropagation();
                                                        const rawValue = e.target.value;
                                                        handleItemShadeChange(item.id, rawValue || null);
                                                    }}
                                                    onClick={(e) => e.stopPropagation()}
                                                >
                                                    {item.allShades.map((shade) => {
                                                        const shadeName = typeof shade === "string" ? shade : shade.shadeName;
                                                        if (!shadeName) return null;
                                                        return (
                                                            <option key={shadeName} value={shadeName}>
                                                                {shadeName}
                                                            </option>
                                                        );
                                                    })}
                                                </select>
                                            </>
                                        ) : (
                                            item.productShadeName && (
                                                <span>• {item.productShadeName}</span>
                                            )
                                        )}
                                        <span>• </span>
                                        <ReviewStars
                                            productId={item.productId}
                                            shadeName={item.productShadeName}
                                            rating={item.rating}
                                            reviewId={item.reviewId ? item.reviewId : null}
                                            onReviewSubmitted={(newRating) => {
                                                setEditedRoutine(prev => ({
                                                    ...prev,
                                                    items: prev.items.map(it =>
                                                        it.id === item.id ? { ...it, rating: newRating } : it
                                                    )
                                                }));
                                            }}
                                        />
                                    </div>
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
                            </div>
                                    {isEditingRoutine ? (
                                        <div className="routine-item-notes">
                                            <textarea
                                                value={editedRoutine.items[index]?.productNotes || ''}
                                                onChange={e => handleItemNotesChange(item.id, e.target.value)}
                                                className="routine-item-notes-input"
                                                placeholder="Add product notes..."
                                                onClick={(e) => e.stopPropagation()}
                                            />
                                        </div>
                                    ) : (
                                        (item.productNotes && (
                                            <div className="routine-item-notes">
                                                <p>{item.productNotes}</p>
                                            </div>
                                        ))
                                    )}
                        </div>
                    </li>
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