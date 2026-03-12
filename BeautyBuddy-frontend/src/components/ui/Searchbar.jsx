import React, { useState } from "react";
import { FaSearch } from "react-icons/fa";
import "./Searchbar.css";

export default function Searchbar({ placeholder = "Search...", onSearch }) {
    const [inputValue, setInputValue] = useState("");

    const handleInputChange = (e) => {
        setInputValue(e.target.value);
    };

    const handleSearchClick = () => {
        if (onSearch) onSearch(inputValue);
    };

    const handleKeyDown = (e) => {
        if (e.key === "Enter" && onSearch) {
            onSearch(inputValue);
        }
    };

    return (
        <div className="searchbar-center">
            <div className="searchbar-container">
                <div className="searchbar">
                    <input
                        type="text"
                        className="searchbar-input"
                        placeholder={placeholder}
                        value={inputValue}
                        onChange={handleInputChange}
                        onKeyDown={handleKeyDown}
                    />
                    <button
                        type="button"
                        className="searchbar-button"
                        aria-label="Search"
                        onClick={handleSearchClick}
                    >
                        <FaSearch />
                    </button>
                </div>
            </div>
        </div>
    );
}
    