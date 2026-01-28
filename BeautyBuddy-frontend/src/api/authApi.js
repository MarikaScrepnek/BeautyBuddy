const API_BASE_URL = 'http://localhost:8080/api/auth';

export async function registerUser(email, username, password) {
    const res = await fetch(`${API_BASE_URL}/register`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
    });
    return res.json();
}

export async function loginUser(email, password) {
    const res = await fetch(`${API_BASE_URL}/login`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        credentials: 'include',
        body: JSON.stringify({ email, password }),
    });
    const data = await res.json();
    return { status: res.status, data };
}

export async function getCurrentUser() {
    const res = await fetch(`${API_BASE_URL}/me`, {
        method: 'GET',
        credentials: 'include',
    });
    if (!res.ok) {
        throw new Error('Failed to fetch current user');
    }
    return res.json();
}

export async function logoutUser() {
    const res = await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST',
        credentials: 'include',
    });
    return res.ok;
}