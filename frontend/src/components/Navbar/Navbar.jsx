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
const Navbar = ({ number, amount, isHome }) => {
    return (
        <>
            <CustomBox
                sx={{ display: "flex", justifyContent: "flex-end" }}
            >
                <img src={PoweredBySVG} alt="Your SVG" width="150" />
            </CustomBox>
            <CustomAppbar>

                <Container maxWidth="xl">
                    <Toolbar disableGutters>
                        <PhoneAndroidIcon sx={{ mr: 1 }} />
                        <Typography
                            noWrap
                            sx={{
                                // mr: 2,
                                //   display: { xs: 'none', md: 'flex' },
                                fontFamily: 'monospace',
                                color: 'inherit',
                                textDecoration: 'none',
                                flex: 1
                            }}
                        >
                            {number}
                        </Typography>

                        <Box>
                            AMOUNT : <CurrencyRupeeIcon sx={{ fontSize: "13px" }} /><strong>{amount}</strong>
                        </Box>
                    </Toolbar>
                </Container>
            </CustomAppbar>
            {
                !isHome && (
                    <CustomBox
                        sx={{
                            display: "flex",
                            justifyContent: "flex-start",
                            padding: "0 0 10px 10px",
                            top: "120px"
                        }}
                    >
                    <ArrowBackIcon /> Back
                </CustomBox>
                )
            }
            
        </>
    )
}

export default Navbar