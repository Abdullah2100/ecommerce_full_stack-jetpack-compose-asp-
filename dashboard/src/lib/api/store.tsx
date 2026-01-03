
import axios from "axios";
import { api_auth } from "./api_config";
import IStore from "@/model/IStore";



async function getStorePages() {
    try {
        const result = await api_auth.get(`/api/Store/pages`)
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

async function getStoresByName(name: string) {
    try {
        const result = await api_auth.get(`/api/Store/name/${name}`);
        return result.data as IStore[];
    } catch (error) {
        let errorMessage = "An unexpected error occurred";
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            errorMessage = error.message;
        }
        throw new Error(errorMessage);
    }
}

async function getStoreAtPage(pageNumber: number) {
    try {
        const result = await api_auth.get(`/api/Store/all/${pageNumber}`)
        return (result.data as IStore[])
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
    try {
        const result = await api_auth.patch(`/api/Store/status/${store_id}`)
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

async function createStore(data: FormData) {
    try {
        const result = await api_auth.post(`/api/Store`, data, {
            headers: {
                'Content-Type': 'multipairt/form-data'
            }
        })
        return result.data
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

async function updateStore(data: FormData) {
    try {
        const result = await api_auth.put(`/api/Store`, data, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        })
        return result.data
    } catch (error) {
        let errorMessage = "An unexpected error occurred";
        if (axios.isAxiosError(error)) {
            errorMessage = error.response?.data || error.message;
        } else if (error instanceof Error) {
            errorMessage = error.message;
        }
        throw new Error(errorMessage);
    }
}

export {
    getStoreAtPage,
    getStorePages,
    changeStoreStatus,
    createStore,
    updateStore,
    getStoresByName
}
