import { useEffect } from "react";

export default function Toast({ message, type = "info", duration = 2500, onClose }) {
    useEffect(() => {
        if (!message) return;
        const id = setTimeout(() => onClose?.(), duration);
        return () => clearTimeout(id);
    }, [message, duration, onClose]);

    if (!message) return null;

    return (
        <div className={`toast toast--${type}`}>
            {message}
        </div>
    );
}