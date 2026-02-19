export async function submitReview(productId, shadeName, rating, title, text, imageLinks) {
    const response = await fetch('http://localhost:8080/api/reviews/add', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
            productId,
            shadeName,
            rating,
            title,
            text,
            imageLinks
        })
    });
    return response.ok;
}

export async function editReview(reviewId, shadeName, rating, title, text, imageLinks) {
    const response = await fetch(`http://localhost:8080/api/reviews/${reviewId}/edit`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({
            shadeName,
            rating,
            title,
            text,
            imageLinks
        })
    });
    return response.ok;
}

export async function deleteReview(reviewId) {
    const response = await fetch(`http://localhost:8080/api/reviews/${reviewId}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return response.ok;
}

export async function getAverageRating(productId) {
    const response = await fetch(`http://localhost:8080/api/reviews/${productId}/average-rating`);
    return response.json();
}

export async function getReviews(productId, page = 0, size = 10) {
    const response = await fetch(`http://localhost:8080/api/reviews/${productId}?page=${page}&size=${size}`, {
        credentials: 'include'
    });
    return response.json();
}

export async function upvoteReview(reviewId) {
    const response = await fetch(`http://localhost:8080/api/reviews/${reviewId}/upvote`, {
        method: 'POST',
        credentials: 'include'
    });
    return response.ok;
}

export async function removeUpvoteReview(reviewId) {
    const response = await fetch(`http://localhost:8080/api/reviews/${reviewId}/upvote`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return response.ok;
}

export async function reportReview(reviewId, reason) {
    const response = await fetch(`http://localhost:8080/api/reviews/${reviewId}/report`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ reason })
    });
    return response.ok;
}