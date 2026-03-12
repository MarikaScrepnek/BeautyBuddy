import React, { useState, useEffect } from 'react';
import { getCurrentUser } from '../../features/auth/api/authApi';

export default function Feed() {
    const [isLoggedIn, setIsLoggedIn] = useState(false);

    useEffect(() => {
        getCurrentUser()
            .then(() => setIsLoggedIn(true))
            .catch(() => setIsLoggedIn(false));
    }, []);
    return (
        <>
        {!isLoggedIn ? (

            <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh'}}>
                <h1 style={{fontSize: '2rem', color: '#555'}}>Login to view your feed...</h1>
            </div>

        ) : (

            <div style={{display: 'flex', justifyContent: 'center', alignItems: 'center', height: '80vh'}}>
                <h1 style={{fontSize: '2rem', color: '#555'}}>Feed coming soon...</h1>
            </div>

        )}
        </>
    );
}