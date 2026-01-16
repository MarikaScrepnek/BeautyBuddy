import { useEffect } from "react";
import "./ResetPassModal.css";

export default function ResetPassModal({ onClose }) {
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
        <h2>Reset Password</h2>

        <h3 className="modal-reset-instructions">Enter your email below and if there is a corresponding account, we will send you a message with your username and a link to reset your password.</h3>

        <input type="email" placeholder="Email" />

        <button className="modal-resetpass-button">Reset Password</button>

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