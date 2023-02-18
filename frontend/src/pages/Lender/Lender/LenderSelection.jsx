import React, { useEffect, useState } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from '../../../components/Navbar/Navbar';
import LenderInfo from '../../../components/Lender/LenderInfo';
import reactSVG from '../../../assets/react.svg';
import { CustomBox } from '../../../components/Lender/Styles.jsx';
import { AppState } from '../../../context/AppContext';
import { GetLenderDetails, TrackStage } from '../../../config/API/Api';
import LinearProgress from '@mui/material/LinearProgress';

const LenderSelection = () => {
    const { trackId } = useParams();
    const { lenderDetails, setLenderdetails, loading, setLoading, setAlert, user, setUser } = AppState();

    const [selectedLenderId, setSelectedLenderId] = useState(null)

    const fetchLenderDetails = async () => {
        try {
            setLoading(true)
            // make change in /details api here: fetch details using trackId
            const result = await GetLenderDetails(user.uid, trackId);
            setLoading(false)

            setLenderdetails(result)

            setUser({
                name: result.data.userName,
                // uid: result.data.userId,
                uid: user.uid, // mocking - later this line will be removed
                mobile: result.data.mobileNumber,
                amount: result.data.amount,
                trackId: trackId
            })

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
            // make change in /details api here: fetch details using trackId
            const result = await TrackStage(trackStage, lenderId, null, trackId)

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
    }, [trackId])

    useEffect(() => {
        handleLenderOnclick(selectedLenderId, "LENDER_SELECTION");
    }, [selectedLenderId])

    const lenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList : null

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
                    return (
                        <LenderInfo
                            img={reactSVG}
                            bankName={lender.lenderName}
                            emiStarting={str}
                            key={lender.lenderId}
                            onClick={() => setSelectedLenderId(lender.lenderId) }
                        />
                    )
                })
            }
            {/* <LenderInfo img={reactSVG} bankName="ICICI Bank" emiStarting="2,300 x 3 Months" /> */}
        </>
    )
}

export default LenderSelection