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
    const { lenderDetails, setLenderdetails, loading, setLoading, setAlert, user, setUser } = AppState();

    const [selectedLenderId, setSelectedLenderId] = useState()

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
                    message: result.data.errorMessage,
                    type: "error",
                });
                return navigate("/")
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

    const handleLenderOnclick = async (lenderId, trackStage) => {
        try {
            const result = await TrackStage(trackStage, lenderId, null, detailsId)

        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    useEffect(() => {
        fetchLenderDetails();
    }, [detailsId])

    useEffect(() => {
        handleLenderOnclick(selectedLenderId, "LENDER_SELECTION");
    }, [selectedLenderId])

    const lenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList : null

    const handleSelectLenderId = (lenderId) => {
        console.log("X" + lenderId)
        setSelectedLenderId(lenderId);
    }

    return (
        <>
            <Navbar isHome={false} />
            {loading && <LinearProgress style={{ backgroundColor: "#4DBE0E" }} />}
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select Lender</h3>

            </CustomBox>

            {
                lenderDetailsList?.map((lender) => {
                    const data = lender.emiDetailsList[0]
                    let str = `${data.monthlyInstallment} x ${data.loanDuration} ${data.tenureType}`
                    console.log(lender.lenderId)
                    return (
                        <LenderInfo
                            img={reactSVG}
                            bankName={lender.lenderName}
                            emiStarting={str}
                            key={lender.lenderId}
                            onClick={() => handleSelectLenderId(lender.lenderId)}
                        />
                    )
                })
            }

        </>
    )
}

export default LenderSelection