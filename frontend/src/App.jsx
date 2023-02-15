import { Routes, Route } from 'react-router-dom'
import './App.css'

import Home from './pages/Home/Home'
import Error_404 from './pages/Error/Error_404'
import Alert from './components/Alert/Alert'

function App() {
  return (
    <>
      <Routes>
        <Route exact path="/" element={<Home />} />
        <Route path="*" element={<Error_404 />} />
      </Routes>
      <Alert />
    </>
  )
}

export default App
