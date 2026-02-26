import React from "react";
import { FaSearch } from "react-icons/fa";
import "./Searchbar.css";

export default function Searchbar({ placeholder = "Search..." , value, onChange, onSearch, onKeyDown }) {
    return (
        <div className="searchbar-center">
            <div className="searchbar-container">
                <div className="searchbar">
                    <input
                        type="text"
                        className="searchbar-input"
                        placeholder={placeholder}
                        value={value}
                        onChange={onChange}
                        onKeyDown={onKeyDown}
                    />
                    <button
                        type="button"
                        className="searchbar-button"
                        aria-label="Search"
                        onClick={onSearch}
                    >
                        <FaSearch />
                    </button>
                </div>
            </div>
        </div>
    );
}
    