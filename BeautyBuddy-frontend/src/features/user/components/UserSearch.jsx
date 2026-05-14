export default function UserSearch() {

    async function handleSearch(query) {
        // Implement search logic here, e.g., make an API call to fetch users based on the query
        console.log('Searching for:', query);
    }

    return (

            <div className="feed-container">
                    <Searchbar placeholder="Search for users..." />
                Follow users to view their activity here!
            </div>

    );
}