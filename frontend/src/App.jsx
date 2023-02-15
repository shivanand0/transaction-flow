import { Routes, Route } from 'react-router-dom'
import './App.css'

import Home from './pages/Home/Home'
import Error_404 from './pages/Error/Error_404'

function App() {
  return (
    <>
      <Routes>
        <Route exact path="/" element={<Home />} />
        <Route path="*" element={<Error_404 />} />
      </Routes>
    </>
  )
}

export default App
