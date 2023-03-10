
import React, { useEffect, useState } from 'react'
import Navbar from "../../components/Navbar/Navbar.jsx";
import { CustomBox } from "../../components/Lender/Styles.jsx";
import TenureInfo from "../../components/Tenure/TenureInfo.jsx";
import { AppState } from "../../context/AppContext.jsx";
import { useNavigate, useParams } from 'react-router-dom';
import { Button } from '@mui/material';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { TrackStage } from '../../config/API/Api.jsx';
import LoadingButton from '@mui/lab/LoadingButton';
import SaveIcon from '@mui/icons-material/Save';
import { CustomBox2 } from "../../components/Lender/Styles.jsx";

const TenureSelection = () => {
    const navigate = useNavigate();
    const { detailsId } = useParams();

    const [selectedOption, setSelectedOption] = useState(null);

    const { lenderDetails, trackStageValues, setTrackStageValues, setAlert, loading, setLoading } = AppState();
    const lenderDetailsList = lenderDetails && lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1].emiDetailsList

    const handleSubmit = async () => {
        if (selectedOption === null) {
            setAlert({
                open: true,
                message: "Please select tenure before proceeding",
                type: "error",
            });
            return;
        }

        try {
            setLoading(true)
            const result = await TrackStage("TENURE_SELECTION", trackStageValues.selectedLenderId, selectedOption, detailsId)
            setLoading(false)

            return navigate(`/transaction/confirm/${detailsId}`)
        } catch (error) {
            setAlert({
                open: true,
                message: error.message,
                type: "error",
            });
            return;
        }
    }

    const handleSelectedTenureChange = (lenderInfoId) => {
        setSelectedOption(lenderInfoId)
        setTrackStageValues({
            selection: "TENURE_SELECTION",
            selectedLenderId: trackStageValues.selectedLenderId,
            selectedLenderInfoId: lenderInfoId
        })
    }
    useEffect(() => {
        if (lenderDetails === null || lenderDetailsList === null) {
            return navigate(`/transaction/lender-selection/${detailsId}`)
        }
    }, [])

    return (
        <>
            <Navbar isHome={false} goBackUri={`/transaction/lender-selection/${detailsId}`} isTenurePage={true} />
            <CustomBox2>
                <div style={{ display: "flex", justifyContent: "center", fontSize: "26px", fontWeight: "bold", textDecoration: "underline" }}>Select EMI</div>
                <p style={{ display: "flex", justifyContent: "center", textAlign: "center", fontSize: "15px", fontWeight: "bold", color: "#808080", padding: "10px" }}>Choose the EMI tenure that is best for you.</p>
            </CustomBox2>

            <div style={{ scrollBehavior: "scroll", marginBottom: "10px" }} >
                {
                    lenderDetails && lenderDetailsList?.map((emiDetails) => {
                        return (
                            <TenureInfo
                                key={emiDetails.lenderInfoId}
                                loanDuration={emiDetails.loanDuration}
                                interestRate={emiDetails.interestRate}
                                monthlyInstallment={emiDetails.monthlyInstallment}
                                loanAmount={emiDetails.loanAmount}
                                totalInterest={emiDetails.totalInterest}
                                onChangeOption={() => handleSelectedTenureChange(emiDetails.lenderInfoId)}
                                checkChecked={selectedOption === emiDetails.lenderInfoId}
                                lenderInfoId={emiDetails.lenderInfoId}
                                selectedLenderInfoId={selectedOption}
                            />
                        )
                    })
                }
            </div>
            <CustomBox2
                sx={{
                    display: "flex",
                    justifyContent: "center",
                    marginBottom: "50px"
                }}
            >
                {
                    loading && <LoadingButton
                        loading
                        loadingPosition="start"
                        size="large"
                        startIcon={<SaveIcon />}
                        variant="contained"
                        sx={{ backgroundColor: "#4DBE0E" }}
                    >
                        Continue
                    </LoadingButton>

                }
                {
                    !loading && <Button
                        variant="contained"
                        size="large"
                        onClick={handleSubmit}
                        sx={{ backgroundColor: "#4DBE0E" }}
                    >
                        Continue <ArrowForwardIcon sx={{ marginLeft: "10px" }} />
                    </Button>

                }

            </CustomBox2>
        </>
    );
}

export default TenureSelection;