import {Routes, Route} from 'react-router-dom'
import './App.css'

import Home from './pages/Home/Home'
import LenderSelection from './pages/Lender/Lender/LenderSelection'
import Error_404 from './pages/Error/Error_404'
import Alert from './components/Alert/Alert'
import TenureSelection from "./pages/Tenure/TenureSelection.jsx";

function App() {
    return (
        <>
            <Routes>
                <Route exact path="/" element={<Home/>}/>
                <Route exact path="/transaction/lender-selection/:detailsId" element={<LenderSelection/>}/>
                <Route exact path="/transaction/tenure-selection/:detailsId" element={<TenureSelection/>}/>
                <Route path="*" element={<Error_404/>}/>

            </Routes>
            <Alert/>
        </>
    )
}

export default App
