import { useEffect, useState } from "react";
import { getBrands } from "../api/brandApi";

export default function BrandList() {
  const [brands, setBrands] = useState([]);

  useEffect(() => {
    getBrands().then(setBrands);
  }, []);

  return (
    <div>
      {brands.map(b => (
        <div key={b.brand_id}>{b.name}</div>
      ))}
    </div>
  );
}