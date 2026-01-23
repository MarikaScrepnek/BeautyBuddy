import { useEffect } from "react";
import "./SignupModal.css";

export default function SignupModal({ onClose, onSwitchToLogin}) {
  const[email, setEmail] = useState("");
  const[username, setUsername] = useState("");
  const[password, setPassword] = useState("");
  const[error, setError] = useState("");
  const [loading, setLoading] = useState(false);

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
        <h2>Sign Up</h2>

        <input type="email" placeholder="Email" />
        <input type="text" placeholder="Username" />
        <input type="password" placeholder="Password" />

        <button className="modal-signup-button">Sign Up</button>

        <div className="modal-login">
            <span className="modal-login-prompt">Already have an account?</span>
            <button
                className="modal-login-link"
                onClick={() => {
                    onSwitchToLogin();
                }}
            >
                Log In
            </button>
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