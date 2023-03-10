import React from 'react';
import { createContext, useContext, useState, useEffect } from 'react';

const App = createContext()
const AppContext = ({ children }) => {

  const [alert, setAlert] = useState({
    open: false,
    message: "",
    type: "",
  });

  const [user, setUser] = useState({
    name: "",
    uid: "",
    mobile: "",
    amount: null,
    detailsId: ""
  });

  const [lenderDetails, setLenderdetails] = useState(null)

  const [loading, setLoading] = useState(false)

  const [transactionStatus, setTransactionStatus] = useState("")

  const [trackStageValues, setTrackStageValues] = useState({
      selection: "LENDER_SELECTION",
      selectedLenderId: null,
      selectedLenderInfoId: null
  })
  
  return (
      <App.Provider value={{alert, setAlert, user, setUser, lenderDetails, setLenderdetails, loading, setLoading, trackStageValues, setTrackStageValues, transactionStatus, setTransactionStatus}} >
          { children }
      </App.Provider>
  );
};

export default AppContext;

export const AppState = () => {
    return useContext(App);
}