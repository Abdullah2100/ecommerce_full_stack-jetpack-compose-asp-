
import axios from "axios";
import { api_auth } from "./api_config";
import iProductResponseDto from "@/dto/response/iProductResponseDto";

async function getProductPages() {
    try {
        const result = await api_auth.get(`/api/Product/pages`)
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
    try {
        const result = await api_auth.get(`/api/Product/all/${pageNumber}`)
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

async function createProduct(data: FormData) {
    try {
        const result = await api_auth.post(`/api/Product`, data);
        return result.data;
    } catch (error) {
        let errorMessage = "An unexpected error occurred";
        if (axios.isAxiosError(error)) {
            const data = error.response?.data;
            errorMessage = typeof data === 'object' ? JSON.stringify(data) : (data || error.message);
        } else if (error instanceof Error) {
            errorMessage = error.message;
        }
        throw new Error(errorMessage);
    }
}

async function updateProduct(data: FormData) {
    try {
        const result = await api_auth.put(`/api/Product`, data);
        return result.data;
    } catch (error) {
        let errorMessage = "An unexpected error occurred";
        if (axios.isAxiosError(error)) {
            const data = error.response?.data;
            errorMessage = typeof data === 'object' ? JSON.stringify(data) : (data || error.message);
        } else if (error instanceof Error) {
            errorMessage = error.message;
        }
        throw new Error(errorMessage);
    }
}

async function deleteProduct(storeId: string, productId: string) {
    try {
        const result = await api_auth.delete(`/api/Product/${storeId}/${productId}`);
        return result.data;
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
    getProductAtPage,
    getProductPages,
    createProduct,
    updateProduct,
    deleteProduct
}
