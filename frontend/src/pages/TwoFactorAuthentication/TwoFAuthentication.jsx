import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'
import { CustomBox } from "../../components/Lender/Styles";
import { LinearProgress, TextField } from "@mui/material";
import { Button, Card, CardContent } from '@mui/material';
import { AppState } from '../../context/AppContext';
import { InitiateTransaction, ConfirmTransaction } from '../../config/API/Api';
import ConfirmationNavbar from '../../components/Navbar/ConfirmationNavbar';
import { useNavigate, redirect } from 'react-router-dom';
import { CustomBox2 } from "../../components/Lender/Styles";
const TwoFAuthentication = () => {
  const navigate = useNavigate();
  const { detailsId } = useParams();
  const { lenderDetails, loading, setLoading, setAlert, user, setUser, trackStageValues, setTrackStageValues, transactionStatus, setTransactionStatus } = AppState();

  useEffect(() => {
    if (lenderDetails === null) {
      return navigate(`/transaction/lender-selection/${detailsId}`)
    }
    if(transactionStatus === "FAIL" || transactionStatus === "SUCCESS"){
      return navigate(`/`)
    }
  }, [])



  const [showCard1, setShowCard1] = useState(true);
  const [showCard2, setShowCard2] = useState(false);
  const [panNumber, setPanNumber] = useState(null);
  const [otpNumber, setOtpNumber] = useState(null);
  const [error, setError] = useState(false);
  const [otpError, setOtpError] = useState(false);
  const [isValidInput, setValidInput] = useState(false);
  const [formSubmitted, setFormSubmitted] = useState(false);
  const [otpFormSubmitted, setOtpFormSubmitted] = useState(false);
  const [isValidOtpInput, setValidOtpInput] = useState(false);
  const handleShowCard2 = () => {
    setShowCard2(true);
    setShowCard1(false);
  }
  const handlePanNumberChange = (event) => {
    const newValue = event.target.value;
    if (newValue.length === 4) {
      setPanNumber(newValue);
      setError(false);
      setValidInput(true);
    }
    else {
      setValidInput(false);
      setPanNumber(newValue);
      setError(true);
    }
  }
  const handleOtpNumberChange = (event) => {
    const newValue = event.target.value;
    if (newValue.length === 4) {
      setOtpNumber(newValue);
      setOtpError(false);
      setValidOtpInput(true);
    }
    else {
      setOtpNumber(newValue);
      setOtpError(true);
      setValidOtpInput(false);
    }
  }

  const handlePanSubmit = async (e) => {
    e.preventDefault();
    if (!panNumber) {
      setAlert({
        open: true,
        message: "Please enter last four digits of PAN card",
        type: "error",
      });
      return;
    }

    try {
      setLoading(true)
      const result = await InitiateTransaction("INITIATE", detailsId, "initiate", panNumber);
      setLoading(false)

      if (result.data.status === true) {
        handleShowCard2();
        setValidInput(false);
        setFormSubmitted(true);
      } else {
        let errMsg1 = "";
        if (result.data.message === "PAN-OTP-EXCEED") {
          errMsg1 = "Transaction Failed... OTP limit exceed"
          setTransactionStatus("FAIL")

          // redirect to txnStatus page
          navigate(`/transaction/payment/failure`)
        } else {
          errMsg1 = result.data.message
        }

        setAlert({
          open: true,
          message: errMsg1,
          type: "error",
        });
      }
    } catch (error) {
      setAlert({
        open: true,
        message: error.message,
        type: "error",
      });
      return;
    }
  }


  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    if (!otpNumber) {
      setAlert({
        open: true,
        message: "Please enter the otp",
        type: "error",
      });
      return;
    }

    try {
      setLoading(true)
      const result = await ConfirmTransaction("CONFIRM", detailsId, "confirm", otpNumber);
      setLoading(false)

      if (result.data.status === true) {
        setOtpFormSubmitted(true);
        setTransactionStatus("SUCCESS")
        return navigate(`/transaction/payment/success`)
      } else {
        let errMsg2 = "";
        if (result.data.message === "MOB-OTP-EXCEED") {
          errMsg2 = "Transaction Failed... OTP limit exceed"
          // redirect to txsStatus page
          setTransactionStatus("FAIL")
          navigate(`/transaction/payment/failure`)
        } else {
          errMsg2 = result.data.message
        }

        setAlert({
          open: true,
          message: errMsg2,
          type: "error",
        });
      }
    } catch (error) {
      setAlert({
        open: true,
        message: error.message,
        type: "error",
      });
      return;
    }
  }

  return (
    <>
      <ConfirmationNavbar goBackUri={`/transaction/lender-selection/${detailsId}`} detailsId={detailsId} />

      {loading && <LinearProgress style={{ backgroundColor: "#4DBE0E" }} />}
      <CustomBox2 sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center", marginTop: "20px" }}>
        <h2>Transaction Confirmation</h2>

      </CustomBox2>
      <div className="card-container" style={{ display: 'flex', justifyContent: 'center', marginTop: 165 }}>
        {showCard1 &&
          <Card className="card" style={{ width: "500px" }}>
            <CardContent align="center">
              <h2>PAN Card Verification</h2>
              <br />
              <p>Please enter the last four digits of your PAN card</p>
              <br />
              <TextField type="number" value={panNumber} onChange={handlePanNumberChange} minLength="4" maxLength="4" disabled={formSubmitted}></TextField>
              {error && <div style={{ color: 'red' }}>Please enter 4 digits</div>}
              <br />
              <br />
              <Button onClick={handlePanSubmit} variant="contained" size="large" sx={{ backgroundColor: "#4DBE0E" }} disabled={!isValidInput} >Submit</Button>
            </CardContent>
          </Card>
        }
      </div>

      <div className="card-container" style={{ display: 'flex', justifyContent: 'center', marginTop: 20 }}>
        {showCard2 &&
          <Card className="card2" style={{ width: "500px" }}>
            <CardContent align="center">
              <h2>OTP Verification</h2>
              <br />
              <p>OTP sent on {user.mobile}</p>
              <br />
              <TextField type="number" value={otpNumber} onChange={handleOtpNumberChange} minLength="4" maxLength="4" ></TextField>
              {otpError && <div style={{ color: 'red' }}>Please enter 4 digits</div>}
              <br />
              <br />
              <Button onClick={handleOtpSubmit} variant="contained" size="large" sx={{ backgroundColor: "#4DBE0E" }} disabled={!isValidOtpInput || otpFormSubmitted}>Submit</Button>

            </CardContent>
          </Card>
        }

      </div>

    </>
  );
}

export default TwoFAuthentication;