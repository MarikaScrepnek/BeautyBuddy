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

export async function getSkincareRoutines() {
    const response = await fetch(`http://localhost:8080/api/routines/skincare`, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch skincare routines");
    }
    return await response.json();
}

export async function getHaircareRoutine() {
    const response = await fetch(`http://localhost:8080/api/routines/haircare`, {
        method: "GET",
        credentials: "include",
    });
    if (!response.ok) {
        throw new Error("Failed to fetch haircare routine");
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

export async function updateRoutine(updatedData) {
    const response = await fetch(`http://localhost:8080/api/routines/${updatedData.routineId}`, {
        method: "PUT",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(updatedData),
    });
    if (!response.ok) {
        throw new Error("Failed to update makeup routine");
    }
    return response.json();
}