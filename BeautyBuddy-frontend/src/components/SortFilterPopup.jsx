import React, { useEffect, useRef, useState } from "react";

import "./SortFilterPopup.css";

export default function SortFilterPopup({ isOpen, onClose, type, page, onSelect }) {
    const [options, setOptions] = useState([]);
    const popupRef = useRef(null);

    useEffect(() => {
        if (type === "sort" && page === "wishlist") {
            setOptions([
                "Date Added: Newest First",
                "Date Added: Oldest First",
                "Price: Low to High",
                "Price: High to Low",
                "Rating: High to Low",
                "Rating: Low to High"
            ]);
        } else if (type === "filter" && page === "wishlist") {
            setOptions([
                "Skincare",
                "Makeup",
                "Haircare"
            ]);
        } else {
            setOptions([]);
        }
    }, [type, page]);

    useEffect(() => {
        if (!isOpen) return;

        const handleClickOutside = (event) => {
            if (!popupRef.current) return;
            if (!popupRef.current.contains(event.target)) {
                onClose?.();
            }
        };

        document.addEventListener("mousedown", handleClickOutside);
        return () => document.removeEventListener("mousedown", handleClickOutside);
    }, [isOpen, onClose]);

    if (!isOpen) return null;

    return (
        <div className="sort-filter-popup" ref={popupRef}>
            <ul className="sort-filter-list">
                {options.map((option) => (
                    <li key={option} className="sort-filter-item">
                        <button
                            type="button"
                            className="sort-filter-button"
                            style={{color:"white"}}
                            onClick={() => {
                                onSelect?.(type, option);
                                onClose?.();
                            }}
                        >
                            {option}
                        </button>
                    </li>
                ))}
            </ul>
        </div>
    );
}