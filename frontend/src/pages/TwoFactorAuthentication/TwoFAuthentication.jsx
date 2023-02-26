import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom'
import { CustomBox } from "../../components/Lender/Styles";
import { LinearProgress, TextField } from "@mui/material";
import { Button, Card, CardContent } from '@mui/material';
import { AppState } from '../../context/AppContext';
import { GetLenderDetails, InitTxn, TrackStage, VerifyNumber } from '../../config/API/Api';
import ConfirmationNavbar from '../../components/Navbar/ConfirmationNavbar';
import { useNavigate, redirect } from 'react-router-dom';
import {CustomBox2} from "../../components/Lender/Styles";
const TwoFAuthentication = () => {
  const navigate = useNavigate();
  const { detailsId } = useParams();
  const { lenderDetails, loading, setLoading, setAlert, user, setUser, trackStageValues, setTrackStageValues, transactionStatus, setTransactionStatus } = AppState();

  useEffect(() => {
    if (lenderDetails === null) {
      return navigate(`/transaction/lender-selection/${detailsId}`)
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
      const result = await VerifyNumber(panNumber, detailsId, "PAN");
      setLoading(false)

      if (result.data.status === true) {
        handleShowCard2();
        setValidInput(false);
        setFormSubmitted(true);

        // initiate the transaction /initTxn/initiate
        InitTxnFunc("INITIATE", result.data.message, "initiate")
      } else {
        let errMsg1 = "";
        if (result.data.message === "PAN-OTP-EXCEED") {
          // init fail txn
          InitTxnFunc("FAIL", result.data.message, "initiate")
          errMsg1 = "PAN Verification Failed"
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
      const result = await VerifyNumber(otpNumber, detailsId, "MOBILE");
      setLoading(false)

      if (result.data.status === true) {
        setOtpFormSubmitted(true);

        // confirm success txn
        InitTxnFunc("SUCCESS", result.data.message, "confirm")
      } else {
        let errMsg2 = "";
        if (result.data.message === "MOB-OTP-EXCEED") {
          // confirm fail txn
          InitTxnFunc("FAIL", result.data.message, "confirm")
          errMsg2 = "Mobile Verification Failed"
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

  const InitTxnFunc = async (status, remark, stage) => {
    try {
      setLoading(true)
      // status, detailsId, remark, stage
      const result = await InitTxn(status, detailsId, remark, stage);
      setLoading(false)

      if (result.data.statusCode === 201) {
        
        if (status === "SUCCESS") {
          setTransactionStatus("SUCCESS")
          return navigate(`/transaction/payment/success`)
        }
        else if (status === "FAIL") {
          setTransactionStatus("FAIL")
          return navigate(`/transaction/payment/failure`)}

      } else {
        setAlert({
          open: true,
          message: result.data.message,
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