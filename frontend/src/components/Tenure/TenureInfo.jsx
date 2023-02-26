import React, { useState } from 'react';
import { CustomBox } from "../Lender/Styles.jsx";
import Grid from "@mui/material/Grid";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee.js";
import Radio from '@mui/material/Radio';


function TenureInfo({ loanDuration, interestRate, monthlyInstallment, loanAmount, totalInterest, lenderInfoId, onChangeOption, checkChecked, selectedLenderInfoId }) {
    
    return (
        <>
            <CustomBox sx={{marginBottom:"-50px"}}>
                <Grid
                    container
                    spacing={2}
                    sx={{
                        gap: "10px",
                        padding: "10px",
                        cursor: "pointer"
                    }}
                >
                    <Grid item>
                        <Radio
                            checked={checkChecked}
                            onChange={onChangeOption}
                            value={lenderInfoId}
                            name="radio-buttons"
                            inputProps={{ 'aria-label': lenderInfoId }}
                        />
                    </Grid>

                    <Grid item xs={2} sx={{fontSize:"20px"}}>
                        <h4>{loanDuration}</h4>
                        <small>months</small>
                    </Grid>

                    <Grid item xs sx={{ marginLeft: "10px", alignContent: "center",fontSize:"20px" }}>
                        <h4><b> <CurrencyRupeeIcon sx={{ fontSize: "12px" }} /></b> {monthlyInstallment} </h4>
                        <small>/month</small>

                    </Grid>
                    <Grid item xs={4} sx={{fontSize:"20px"}}>
                        <h4><b> <CurrencyRupeeIcon sx={{ fontSize: "12px" }} /></b> {loanAmount + totalInterest} </h4>
                        <small> Total @{interestRate}% p.a.</small>
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    );
}

export default TenureInfo;