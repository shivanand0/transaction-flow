import React, { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import Navbar from '../../../components/Navbar/Navbar';
import LenderInfo from '../../../components/Lender/LenderInfo';
import reactSVG from '../../../assets/react.svg';
import { CustomBox } from '../../../components/Lender/Styles.jsx';
import { AppState } from '../../../context/AppContext';
import { GetLenderDetails, TrackStage } from '../../../config/API/Api';
import LinearProgress from '@mui/material/LinearProgress';

const LenderSelection = () => {
    const navigate = useNavigate();
    const { detailsId } = useParams();
    const { lenderDetails, setLenderdetails, loading, setLoading, setAlert, user, setUser, trackStageValues, setTrackStageValues } = AppState();

    const fetchLenderDetails = async () => {
        try {
            setLoading(true)
            const result = await GetLenderDetails(detailsId);
            setLoading(false)

            if (result.data.statusCode === 200) {
                setLenderdetails(result)

                setUser({
                    name: result.data.userName,
                    uid: result.data.userId,
                    mobile: result.data.mobileNumber,
                    amount: result.data.amount,
                    detailsId: detailsId
                })
            } else {
                setAlert({
                    open: true,
                    message: result.data.errorMessage || result.data.message,
                    type: "error",
                });
                return navigate("/transaction/payment/failure")
            }
        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    const handleLenderOnclick = async () => {
        try {
            const result = await TrackStage(trackStageValues.selection, trackStageValues.selectedLenderId, trackStageValues.selectedLenderInfoId, detailsId)
        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    const handleArrowOnClick = (lenderId) => {
        handleSelectLenderId(lenderId)
        handleLenderOnclick()
        return navigate(`/transaction/tenure-selection/${detailsId}`)
    }

    useEffect(() => {
        if (lenderDetails === null) fetchLenderDetails();
    }, [detailsId])

    useEffect(() => {
        handleLenderOnclick();
    }, [trackStageValues])

    const fetchedLenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList : null
    
    const handleSelectLenderId = (lenderId) => {
        setTrackStageValues({
            selection: "LENDER_SELECTION",
            selectedLenderId: lenderId,
            selectedLenderInfoId: null
        })
    }
    
    return (
        <>
            <Navbar isHome={false} goBackUri={`/`} />
            {loading && <LinearProgress style={{ backgroundColor: "#4DBE0E" }} />}
            <CustomBox sx={{marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select Lender</h3>

            </CustomBox>

            {
                fetchedLenderDetailsList?.map((lender) => {
                    const data = lender.emiDetailsList[0]
                    let str = `${data.monthlyInstallment} x ${data.loanDuration} ${data.tenureType}`
                    return (
                        <LenderInfo
                            img={reactSVG}
                            bankName={lender.lenderName}
                            emiStarting={str}
                            key={lender.lenderId}
                            onClickLender={() => handleSelectLenderId(lender.lenderId)}
                            onClickArrow={() => handleArrowOnClick(lender.lenderId)}
                        />

                    )
                })
            }

        </>
    )
}

export default LenderSelection