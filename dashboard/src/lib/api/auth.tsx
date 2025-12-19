import axios from "axios";
import { iLoginData } from "../../app/login/page";
import iAuthResult from "@/model/iAuthResult";




export async function login({ name, password }: iLoginData) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + '/api/User/login';
    console.log('login ur is ', url);

    try {
        var response = await axios.post(url,
            {
                username: name,
                password: password
            }
        )

        return response.data as iAuthResult;
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

