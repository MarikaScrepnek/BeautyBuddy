import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'

function Product({title}) {
  return (
    <div>
      <h2>{title}</h2>
    </div>
  )
}

function App() {
  return (
    <>
      <div className = "product-container">
        <Product title="Foundation" rating={5} isCrueltyFree={true}/>
        <Product title="Concealer"/>
        <Product title="Blush"/>
      </div>
    </>
  )
}

export default App
