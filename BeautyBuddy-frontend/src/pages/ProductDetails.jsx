import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

export default function ProductDetails() {
  const { productId } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetch(`/api/products/${productId}`)
      .then(res => {
        if (!res.ok) throw new Error("Not found");
        return res.json();
      })
      .then(setData)
      .catch(err => setError(err.message))
      .finally(() => setLoading(false));
  }, [productId]);

  if (loading) return <p>Loading…</p>;
  if (error) return <p>{error}</p>;

  return (
    <div>
      <h1>{data.name}</h1>

      <section>
        <h2>Ingredients</h2>
        {/* list */}
      </section>

      <section>
        <h2>Reviews</h2>
        {/* reviews */}
      </section>

      <section>
        <h2>Questions</h2>
        {/* Q&A */}
      </section>
    </div>
  );
}
