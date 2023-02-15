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
    mobile: "",
    amount: null,
  });

  return (
      <App.Provider value={{alert, setAlert, user, setUser}} >
          { children }
      </App.Provider>
  );
};

export default AppContext;

export const AppState = () => {
    return useContext(App);
}