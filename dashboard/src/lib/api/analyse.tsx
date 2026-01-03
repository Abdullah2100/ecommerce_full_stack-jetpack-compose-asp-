import { IAnalayes } from "@/model/IAnalyse";
import { api_auth } from "./api_config";
import axios from "axios";

export async function getAnalyse() {
    try {
        const result = await api_auth.get('/api/analyse/currentMonth')

        let data = result.data as IAnalayes
        return data
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