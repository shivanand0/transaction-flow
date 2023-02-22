import React from 'react'
import { CustomBox } from './Styles'
import Grid from '@mui/material/Grid';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const LenderInfo = ({ img, bankName, emiStarting, onClick }) => {
    return (
        <>
            <CustomBox onClick={onClick}>
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
                        <img src={img} alt="" />
                    </Grid>
                    <Grid item xs>
                        <h4>{bankName}</h4>
                        <small>EMI starting <b>@<CurrencyRupeeIcon sx={{ fontSize: "12px" }} />{emiStarting}</b></small>
                    </Grid>
                    <Grid item xs={2}>
                        <ArrowForwardIosIcon sx={{ color: "#4DBE0E" }} />
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    )
}

export default LenderInfo