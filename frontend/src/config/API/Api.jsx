import Axios from "axios";
const APP_ENV = import.meta.env.VITE_ENV;
const API_URL = APP_ENV == "DEV" ? import.meta.env.VITE_API_IP_TEST : import.meta.env.VITE_API_IP_PROD;

export const CreateUser = async (name, number, amount) => {
    const data = {
        userName: name,
        mobileNumber: number,
        amount: amount
    }
    const res = await Axios.post(
        `${API_URL}/users`,
        data
    ).catch((err) => {
        if (err.statusCode === 400 || err.statusCode === 500) {
            return err.errorMessage;
        }
    })
    
    return res;
}

export const GetLenderDetails = async (detailsId) => {

    const res = await Axios.post(
        `${API_URL}/details?uuid=${detailsId}`
    ).catch((err) => {
        if (err.statusCode === 400 || err.statusCode === 500) {
            return err.errorMessage;
        }
    })
    // console.log(res)
    return res;
}

export const TrackStage = async (selection, selectedLenderId, selectedLenderInfoId, detailsId) => {
    const data = {
        selection: selection,
        selectedLenderId: selectedLenderId,
        selectedLenderInfoId: selectedLenderInfoId
    }

    const res = await Axios.post(
        `${API_URL}/trackStage/${detailsId}`,
        data
    ).catch((err) => {
        if (err.statusCode === 400 || err.statusCode === 500) {
            return err.errorMessage;
        }
    })
    // console.log(res)
    return res;
}
