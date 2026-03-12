export async function getQuestionsForProduct(productId, page = 0, size = 10) {
    const res = await fetch(`http://localhost:8080/api/questions/${productId}?page=${page}&size=${size}`, {
        credentials: 'include'
    });
    return res.json();
}

export async function submitQuestion(productId, text) {
    const res = await fetch(`http://localhost:8080/api/questions/ask`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ productId, text })
    });
    return res.ok;
}

export async function submitAnswer(questionId, text) {
    const res = await fetch(`http://localhost:8080/api/answers/submit`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ questionId, text })
    });
    return res.ok;
}

export async function editQuestion(questionId, text) {
    const res = await fetch(`http://localhost:8080/api/questions/${questionId}/edit`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ questionId, text })
    });
    return res.ok;
}

export async function editAnswer(answerId, text) {
    const res = await fetch(`http://localhost:8080/api/answers/${answerId}/edit`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ answerId, text })
    });
    return res.ok;
}

export async function removeQuestion(questionId) {
    const res = await fetch(`http://localhost:8080/api/questions/${questionId}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return res.ok;
}

export async function removeAnswer(answerId) {
    const res = await fetch(`http://localhost:8080/api/answers/${answerId}`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return res.ok;
}

export async function upvoteQuestion(questionId) {
    const res = await fetch(`http://localhost:8080/api/questions/${questionId}/upvote`, {
        method: 'POST',
        credentials: 'include'
    });
    return res.ok;
}

export async function upvoteAnswer(answerId) {
    const res = await fetch(`http://localhost:8080/api/answers/${answerId}/upvote`, {
        method: 'POST',
        credentials: 'include'
    });
    return res.ok;
}

export async function removeUpvoteQuestion(questionId) {
    const res = await fetch(`http://localhost:8080/api/questions/${questionId}/upvote`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return res.ok;
}

export async function removeUpvoteAnswer(answerId) {
    const res = await fetch(`http://localhost:8080/api/answers/${answerId}/upvote`, {
        method: 'DELETE',
        credentials: 'include'
    });
    return res.ok;
}

export async function reportQuestion(questionId, reason) {
    const res = await fetch(`http://localhost:8080/api/questions/${questionId}/report`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ targetId: questionId, reason, targetType: 'question' })
    });
    return res.ok;
}

export async function reportAnswer(answerId, reason) {
    const res = await fetch(`http://localhost:8080/api/answers/${answerId}/report`, {
        method: 'POST',
        credentials: 'include',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ targetId: answerId, reason, targetType: 'answer' })
    });
    return res.ok;
}

export async function searchQuestions(productId, query, page = 0, size = 10) {
    const response = await fetch(`http://localhost:8080/api/questions/${productId}/search?query=${encodeURIComponent(query)}&page=${page}&size=${size}`, {
        credentials: 'include'
    });
    return response.json();
}