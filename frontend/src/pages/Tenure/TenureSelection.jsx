
import React, { useEffect, useState } from 'react'
import Navbar from "../../components/Navbar/Navbar.jsx";
import {CustomBox} from "../../components/Lender/Styles.jsx";
import TenureInfo from "../../components/Tenure/TenureInfo.jsx";
import {AppState} from "../../context/AppContext.jsx";

const TenureSelection= () => {

    const { lenderDetails } = AppState();

    return (
        <>
            <Navbar isHome={false} />
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select EMI</h3>
                {/*<small>Choose the EMI tenure that is best for you</small>*/}
            </CustomBox>
            {
                lenderDetails[0]?.map((emiDetailsList) => {
                    return (
                        <TenureInfo
                        loanDuration={emiDetailsList.loanDuration}
                        interestRate={emiDetailsList.interestRate}
                        monthlyInstallment={emiDetailsList.monthlyInstallment}
                        loanAmount={emiDetailsList.loanAmount}
                        totalInterest={emiDetailsList.totalInterest}
                        />
                    )
                })
            }

        </>
    );
}

export default TenureSelection;