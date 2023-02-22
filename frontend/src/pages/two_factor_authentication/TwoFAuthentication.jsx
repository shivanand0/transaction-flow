import React, { useState,useEffect } from 'react';
import { useParams } from 'react-router-dom'
import { CustomBox } from "../../components/Lender/Styles";
import { LinearProgress, TextField } from "@mui/material";
import { Button, Card, CardContent } from '@mui/material';
import LenderInfo from '../../components/Lender/LenderInfo';
import reactSVG from '../../assets/react.svg';
import { AppState } from '../../context/AppContext';
import { GetLenderDetails, InitTxn, TrackStage, VerifyNumber } from '../../config/API/Api';
import ConfirmationNavbar from '../../components/Navbar/ConfirmationNavbar';
import { useNavigate, redirect } from 'react-router-dom';

const TwoFAuthentication = () => {
    const navigate = useNavigate();
    const { detailsId } = useParams();
    const { lenderDetails, setLenderdetails, loading, setLoading, setAlert, user, setUser } = AppState();

    const [selectedLenderId, setSelectedLenderId] = useState(null)

    const fetchLenderDetails = async () => {
        try {
            setLoading(true)
            const result = await GetLenderDetails(detailsId);
            setLoading(false)

            setLenderdetails(result)

            setUser({
                name: result.data.userName,
                uid: result.data.userId,
                mobile: result.data.mobileNumber,
                amount: result.data.amount,
                detailsId: detailsId
            })


        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    const handleLenderOnclick = async (lenderId, trackStage) => {
        try {
            const result = await TrackStage(trackStage, lenderId, null, detailsId)

        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    useEffect(() => {
        fetchLenderDetails();
    }, [detailsId])

    useEffect(() => {
        handleLenderOnclick(selectedLenderId, "TWO_FACTOR_AUTHENTICATION");
    }, [selectedLenderId])

    const lenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList : null
    console.log(lenderDetails)

    const [showCard2, setShowCard2] = useState(false);
    const [panNumber,setPanNumber] = useState(null);
    const [otpNumber,setOtpNumber] = useState(null);
    const [error, setError] = useState(false);
    const [otpError, setOtpError] = useState(false);
    const [isValidInput, setValidInput] =useState(false);
    const [formSubmitted, setFormSubmitted] =useState(false);
    const [isValidOtpInput, setValidOtpInput] =useState(false);
    const handleShowCard2 = () => {
        setShowCard2(true);
    }
    const handlePanNumberChange =  (event) =>{
        const newValue = event.target.value;
        if (newValue.length===4) {
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
    const handleOtpNumberChange =  (event) =>{
        const newValue = event.target.value;
        if (newValue.length===4) {
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
          const result = await VerifyNumber(panNumber,detailsId,"PAN");
          setLoading(false)
    
          if (result.data.statusCode === 202) {
            handleShowCard2();
            setValidInput(false);
            setFormSubmitted(true);
        } else {
            setAlert({
              open: true,
              message: result.data.errorMessage,
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
          const result = await VerifyNumber(otpNumber,detailsId,"MOBILE");
          setLoading(false)
    
          if (result.data.statusCode === 202) {
                e.preventDefault();
                try {
                  setLoading(true)
                  const result = await InitTxn("SUCCESS",detailsId);
                  setLoading(false)
            
                  if (result.data.statusCode === 201) {
                    setAlert({
                        open: true,
                        message: `Successful.`,
                        type: "success",
                      });
          
                      return navigate(`/`)
                      
                } else {
                    setAlert({
                      open: true,
                      message: result.data.errorMessage,
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
        } else {
            setAlert({
              open: true,
              message: result.data.errorMessage,
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
            <ConfirmationNavbar isHome={false} />
            {loading &&<LinearProgress style={{ backgroundColor: "#4DBE0E" }} />}
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h2>Transaction Confirmation</h2>
                
            </CustomBox>
            <div className="card-container" style={{ display: 'flex', justifyContent: 'center', marginTop: 165 }}>

                <Card className="card" style={{width:"500px"}}>
                    <CardContent align="center">
                        <h2>PAN Card Verification</h2>
                        <br />
                        <p>Please enter the last four digits of your PAN card</p>
                        <br />
                        <TextField type="number" value={panNumber} onChange={handlePanNumberChange} minLength="4" maxLength="4" disabled={formSubmitted}></TextField>
                        {error && <div style={{ color: 'red' }}>Please enter 4 digits</div>}
                         <br />
                         <br />
                        <Button onClick={handlePanSubmit} variant="contained" color="primary" disabled={!isValidInput} >Submit</Button>
                    </CardContent>
                </Card>
            </div>
            
            <div className="card-container" style={{ display: 'flex', justifyContent: 'center', marginTop: 20 }}>
                {showCard2 &&
                    <Card className="card2" style={{width:"500px"}}>
                        <CardContent align="center">
                            <h2>OTP Verification</h2>
                            <br />
                            <p>OTP sent on mobile number</p>
                            <br />
                            <TextField type="number" value={otpNumber} onChange={handleOtpNumberChange} minLength="4" maxLength="4" ></TextField>
                            {otpError && <div style={{ color: 'red' }}>Please enter 4 digits</div>}
                            <br />
                            <br />
                            <Button onClick={handleOtpSubmit} variant="contained" color="primary"  disabled={!isValidOtpInput}>Submit</Button>
                        </CardContent>
                    </Card>
                }

            </div>

        </>
    );
}

export default TwoFAuthentication;