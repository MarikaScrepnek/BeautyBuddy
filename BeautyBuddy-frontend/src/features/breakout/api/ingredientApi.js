export default function getIngredients(page = 0, size = 10) {
    return fetch(`/api/ingredients?page=${page}&size=${size}`)
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