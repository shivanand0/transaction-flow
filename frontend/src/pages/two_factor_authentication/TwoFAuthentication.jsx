import React, { useState,useEffect } from 'react';
import { useParams } from 'react-router-dom'
import Navbar from "../../components/Navbar/Navbar";
import { CustomBox } from "../../components/Lender/Styles";
import { LinearProgress, TextField } from "@mui/material";
import { Button, Card, CardContent } from '@mui/material';
import LenderInfo from '../../components/Lender/LenderInfo';
import reactSVG from '../../assets/react.svg';
import { AppState } from '../../context/AppContext';
import { GetLenderDetails, TrackStage } from '../../config/API/Api';

const TwoFAuthentication = () => {
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

    const [showCard2, setShowCard2] = useState(false);
    const [panNumber,setPanNumber] = useState('');
    const [otpNumber,setOtpNumber] = useState('');
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
        if (newValue.length===7) {
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
    const handlePanSubmit = (event) => {
        // Do something with the number
        event.preventDefault();
        console.log(panNumber);
        setFormSubmitted(true);
        setValidInput(false);
        handleShowCard2();
      }
      const handleOtpSubmit = (event) => {
        // Do something with the number
        setFormSubmitted(true);
        event.preventDefault();
        console.log(otpNumber);
      }
    return (
        <>
            <Navbar isHome={false} />
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
                            <TextField type="number" value={otpNumber} onChange={handleOtpNumberChange} minLength="7" maxLength="7" ></TextField>
                            {otpError && <div style={{ color: 'red' }}>Please enter 7 digits</div>}
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