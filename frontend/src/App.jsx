import { Routes, Route } from 'react-router-dom'
import './App.css'

import Home from './pages/Home/Home'
import LenderSelection from './pages/Lender/Lender/LenderSelection'
import Error_404 from './pages/Error/Error_404'
import Alert from './components/Alert/Alert'
import TwoFAuthentication from './pages/two_factor_authentication/TwoFAuthentication'

function App() {
  return (
    <>
      <Routes>
        <Route exact path="/" element={<Home />} />
        <Route exact path="/transaction/lender-selection/:trackId" element={<LenderSelection />} />
        <Route exact path="/transaction/confirm" element={<TwoFAuthentication/>} />
        <Route path="*" element={<Error_404 />} />
      </Routes>
      <Alert />
    </>
  )
}

export default App
