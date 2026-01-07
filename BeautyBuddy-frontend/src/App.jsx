import { useState } from 'react'
import { useEffect } from 'react'

import './App.css'

function Products() {
  const [products, setProducts] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/products")
      .then(res => res.json())
      .then(data => setProducts(data));
  }, [])

  return (
    <ul>
      {products.map(p => (
        <li key={p.product_id}>{p.name}</li>
      ))}
    </ul>
  )
}

function Brands() {
  const [brands, setBrands] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/brands")
      .then(res => res.json())
      .then(data => setBrands(data));
  }, [])

  return (
    <ul>
      {brands.map(b => (
        <li key={b.brand_id}>{b.name}</li>
      ))}
    </ul>
  )
}

function Categories() {
  const [categories, setCategories] = useState([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/categories")
      .then(res => res.json())
      .then(data => setCategories(data));
  }, [])

  return (
    <ul>
      {categories.map(c => (
        <li key={c.category_id}>{c.name}</li>
      ))}
    </ul>
  )
}

function App() {
  return (
    <>
    <h2>Products</h2>
      <Products />

      <h2>Brands</h2>
      <Brands />

      <h2>Categories</h2>
      <Categories />
    </>
  )
}

export default App
