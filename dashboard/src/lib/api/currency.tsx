import { ICurrency } from "@/model/ICurrency";
import { Util } from "@/util/globle";
import axios from "axios";

export async function getCurrencies(pageNumber: number = 1) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Currencies/all/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })

        let data = result.data as ICurrency[]
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