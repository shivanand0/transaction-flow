import React from 'react'
import { CustomBox } from './Styles'
import Grid from '@mui/material/Grid';
import CurrencyRupeeIcon from '@mui/icons-material/CurrencyRupee';
import ArrowForwardIosIcon from '@mui/icons-material/ArrowForwardIos';

const LenderInfo = ({ img, bankName, emiStarting, onClickLender,onClickArrow }) => {
    return (
        <>
            <CustomBox onClick={onClickLender}>
                <Grid
                    container
                    spacing={2}
                    sx={{
                        gap: "10px",
                        padding: "10px",
                        cursor: "pointer",
                        display:"flex", flexDirection:"row", justifyContent:"center", alignItems:"center"
                    }}
                >
                    <Grid item xs={2}>
                        <img src={img} width="60" alt="" />
                    </Grid>
                    <Grid item xs>
                        <p style={{ fontSize: "22px" ,fontWeight:"bold"}}>{bankName}</p>
                        <small style={{ fontSize: 15 }}>EMI starting <b>@<CurrencyRupeeIcon sx={{ fontSize: "12px" }} />{emiStarting}</b></small>
                    </Grid>
                    <Grid item xs={2}  >
                        <ArrowForwardIosIcon sx={{ color: "#4DBE0E" }} onClick={onClickArrow} />
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    )
}

export default LenderInfo