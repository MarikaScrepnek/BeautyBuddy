import { API_BASE_URL } from '../../../config/apiBase';

export async function submitReview(productId, shadeName, rating, title, text, imageLinks) {
    const response = await fetch(`${API_BASE_URL}/reviews/add`, {
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
    const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/edit`, {
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
    const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return response.ok;
}

export async function getAverageRating(productId) {
    const response = await fetch(`${API_BASE_URL}/reviews/${productId}/average-rating`);
    return response.json();
}

export async function getReviews(productId, page = 0, size = 10, options = {}) {
    const params = new URLSearchParams({
        page: String(page),
        size: String(size)
    });
    if (options.sort) params.set("sort", options.sort);
    if (options.filter) params.set("filter", options.filter);

    const response = await fetch(`${API_BASE_URL}/reviews/${productId}?${params.toString()}`, {
        credentials: 'include'
    });
    return response.json();
}

export async function upvoteReview(reviewId) {
    const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/upvote`, {
        method: 'POST',
        credentials: 'include'
    });
    return response.ok;
}

export async function removeUpvoteReview(reviewId) {
    const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/upvote`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return response.ok;
}

export async function reportReview(reviewId, reason) {
    const response = await fetch(`${API_BASE_URL}/reviews/${reviewId}/report`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify({ reason })
    });
    return response.ok;
}

export async function searchReviews(productId, query, page = 0, size = 10, options = {}) {
    const params = new URLSearchParams({
        query,
        page: String(page),
        size: String(size)
    });
    if (options.sort) params.set("sort", options.sort);
    if (options.filter) params.set("filter", options.filter);

    const response = await fetch(`${API_BASE_URL}/reviews/${productId}/search?${params.toString()}`, {
        credentials: 'include'
    });
    return response.json();
}