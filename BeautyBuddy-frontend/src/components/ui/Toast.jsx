import { useEffect, useState } from "react";
import "./Toast.css";

export default function Toast({ message, type = "info", duration = 800, onClose }) {
  const [closing, setClosing] = useState(false);

  useEffect(() => {
    if (!message) return;

    setClosing(false);

    const exitTimer = setTimeout(() => {
      setClosing(true);
    }, duration);

    const removeTimer = setTimeout(() => {
      onClose?.();
    }, duration + 300); // must match CSS exit duration

    return () => {
      clearTimeout(exitTimer);
      clearTimeout(removeTimer);
    };
  }, [message, duration, onClose]);

  if (!message) return null;

  return (
    <div className={`toast toast-${type} ${closing ? "toast-exit" : ""}`}>
      {message}
    </div>
  );
}
