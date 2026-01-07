import { useEffect, useState } from "react";
import { getProducts } from "../api/productApi";

export default function ProductList() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    getProducts().then(setProducts);
  }, []);

  return (
    <div>
      {products.map(p => (
        <div key={p.product_id} style={{ marginBottom: "20px" }}>
          <h3>{p.name}</h3>
          
          {/* Display image if it exists */}
          {p.image_link && (
            <img 
              src={p.image_link} 
              alt={p.name} 
              style={{ width: "150px", height: "150px", objectFit: "contain" }}
            />
          )}

          <p>Price: ${p.price ?? "N/A"}</p>
        </div>
      ))}
    </div>
  );
}