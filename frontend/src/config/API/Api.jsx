import Axios from "axios";
const APP_ENV = import.meta.env.VITE_ENV;
const API_URL = APP_ENV == "DEV" ? import.meta.env.VITE_API_IP_TEST : import.meta.env.VITE_API_IP_PROD;

// const res = await Axios.get('https://jsonplaceholder.typicode.com/users', {
//     headers: {
//         'Content-Type': 'application/json',
//     }
// });
// console.log(res)
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
    // console.log(res)
    return res;
}

export const GetLenderDetails = async (uid, trackId) => {
    const data = {
        userId: uid
        // trackId: trackId
    }

    const res = await Axios.post(
        `${API_URL}/details`,
        data
    ).catch((err) => {
        if (err.statusCode === 400 || err.statusCode === 500) {
            return err.errorMessage;
        }
    })
    // console.log(res)
    return res;
}

export const TrackStage = async (selection, selectedLenderId, selectedTenureId, trackId) => {
    const data = {
        selection: selection,
        selectedLenderId: selectedLenderId,
        selectedTenureId: selectedTenureId
    }

    const res = await Axios.post(
        `${API_URL}/trackStage/${trackId}`,
        data
    ).catch((err) => {
        if (err.statusCode === 400 || err.statusCode === 500) {
            return err.errorMessage;
        }
    })
    // console.log(res)
    return res;
}
