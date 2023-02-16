import React, { useEffect } from 'react'
import { useParams } from 'react-router-dom'
import Navbar from '../../../components/Navbar/Navbar';
import LenderInfo from '../../../components/Lender/LenderInfo';
import reactSVG from '../../../assets/react.svg';
import { CustomBox } from '../../../components/Lender/Styles.jsx';
import CircularProgress from '@mui/material/CircularProgress';
import Stack from '@mui/material/Stack';
import { AppState } from '../../../context/AppContext';
import { GetLenderDetails } from '../../../config/API/Api';

const LenderSelection = () => {
    const { trackId } = useParams();
    const { user, setLenderdetails } = AppState();

    const fetchLenderDetails = async() => {
        const data = {
            trackId: trackId,
            uid: user.uid
        }
        try {
            const result = await GetLenderDetails(data);
            setAlert({
              open: true,
              //message: `Successful. Welcome ${result.user.name}`,
              message: `Successful. Welcome`,
              type: "success",
            });
      
            // setLenderdetails({
            //   name: result.data.name,
            //   
            // })
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
    //   fetchLenderDetails();
    }, [trackId])
    
    return (
        <>
            <Navbar isHome={false} />
            <CustomBox sx={{ marginBottom: "-80px", display: "flex", justifyContent: "center" }}>
                <h3>Select Lender</h3>
            </CustomBox>
            {/* <CustomBox sx={{ marginTop: "100px", display: "flex", justifyContent: "center", boxShadow: "0" }}>
                <Stack spacing={2} direction="row">
                    <CircularProgress color="secondary" />
                    <CircularProgress color="success" />
                    <CircularProgress color="inherit" />
                </Stack>
            </CustomBox> */}
            <LenderInfo img={reactSVG} bankName="ICICI Bank" emiStarting="2,300 x 3 Months" />
            <LenderInfo img={reactSVG} bankName="Kotak Bank" emiStarting="2,800 x 3 Months" />
        </>
    )
}

export default LenderSelection