import React,{useState} from 'react';
import Navbar from "../../components/Navbar/Navbar";
import { CustomBox } from "../../components/Lender/Styles";
import { LinearProgress } from "@mui/material";
import { Card, CardContent } from '@mui/material';

const TwoFAuthentication = ()=>{

    return(
        <>
        <Navbar isHome={false} />
            {<LinearProgress style={{ backgroundColor: "#4DBE0E" }} />}
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h2>Transaction Confirmation</h2>

            </CustomBox>
            <div className="card-container" style={{ display: 'flex', justifyContent: 'center', marginTop: 160 }}>

                <Card className="card">
            <CardContent align="center">
            <h2>PAN CARD Verification</h2>
            <br />
            <p>Please enter the last four digits of your PAN card</p>
            {/* <Button onClick={handleShowCard2} variant="contained" color="primary">Submit</Button> */}
            </CardContent>
        </Card>
            </div>
        </>
    );
}

export default TwoFAuthentication;