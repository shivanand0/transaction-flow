import { styled, Box } from "@mui/material";

export const CustomBox = styled(Box)(({ theme }) => ({
    maxWidth: 500,
    margin: "auto",
    marginTop: "40px",
    transition: "0.3s",
    boxShadow: "0 8px 40px -12px rgba(0,0,0,0.4)",
    "&:hover": {
        boxShadow: "0 16px 70px -12.125px rgba(0,0,0,0.6)"
    },

    [theme.breakpoints.down("sm")]: {
        width: "100%",
        padding: "0 5px 0 5px"
    },
}));

export const CustomBox2 = styled(Box)(({ theme }) => ({
    margin: "auto",
    paddingTop: "3em"
}));