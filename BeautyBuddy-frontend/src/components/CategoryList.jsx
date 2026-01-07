import { useEffect, useState } from "react";
import { getCategories } from "../api/categoryApi";

export default function CategoryList() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    getCategories().then(setCategories);
  }, []);

  return (
    <div>
      {categories.map(c => (
        <div key={c.category_id}>{c.name}</div>
      ))}
    </div>
  );
}