import { useEffect } from "react";
import "./LoginModal.css";

export default function LoginModal({ onClose, onSwitchToSignup, onSwitchToResetPass }) {
  useEffect(() => {
    const handleEsc = (e) => {
      if (e.key === "Escape") onClose();
    };

    window.addEventListener("keydown", handleEsc);

    return () => {
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

        <button className='modal-forgot-password-link' onClick={onSwitchToResetPass}>Forgot Password?</button>

        <div className='modal-signup'>
            <h2 className='modal-signup-prompt'>Don't have an account?</h2>
            <button className="modal-signup-link" onClick={onSwitchToSignup}>Sign Up</button>
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