import React from 'react'
import { Snackbar } from '@mui/material'
import MuiAlert from '@mui/material/Alert'
import { AppState } from '../../context/AppContext'

let vertical = 'bottom'
let horizontal = 'center'
const Alert = () => {

  const { alert, setAlert } = AppState()
  const handleCloseAlert = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }

    setAlert({ open: false });
  };

  return (
    <>
      <Snackbar
        anchorOrigin={{ vertical, horizontal }}
        open={alert.open}
        autoHideDuration={3000}
        onClose={handleCloseAlert}
      >
        <MuiAlert
          onClose={handleCloseAlert}
          elevation={10}
          variant="filled"
          severity={alert.type}
        >
          {alert.message}
        </MuiAlert>
      </Snackbar>
    </>
  )
}

export default Alert