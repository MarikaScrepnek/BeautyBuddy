export default function getIngredients(page = 0, size = 10, searchQuery = "") {
    let url = `http://localhost:8080/api/ingredients?page=${page}&size=${size}`;
    if (searchQuery) {
        url += `&search=${encodeURIComponent(searchQuery)}`;
    }
    return fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error("Failed to fetch ingredients");
            }
            return response.json();
        })
        .catch(error => {
            console.error("Error fetching ingredients:", error);
            throw error;
        });
}