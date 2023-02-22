import React from 'react';
import {AppState} from "../../context/AppContext.jsx";
import CircularProgress from '@mui/material/CircularProgress';
import {CustomBox} from "../../components/Lender/Styles.jsx";
import Grid from "@mui/material/Grid";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee.js";
import PoweredBySVG from "../../assets/inline-powered-by.svg";


const CheckStatus = () => {
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
                        <small> <CurrencyRupeeIcon sx={{fontSize: "12px"}}/> 10000</small>
                    </Grid>

                    <Grid item xs sx={{marginLeft: "80px", alignContent: "center"}}>
                        <h4>Tenure</h4>
                        <small>3 months</small>

                    </Grid>
                    <Grid item xs={4}>
                        <h4>EMI</h4>
                        <small> <CurrencyRupeeIcon sx={{fontSize: "12px"}}/> 3468/month</small>
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    )
}

const TransactionStatus = (props) => {

    const {lenderDetails, trackStageValues, user} = AppState();
    const tenureDetails = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1].emiDetailsList[trackStageValues.selectedLenderInfoId - 1] : null
    const status = true;
    const img = status ? "https://img.icons8.com/color/96/null/checked--v1.png" : "https://img.icons8.com/color/96/null/cancel--v1.png";

    return (

        <div>
            <CustomBox
                sx={{ display: "flex", justifyContent: "flex-end" }}
            >
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>

            <div style={{marginTop: "200px", marginBottom: "-80px", display: "flex", justifyContent: "center"}}>
                <img style={{marginBottom: "-200px", display: "flex", justifyContent: "center"}} src={img} height="40px"
                     width="40px"/>
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
                <p>We've sent a confirmation message on your mobile number {user.mobile} </p>
            </div>

            {
                status ? <CheckStatus/> : <div></div>
            }
            {
                status ?
                    <div style={{marginTop: "200px", marginBottom: "-80px", display: "flex", justifyContent: "center"}}>
                        <CircularProgress color="success"/>
                    </div> :
                    <div></div>
            }

        </div>
    );
}

export default TransactionStatus;