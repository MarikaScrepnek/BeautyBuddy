import { API_BASE_URL } from '../../../config/apiBase';

const AUTH_BASE = `${API_BASE_URL}/auth`;

export async function registerUser(email, username, password) {
    const res = await fetch(`${AUTH_BASE}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
    });
    return res.json();
}

export async function loginUser(email, password) {
    const res = await fetch(`${AUTH_BASE}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ email, password }),
    });
    const data = await res.json().catch(() => ({}));

    if (!res.ok) return { error: data?.error ?? "Invalid credentials" };
    return { data };
}

export async function getCurrentUser() {
    const res = await fetch(`${AUTH_BASE}/me`, {
        method: 'GET',
        credentials: 'include',
    });
    const data = await res.json();
    if (!res.ok || data === null) {
        throw new Error('Failed to fetch current user');
    }
    return data;
}

export async function logoutUser() {
    const res = await fetch(`${AUTH_BASE}/logout`, {
        method: 'POST',
        credentials: 'include',
    });
    return res.ok;
}