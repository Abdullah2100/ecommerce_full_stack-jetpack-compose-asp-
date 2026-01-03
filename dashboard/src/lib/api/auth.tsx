import axios from "axios";
import { api_json } from "./api_config";
import { iLoginData } from "@/app/login/page";
import IAuthResult from "@/model/IAuthResult";




export async function login({ name, password }: iLoginData) {

    try {
        var response = await api_json.post('/api/User/login',
            {
                username: name,
                password: password
            }
        )

        return response.data as IAuthResult;
    } catch (error) {
        // Extract meaningful error message
        let errorMessage = "An unexpected error occurred";

        if (axios.isAxiosError(error)) {
            // Server responded with error message
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            // Other JavaScript errors
            errorMessage = error.message;
        }

        throw new Error(errorMessage);
    }

}

