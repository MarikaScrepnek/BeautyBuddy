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

  // Grab first letter if avatar is missing
  const avatarContent = user.avatar ? (
    <img src={user.avatar} alt="avatar" className="user-avatar" />
  ) : (
    <div className="user-avatar-fallback">
      {user.name.charAt(0).toUpperCase()}
    </div>
  );

  return (
    <div className="user-dropdown-container" ref={dropdownRef}>
      <button
        className="user-dropdown-button"
        onClick={() => setIsOpen(!isOpen)}
      >
        {avatarContent}
        <span className="user-name">{user.name}</span>
        <FaChevronDown className={`user-chevron ${isOpen ? "open" : ""}`} />
      </button>

      <div className={`user-dropdown-menu ${isOpen ? "show" : ""}`}>
        <a href="/profile" className="dropdown-item">Profile</a>
        <a href="/settings" className="dropdown-item">Settings</a>
        <button onClick={onSignOut} className="dropdown-item">Sign Out</button>
      </div>
    </div>
  );
}