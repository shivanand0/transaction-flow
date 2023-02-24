import React, { useState, useEffect } from 'react'
import Box from '@mui/material/Box';
import Toolbar from '@mui/material/Toolbar';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';
import PhoneAndroidIcon from '@mui/icons-material/PhoneAndroid';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import { CustomAppbar } from './Styles';
import PoweredBySVG from '../../assets/inline-powered-by.svg'
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import { CustomBox } from './Styles';
import { AppState } from '../../context/AppContext';
import { useNavigate } from 'react-router-dom';

const ConfirmationNavbar = ({ goBackUri, detailsId }) => {
    const navigate = useNavigate();
    const goBack = () => navigate(goBackUri);
    const { user, lenderDetails, trackStageValues } = AppState();

    const [emiDetails, setEmiDetails] = useState({
        loanDuration: "",
        tenureType: "",
        monthlyInstallment: "",
        interest: "",
        loanAmount: "",
        totalAmount: ""
    })

    const lenderInfo = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1] : null

    useEffect(() => {
        lenderInfo && lenderInfo.emiDetailsList.map((e) => {
            if (e.lenderInfoId === trackStageValues.selectedLenderInfoId) {
                setEmiDetails({
                    loanDuration: e.loanDuration,
                    tenureType: e.tenureType,
                    monthlyInstallment: e.monthlyInstallment,
                    totalInterest: e.totalInterest,
                    loanAmount: e.loanAmount,
                    totalAmount: e.loanAmount + e.totalInterest
                })
                return;
            }
        })
    }, [])
    
    return (
        <>
            <CustomBox
                sx={{ display: "flex", justifyContent: "flex-end" }}
            >
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>
            <CustomAppbar>

                <Container maxWidth="100px" style={{ padding: "20px" }}>
                    <Toolbar disableGutters>

                        <Box
                            noWrap
                            sx={{
                                // mr: 2,
                                //   display: { xs: 'none', md: 'flex' },

                                color: 'inherit',
                                textDecoration: 'none',
                                flex: 1
                            }}

                        ><PhoneAndroidIcon sx={{ mr: 1, padding: "4px" }} />
                            <strong>{user && user.mobile}</strong>
                            <br />
                            <br />
                            LENDER: <strong>{lenderInfo && lenderInfo.lenderName}</strong>
                            <br />
                            <br />
                            <p>{lenderInfo && lenderInfo.lenderType}<span> : </span><CurrencyRupeeIcon sx={{ fontSize: "13px" }} />
                                <strong>{emiDetails && emiDetails.monthlyInstallment}</strong>/month</p>
                            <br />
                            <strong>TOTAL PAYABLE AMOUNT : </strong><CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> <strong>{emiDetails && emiDetails.loanAmount + emiDetails.totalInterest}</strong>
                        </Box>

                        <Box>
                            AMOUNT : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> <strong>{user && user.amount}</strong>
                            <br />
                            <br />
                            TENURE : <strong>{emiDetails && emiDetails.loanDuration}</strong><span> </span>
                            <strong>{emiDetails && emiDetails.tenureType}</strong>
                            <br />
                            <br />
                            INTEREST : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /><strong>{emiDetails && emiDetails.totalInterest}</strong>
                        </Box>

                    </Toolbar>
                </Container>
            </CustomAppbar>

            <CustomBox
                sx={{
                    marginTop: "70px",
                    display: "flex",
                    justifyContent: "flex-start",
                    padding: "0 0 10px 10px",
                    top: "130px"
                }}
                onClick={goBack}
            >
                <ArrowBackIcon /> Back
            </CustomBox>
        </>
    )
}

export default ConfirmationNavbar