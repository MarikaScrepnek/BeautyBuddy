export async function getMakeupRoutines() {
    const response = await fetch(`http://localhost:8080/api/routines/makeup`, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch makeup routines");
    }
    return await response.json();
}

export async function createMakeupRoutine(occasion, name, notes) {
    const response = await fetch(`http://localhost:8080/api/routines/makeup`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ occasion, name, notes }),
    });
    if (!response.ok) {
        throw new Error("Failed to create makeup routine");
    }
    return response.ok;
}

export async function addProductToRoutine(routineId, productId, shadeName) {
    const response = await fetch(`http://localhost:8080/api/routines/${routineId}/add-product`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ productId, shadeName }),
    });
    if (!response.ok) {
        throw new Error("Failed to add product to makeup routine");
    }
    return response.ok;
}