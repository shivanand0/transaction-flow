
import React, { useEffect, useState } from 'react'
import Navbar from "../../components/Navbar/Navbar.jsx";
import {CustomBox} from "../../components/Lender/Styles.jsx";
import TenureInfo from "../../components/Tenure/TenureInfo.jsx";
import {AppState} from "../../context/AppContext.jsx";

const TenureSelection= () => {

    const { lenderDetails,trackStageValues } = AppState();
    const lenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId].emiDetailsList : null

    return (
        <>
            <Navbar isHome={false} />
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select EMI</h3>
                {/*<small>Choose the EMI tenure that is best for you</small>*/}
            </CustomBox>
            {
                lenderDetailsList?.map((emiDetails) => {
                    return (
                        <TenureInfo
                        loanDuration={emiDetails.loanDuration}
                        interestRate={emiDetails.interestRate}
                        monthlyInstallment={emiDetails.monthlyInstallment}
                        loanAmount={emiDetails.loanAmount}
                        totalInterest={emiDetails.totalInterest}
                        />
                    )
                })
            }


        </>
    );
}

export default TenureSelection;