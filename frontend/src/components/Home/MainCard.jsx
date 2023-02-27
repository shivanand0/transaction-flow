import React, { useState, useEffect } from 'react'
import Card from '@mui/material/Card';
import CardHeader from '@mui/material/CardHeader';
import CardContent from '@mui/material/CardContent';
import CardActions from '@mui/material/CardActions';
import IconButton from '@mui/material/IconButton';
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import { Button, TextField, Grid, Box } from '@mui/material';
import { AppState } from '../../context/AppContext';
import { CreateUser } from '../../config/API/Api';
import { useNavigate, redirect } from 'react-router-dom';
import LoadingButton from '@mui/lab/LoadingButton';
import SaveIcon from '@mui/icons-material/Save';

const MainCard = () => {
  const navigate = useNavigate();
  const { setAlert, setUser, loading, setLoading, setLenderdetails, setTransactionStatus, setTrackStageValues } = AppState()

  useEffect(() => {
    setTransactionStatus("");
    setLenderdetails(null);
    setTrackStageValues({
      selection: "LENDER_SELECTION",
      selectedLenderId: null,
      selectedLenderInfoId: null
    });
    setUser({
      name: "",
      uid: "",
      mobile: "",
      amount: null,
      detailsId: ""
    })
  }, [])

  const [name, setName] = useState("")
  const [number, setNumber] = useState(null)
  const [amount, setAmount] = useState(null)

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!name || !number || !amount) {
      setAlert({
        open: true,
        message: "Please fill all the Fields",
        type: "error",
      });
      return;
    }

    try {
      setLoading(true)
      const result = await CreateUser(name, number, amount);
      setLoading(false)

      if (result.data.statusCode === 200) {
        setUser({
          name: name,
          uid: result.data.userId,
          mobile: number,
          amount: amount,
          detailsId: result.data.txnId
        })

        return navigate(`/transaction/lender-selection/${result.data.txnId}`)
      } else {
        setAlert({
          open: true,
          message: result.data.errorMessage,
          type: "error",
        });
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

  return (
    <Grid
      container
      spacing={0}
      direction="column"
      alignItems="center"
      justifyContent="center"
      sx={{
        maxWidth: 400,
        margin: "auto",
        transform: "translateY(20%)",
        transition: "0.3s",
        boxShadow: "0 8px 40px -12px rgba(0,0,0,0.3)",
        "&:hover": {
          boxShadow: "0 16px 70px -12.125px rgba(0,0,0,0.3)"
        }
      }}
    >
      <Card>
        <CardHeader
          title="Transaction Details"
          subheader="This page mocks merchant's checkout page & will not appear in production"
        />
        <CardContent>
          {/* Form */}
          <Box
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: "10px",
              padding: "10px"
            }}
          >
            <TextField
              variant="outlined"
              type="text"
              label="Name"
              size="small"
              value={name}
              onChange={(e) => setName(e.target.value)}
              fullWidth
            />
            <TextField
              variant="outlined"
              type="number"
              label="Mobile"
              size="small"
              value={number}
              onChange={(e) => setNumber(e.target.value)}
              fullWidth
            />
            <TextField
              variant="outlined"
              label="Amount"
              type="tel"
              size="small"
              value={amount}
              onChange={(e) => setAmount(e.target.value)}
              fullWidth
            />
          </Box>
        </CardContent>
        <CardActions style={{ display: "flex", justifyContent: "center" }} >
          <IconButton aria-label="add to favorites">
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

          </IconButton>
        </CardActions>
      </Card>
    </Grid>
  )
}

export default MainCard