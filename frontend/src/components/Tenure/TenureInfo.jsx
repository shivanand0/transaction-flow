import React from 'react';
import {CustomBox} from "../Lender/Styles.jsx";
import Grid from "@mui/material/Grid";
import CurrencyRupeeIcon from "@mui/icons-material/CurrencyRupee.js";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos.js";
import Radio from '@mui/material/Radio';



function TenureInfo({loanDuration,interestRate, monthlyInstallment,loanAmount,totalInterest}) {
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
                    <Grid item>
                        <Radio
                            // checked={selectedValue === 'a'}
                            // onChange={handleChange}
                            value="a"
                            name="radio-buttons"
                            inputProps={{ 'aria-label': 'A' }}
                        />
                    </Grid>

                    <Grid item xs={2}>
                        <h4>{loanDuration}</h4>
                        <small>months</small>
                    </Grid>

                    <Grid item xs  sx={{marginLeft: "10px", alignContent: "center"}}>
                        <h4><b> <CurrencyRupeeIcon sx={{fontSize: "12px"}}/></b> {monthlyInstallment} </h4>
                        <small>/month</small>

                    </Grid>
                    <Grid item xs={4}>
                        <h4><b> <CurrencyRupeeIcon sx={{fontSize: "12px"}}/></b> {loanAmount+totalInterest} </h4>
                        <small> Total @{interestRate}% p.a.</small>
                    </Grid>
                </Grid>
            </CustomBox>
        </>
    );
}

export default TenureInfo;