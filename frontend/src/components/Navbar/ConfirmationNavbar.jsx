import React from 'react'
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

const ConfirmationNavbar = ({ isHome, goBackUri }) => {
    const navigate = useNavigate();
    const goBack = () => navigate(goBackUri);
    const { user, lenderDetails } = AppState();
    
    return (
        <>
            <CustomBox
                sx={{ display: "flex", justifyContent: "flex-end" }}
            >
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>
            <CustomAppbar>

                <Container maxWidth="100px" style={{padding:"20px"}}>
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
                            
                        ><PhoneAndroidIcon sx={{ mr: 1,padding:"4px" }} />
                            <strong>{!isHome && user.mobile}</strong>
                            <br />
                            <br />
                            LENDER: <strong>{!isHome && lenderDetails && lenderDetails.lenderName}</strong>
                            <br />
                            <br />
                            <p>{!isHome && lenderDetails && lenderDetails.lenderType}<span> : </span><CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> 
                            <strong>{!isHome && lenderDetails && lenderDetails.monthlyInstallment}</strong>/month</p>
                            <br />
                            <strong>TOTAL PAYABLE AMOUNT : </strong><CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> <strong>{!isHome && lenderDetails && lenderDetails.totalAmount}</strong>
                        </Box>

                        <Box>
                            AMOUNT : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /> <strong>{!isHome && user.amount}</strong>
                            <br />
                            <br />
                            TENURE : <strong>{!isHome && lenderDetails && lenderDetails.tenure}</strong><span> </span>
                            <strong>{!isHome && lenderDetails && lenderDetails.tenureType}</strong>
                            <br />
                            <br />
                            INTEREST : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /><strong>{!isHome && lenderDetails && lenderDetails.interest}</strong>
                        </Box>
                        
                    </Toolbar>
                </Container>
            </CustomAppbar>
            {
                !isHome && (
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
                )
            }
            
        </>
    )
}

export default ConfirmationNavbar