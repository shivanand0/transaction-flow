import React from 'react'
import { CustomBox } from './Styles'
import Grid from '@mui/material/Grid';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const LenderInfo = ({ img, bankName, emiStarting,onClickArrow }) => {
    return (
        <>
            <CustomBox>
                <Grid
                    container
                    spacing={2}
                    sx={{
                        gap: "10px",
                        display:"flex", 
                        flexDirection:"row", 
                        justifyContent:"center", 
                        alignItems:"center",
                        padding: "0 0 10px 10px"
                    }}
                >
                    <Grid item xs={2.5} md={2}>
                        <img src={img} width="60" alt="" />
                    </Grid>
                    <Grid item xs>
                        <p style={{ fontSize: "20px" ,fontWeight:"bold", marginBottom: "10px" }}>{bankName}</p>
                        <small style={{ fontSize: "16px" }}> <span style={{ color: "#8E8E8E" }} >EMI starting </span> <b>@<CurrencyRupeeIcon sx={{ fontSize: "12px" }} />{emiStarting}</b></small>
                    </Grid>
                    <Grid item xs={2}  >
                        <ArrowForwardIosIcon sx={{ color: "#4DBE0E", cursor: "pointer" }} onClick={onClickArrow} />
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    )
}

export default LenderInfo