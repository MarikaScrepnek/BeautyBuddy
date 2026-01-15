import { useEffect } from "react";
import "./SignupModal.css";

export default function SignupModal({ onClose }) {
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
        <input type="text" placeholder="Username" />
        <input type="password" placeholder="Password" />

        <button className="modal-signup-button">Sign Up</button>

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