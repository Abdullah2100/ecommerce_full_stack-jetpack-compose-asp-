import axios from "axios";
import { Util } from "@/util/globle";
import iStore from "../../model/iStore";



async function getStorePages() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/pages`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as number
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

async function getStoreAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/all/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iStore[])
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

async function changeStoreStatus(store_id: string) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Store/status/${store_id}`;
    try {
        const result = await axios.patch(url, undefined, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },

        })
        return result.data as boolean
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

export {
    getStoreAtPage,
    getStorePages,
    changeStoreStatus
}