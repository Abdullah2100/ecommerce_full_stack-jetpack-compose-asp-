import axios from "axios";
import { iLoginData } from "../../app/login/page";




export async function login({ name, password }: iLoginData) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User/login';
    try {
        return await axios.post(url,
            {
                username: name,
                password: password
            }
        )
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

