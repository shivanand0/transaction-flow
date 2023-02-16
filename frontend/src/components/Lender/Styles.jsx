import { styled, Box } from "@mui/material";

export const CustomBox = styled(Box)(({ theme }) => ({
    maxWidth: 500,
    margin: "auto",
    marginTop: "5%",
    transform: "translateY(100%)",
    transition: "0.3s",
    boxShadow: "0 8px 40px -12px rgba(0,0,0,0.3)",
    "&:hover": {
        boxShadow: "0 16px 70px -12.125px rgba(0,0,0,0.3)"
    },

    [theme.breakpoints.down("sm")]: {
    width: "90%",
    marginTop: "8%"
    },
}));