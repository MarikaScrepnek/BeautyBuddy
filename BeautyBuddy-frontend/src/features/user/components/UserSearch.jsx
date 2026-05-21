import { searchUsers } from "../api/userApi";
import { useState } from "react";
import Searchbar from "../../../components/ui/Searchbar";

import "./UserSearch.css";

export default function UserSearch() {

    const [users, setUsers] = useState([]);

    async function handleSearch(query) {
        // Implement search logic here, e.g., make an API call to fetch users based on the query
        searchUsers(query)
            .then(users => {
                // Handle the search results, e.g., update state to display the users
                setUsers(users);
            })
            .catch(error => {
                // Handle any errors that occur during the search
                console.error('Error searching users:', error);
            });
    }

    return (

            <>
                    <Searchbar placeholder="Search for users..." onSearch={handleSearch} />
                {users.length > 0 ? (
                    <div className="user-list">
                        {users.map(user => (
                            <div key={user.username} className="user-card">
                                <h3>{user.username}</h3>
                                {user.profilePictureUrl && 
                                    <img src={user.profilePictureUrl}/>
                                } :
                                {
                                    <div className="user-avatar-fallback">
                                        {user.username.charAt(0).toUpperCase()}
                                    </div>
                                }
                            </div>
                        ))}
                    </div>
                ) : (
                    <p>No users found.</p>
                )}
            </>

    );
}