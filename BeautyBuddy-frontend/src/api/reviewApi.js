export async function submitReview(productId, shadeName, rating, title, text, imageLinks) {
    const response = await fetch('/api/reviews', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            productId,
            shadeName,
            rating,
            title,
            text,
            imageLinks
        })
    });
    return response.json();
}

export async function deleteReview(reviewId) {
    const response = await fetch(`/api/reviews/${reviewId}`, {
        method: 'DELETE'
    });
    return response.ok;
}

export async function getAverageRating(productId) {
    const response = await fetch(`/api/reviews/${productId}/average-rating`);
    return response.json();
}

export async function getReviews(productId) {
    const response = await fetch(`/api/reviews/productId=${productId}`);
    return response.json();
}

export async function upvoteReview(reviewId) {
    const response = await fetch(`/api/reviews/${reviewId}/upvote`, {
        method: 'POST'
    });
    return response.ok;
}

export async function reportReview(reviewId, reason) {
    const response = await fetch(`/api/reviews/${reviewId}/report`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'        },
        body: JSON.stringify({reason})
    });
    return response.ok;
}