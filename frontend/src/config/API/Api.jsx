import Axios from "axios";
const APP_ENV = import.meta.env.VITE_ENV;
const API_URL = APP_ENV == "DEV" ? import.meta.env.VITE_API_IP_TEST : import.meta.env.VITE_API_IP_PROD;

export const CreateUser = async(data) => {

    // const res = await Axios.get('https://jsonplaceholder.typicode.com/users', {
    //     headers: {
    //         'Content-Type': 'application/json',
    //     }
    // });
    // console.log(res)
    
    const res = await Axios.post(
        `${API_URL}api/users`,
        data
    ).catch((err) => {
        if(err.response.status !== 200){
            return err.response;
        }
    })
    // console.log(res)
    return res;
}
