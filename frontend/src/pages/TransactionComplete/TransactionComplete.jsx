import React from "react"
import { AppState } from "../../context/AppContext";

const TransactionComplete = () => {
   const {user,transactionStatus}=AppState();
    return (
        <>
            <div style={{ justifyContent: "center", marginTop: "20px", display: "flex" }}>
                <h1>PG Payment Complete Page</h1>
            </div>

            <div style={{ justifyContent: "center", marginTop: "20px", display: "flex" }}>
                <h4>Transaction from Flexmoney complete</h4>
            </div>
            {/* <div style={{ justifyContent: "center", marginTop: "70px", display: "flex",color:"red" }}>
                DETAILS ID : {user.detailsId} 
            </div> */}
            <div style={{ justifyContent: "center", marginTop: "15px", display: "flex",color:"red" }}>
                NAME : {user.name} 
            </div>
            <div style={{ justifyContent: "center", marginTop: "15px", display: "flex",color:"red" }}>
                MOBILE NUMBER : {user.mobile} 
            </div>
            <div style={{ justifyContent: "center", marginTop: "15px", display: "flex",color:"red" }}>
                AMOUNT : {user.amount} 
            </div>
            <div style={{ justifyContent: "center", marginTop: "15px", display: "flex",color:"red" }}>
                STATUS :  {transactionStatus}
            </div>
        </>
    );
}

export default TransactionComplete;