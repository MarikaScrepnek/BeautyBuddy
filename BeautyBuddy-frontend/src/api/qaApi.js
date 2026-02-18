export async function getQuestionsForProduct(productId) {
    const res = await fetch(`http://localhost:8080/api/questions/${productId}`);
    return res.json();
}