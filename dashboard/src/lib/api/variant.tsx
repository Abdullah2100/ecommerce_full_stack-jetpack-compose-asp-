import axios from "axios";
import { api_auth } from "./api_config";
import IVariant from "@/model/IVariant";

async function getVarient(pageNumber: number) {
    try {
        const result = await api_auth.get(`/api/Variant/all/${pageNumber}`)
        const variants = result.data as IVariant[]
        console.log(`variants: ${JSON.stringify(variants)}`);
        return variants
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
    try {
        const result = await api_auth.get(`/api/Variant/pages`)
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
    try {
        const result = await api_auth.delete(`/api/Variant/${id}`, {
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

async function createVarient(data: IVariant) {
    try {
        const result = await api_auth.post(`/api/Variant`, {
            name: data.name
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

async function updateVarient(data: IVariant) {
    try {
        const result = await api_auth.put(`/api/Variant`, {
            id: data.id,
            name: data.name
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