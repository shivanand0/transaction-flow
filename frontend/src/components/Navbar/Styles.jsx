import { AppBar, styled, Box } from "@mui/material";
export const CustomAppbar = styled(AppBar)(({ theme }) => ({
    position: "sticky",
    top: "60px",
    backgroundColor: "#fff",
    color: "#000",
    maxWidth: "400px",
    margin: "auto",
    [theme.breakpoints.down("sm")]: {
        width: "100%"
    },
}));

export const CustomBox = styled(Box)(({ theme }) => ({
    position: "sticky",
    top: "20px",
    backgroundColor: "#fff",
    color: "#000",
    width: "80%",
    margin: "auto",
    [theme.breakpoints.down("sm")]: {
        width: "100%"
    },
}));