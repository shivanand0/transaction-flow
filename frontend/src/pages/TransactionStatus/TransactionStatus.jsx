import React, { useState, useEffect } from 'react';
import { AppState } from "../../context/AppContext.jsx";
import CircularProgress from '@mui/material/CircularProgress';
import { CustomBox } from "../../components/Lender/Styles.jsx";
import Grid from "@mui/material/Grid";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee.js";
import PoweredBySVG from "../../assets/inline-powered-by.svg";
import { useParams, useNavigate } from 'react-router-dom';

const CheckStatus = ({ txnDetails }) => {
    return (
        <>
            <CustomBox>
                <Grid
                    container
                    spacing={2}
                    sx={{
                        gap: "10px",
                        padding: "10px",
                        cursor: "pointer"
                    }}
                >
                    <Grid item xs={2}>
                        <h4>Amount</h4>
                        <small> <CurrencyRupeeIcon sx={{ fontSize: "12px" }} /> {txnDetails && txnDetails.loanAmount}</small>
                    </Grid>

                    <Grid item xs sx={{ marginLeft: "80px", alignContent: "center" }}>
                        <h4>Tenure</h4>
                        <small>{txnDetails && txnDetails.loanDuration} Months</small>

                    </Grid>
                    <Grid item xs={4}>
                        <h4>EMI</h4>
                        <small> <CurrencyRupeeIcon sx={{ fontSize: "12px" }} /> {txnDetails && txnDetails.monthlyInstallment} /month</small>
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    )
}

const TransactionStatus = (props) => {
    const { txnStatus } = useParams()
    const navigate = useNavigate()
    const { trackStageValues, lenderDetails, user } = AppState()

    const [txnDetails, setTxnDetails] = useState({
        loanDuration: "",
        monthlyInstallment: "",
        loanAmount: ""
    })
    let status = false;
    if (txnStatus === "success") {
 
        status = true
    } else if (txnStatus === "failure") {
        status = false;
    } else {
        navigate("/")
    }

    const lenderInfo = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1] : null

    useEffect(() => {
        lenderInfo && lenderInfo.emiDetailsList.map((e) => {
            if (e.lenderInfoId === trackStageValues.selectedLenderInfoId) {
                setTxnDetails({
                    loanDuration: e.loanDuration,
                    monthlyInstallment: e.monthlyInstallment,
                    loanAmount: e.loanAmount,
                })
            }
        })
    }, [])

    const tenureDetails = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1].emiDetailsList[trackStageValues.selectedLenderInfoId - 1] : null

    const img = status ? "https://img.icons8.com/color/96/null/checked--v1.png" : "https://img.icons8.com/color/96/null/cancel--v1.png";

    const [countdown, setCountdown] = useState(3);

    useEffect(()=>{
        const intervalId = setInterval(()=>{
            setCountdown(countdown-1);
        },1000); 

        return ()=>clearInterval(intervalId);
    },[countdown]);

    useEffect(()=>{
        if(countdown===0){
           return navigate('/transaction/complete');
        }
    },[countdown]);

    return (

        <div>
            <CustomBox
                sx={{ display: "flex", justifyContent: "flex-end" }}
            >
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>

            <div style={{ marginTop: "200px", marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <img style={{ marginBottom: "-200px", display: "flex", justifyContent: "center" }} src={img} height="40px"
                    width="40px" />
            </div>
            <div style={{
                marginTop: "150px",
                marginBottom: "-100px",
                display: "flex",
                justifyContent: "center",
                flexDirection: "column",
                textAlign: "center"
            }}>
                <h3>Payment {status ? "Successful" : "Failed"}</h3>
                <br />
                <p>We've sent a confirmation message on your mobile number {user.mobile} </p>
                <br />
                <br />
            </div>

            {
                status && <CheckStatus txnDetails={txnDetails} />
            }
            {
                status && (
                    <>
                        <div style={{ marginTop: "200px", marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                            <CircularProgress color="success" />
                            {countdown>0?(
                                <p> Redirecting in {countdown} seconds</p>
                            ):(
                                <p>Redirecting</p>
                            )}
                        </div>
                    </>
                )

            }
            {/* <div style={{ display: "flex", justifyContent: "center", marginTop: "10%" }}>
                <Button
                    variant="contained"
                    size="large"
                    sx={{ backgroundColor: "#4DBE0E" }}
                    onClick={() => navigate("/")}
                >
                    Make Another Payment <ArrowForwardIcon sx={{ marginLeft: "10px" }} />
                </Button>
            </div> */}
        </div>
    );
}

export default TransactionStatus;