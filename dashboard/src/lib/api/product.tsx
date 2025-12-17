import axios from "axios";
import iProductResponseDto from "../../dto/response/iProductResponseDto";
import { Util } from "@/util/globle";

async function getProductPages() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Product/pages`;
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


async function getProductAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Product/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iProductResponseDto[])
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
    getProductAtPage,
    getProductPages
}
