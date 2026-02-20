export async function getProducts() {
    const res = await fetch("http://localhost:8080/api/products");
    return res.json();
  }

export async function reportProduct(productId, reason) {
  const response = await fetch(`http://localhost:8080/api/products/${productId}/report`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json"
    },
    credentials: "include",
    body: JSON.stringify({ reason })
  });
  return response.ok;
}

export async function getExchangeRate(targetCurrency) {
  if (targetCurrency === "CAD") return 1;
  const res = await fetch(`https://api.frankfurter.app/latest?from=CAD&to=${targetCurrency}`);
  const data = await res.json();
  if (!data.rates || !data.rates[targetCurrency]) return 1;
  return data.rates[targetCurrency];
}