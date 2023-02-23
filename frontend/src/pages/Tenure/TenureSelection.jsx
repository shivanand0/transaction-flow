
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

const TenureSelection = () => {
    const navigate = useNavigate();
    const { detailsId } = useParams();

    const [selectedOption, setSelectedOption] = useState(null);

    const { lenderDetails, trackStageValues, setAlert, loading, setLoading } = AppState();
    const lenderDetailsList = lenderDetails !== null ? lenderDetails.data.lenderDetailsList[trackStageValues.selectedLenderId - 1].emiDetailsList : null

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
    useEffect(() => {
        if (lenderDetails === null || lenderDetailsList === null) {
            return navigate(`/transaction/lender-selection/${detailsId}`)
        }
    }, [])

    return (
        <>
            <Navbar isHome={false} goBackUri={`/transaction/lender-selection/${detailsId}`} />
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select EMI</h3>
                {/*<small>Choose the EMI tenure that is best for you</small>*/}
            </CustomBox>
            {
                lenderDetailsList?.map((emiDetails) => {
                    return (
                        <TenureInfo
                            key={emiDetails.lenderInfoId}
                            loanDuration={emiDetails.loanDuration}
                            interestRate={emiDetails.interestRate}
                            monthlyInstallment={emiDetails.monthlyInstallment}
                            loanAmount={emiDetails.loanAmount}
                            totalInterest={emiDetails.totalInterest}
                            onChangeOption={() => setSelectedOption(emiDetails.lenderInfoId)}
                            checkChecked={selectedOption === emiDetails.lenderInfoId}
                            lenderInfoId={emiDetails.lenderInfoId}
                            selectedLenderInfoId={selectedOption}
                        />
                    )
                })
            }
            <CustomBox
                sx={{
                    marginTop: { md: "10%", sm: "20%", xs: "40%" },
                    display: "flex",
                    justifyContent: "center",
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

            </CustomBox>
        </>
    );
}

export default TenureSelection;