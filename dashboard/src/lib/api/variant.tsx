import axios from "axios";
import { Util } from "@/util/globle";
import { iVarient } from "@/model/iVarient";

async function getVarient(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Variant/all/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as iVarient[]
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

async function getVarientPageLenght() {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Variant/pages`;
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

async function deleteVarient(id: string) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Variant/${id}`;
    try {
        const result = await axios.delete(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
            validateStatus: (status) => status >= 200 && status < 300
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

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

async function createVarient(data: iVarient) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Variant`;
    try {
        const result = await axios.post(url, {
            id: data.id,
            name: data.name
        }, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

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

async function updateVarient(data: iVarient) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Variant`;
    try {
        const result = await axios.put(url, {
            id: data.id,
            name: data.name
        }, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            },
        })

        if (result.status == 204) {
            return true
        } else {
            return false
        }

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
    getVarient,
    getVarientPageLenght,
    deleteVarient,
    createVarient,
    updateVarient
}