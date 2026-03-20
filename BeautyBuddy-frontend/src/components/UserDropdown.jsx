import { useState, useRef, useEffect } from "react";
import { FaChevronDown } from "react-icons/fa";

import "./UserDropdown.css";

export default function UserDropdown({ user, onSignOut }) {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <div className="user-dropdown-container" ref={dropdownRef}>
      <button
        className="user-dropdown-button"
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="user-avatar-fallback">
          {user.username.charAt(0).toUpperCase()}
        </div>
        <span className="user-name">{user.username}</span>
        <FaChevronDown className={`user-chevron ${isOpen ? "open" : ""}`} />
      </button>

      <div className={`user-dropdown-menu ${isOpen ? "show" : ""}`}>
        <a href="/profile" className="dropdown-item">Profile</a>
        <a href="/friends" className="dropdown-item">Friends</a>
        <a href="/settings" className="dropdown-item">Settings</a>
        <button onClick={onSignOut} className="dropdown-item">Sign Out</button>
      </div>
    </div>
  );
}