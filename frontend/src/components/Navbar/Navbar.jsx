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
import { Button } from '@mui/material';
import ReactSVG from '../../assets/react.svg';

const Navbar = ({ isHome, goBackUri, isTenurePage, isTxnPage }) => {
    const navigate = useNavigate();
    const goBack = () => navigate(goBackUri);
    const { user, lenderDetails, trackStageValues } = AppState();

    const lenderInfo = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1] : null

    const [emiDetails, setEmiDetails] = useState({
        loanDuration: "",
        tenureType: "",
        monthlyInstallment: "",
        interest: "",
        loanAmount: "",
        totalAmount: ""
    })

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
                sx={{ display: "flex", justifyContent: "space-between", maxWidth: "400px" }}
            >
                {
                    !isHome && (
                        <Button
                            startIcon={<ArrowBackIcon />}
                            onClick={goBack}
                            variant="text"
                            sx={{ color: "#000" }}
                        >
                            Back
                        </Button>
                    )
                }
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>

            <CustomAppbar>
                <Container maxWidth="xl">
                    <Toolbar disableGutters>
                        <PhoneAndroidIcon sx={{ mr: 1 }} />
                        <Typography
                            noWrap
                            sx={{
                                fontFamily: 'monospace',
                                color: 'inherit',
                                textDecoration: 'none',
                                flex: 1
                            }}
                        >
                            {
                                !isHome && user.mobile
                            }
                        </Typography>

                        <Box>
                            AMOUNT : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /><strong>{!isHome && user.amount}</strong>
                        </Box>
                    </Toolbar>
                    {
                        isTenurePage &&
                        <Toolbar disableGutters sx={{ marginTop: "-20px", display: "flex", justifyContent: "space-between", borderTop: "1px solid #9999" }}>
                            <img src={lenderInfo && lenderInfo.secondaryLogoUrl} width="100px" alt="" />
                            <Box>
                                <strong>{lenderInfo && lenderInfo.lenderName}</strong>
                            </Box>
                        </Toolbar>
                    }

                    {
                        isTxnPage &&
                        <Toolbar disableGutters sx={{ display: "flex", justifyContent: "space-between", borderTop: "1px solid #9999" }}>
                            <Box>
                                <p>{lenderInfo && lenderInfo.lenderType}<span> : </span><CurrencyRupeeIcon sx={{ fontSize: "13px" }} />
                                    <strong>{emiDetails && emiDetails.monthlyInstallment}</strong>/month</p>
                                <br />
                                <strong>TOTAL AMOUNT : </strong><CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> <strong>{emiDetails && emiDetails.loanAmount + emiDetails.totalInterest}</strong>
                            </Box>
                            <Box>
                                TENURE : <strong>{emiDetails && emiDetails.loanDuration}</strong><span> </span>
                                <strong>{emiDetails && emiDetails.tenureType}</strong>
                                <br />
                                <br />
                                INTEREST : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /><strong>{emiDetails && emiDetails.totalInterest}</strong>
                            </Box>
                        </Toolbar>
                    }

                </Container>
            </CustomAppbar>

        </>
    )
}

export default Navbar