const API_BASE_URL = 'http://localhost:5000/api/auth';

export async function registerUser(username, email, password) {
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
        body: JSON.stringify({ email, password }),
    });
    return res.json();
}