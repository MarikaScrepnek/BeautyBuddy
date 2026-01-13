import { useEffect } from "react";
import "./LoginModal.css";

import { NavLink } from "react-router-dom";

export default function LoginModal({ onClose }) {
  useEffect(() => {
    document.body.style.overflow = "hidden";

    const handleEsc = (e) => {
      if (e.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleEsc);

    return () => {
      document.body.style.overflow = "";
      window.removeEventListener("keydown", handleEsc);
    };
  }, [onClose]);

  return (
    <div className="modal-overlay">
        <div className="modal" onClick={(e) => e.stopPropagation()}>
        <h2>Login</h2>

        <input type="email" placeholder="Email" />
        <input type="password" placeholder="Password" />

        <button className="modal-login-button">Log In</button>

        <NavLink className='modal-forgot-password-link'>Forgot Password?</NavLink>

        <div className='modal-signup'>
            <h2 className='modal-signup-prompt'>Don't have an account?</h2>
            <NavLink className="modal-signup-link">Sign Up</NavLink>
        </div>

        <button
            className="modal-close"
            onClick={() => onClose()}
        >
            ✕
        </button>
        </div>
    </div>
  );
}