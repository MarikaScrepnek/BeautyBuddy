import { useParams } from "react-router-dom";
import { useEffect, useState } from "react";

import './ProductDetails.css';

export default function ProductDetails() {
  const { productId } = useParams();
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`http://localhost:8080/api/products/${productId}`)
      .then(res => res.json())
      .then(setData)
      .catch(console.error)
      .finally(() => setLoading(false));
  }, [productId]);

    if (loading) return <p>Loading product details...</p>;

  return (
    <div>
        <h1 className="product-name">{data.name}</h1>

        {data.image_link && (
            <img src={data.image_link} alt={data.name} className="product-image" />
        )}

        <section>
            <h2>Ingredients</h2>
            {/* ingredients list */}
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
