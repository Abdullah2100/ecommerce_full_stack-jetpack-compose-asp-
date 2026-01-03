import axios from "axios";
import { api_auth } from "./api_config";
import ICategory from "@/model/ICategory";
import iCategoryDto from "@/dto/response/iCategoryDto";




async function getCategory(pageNumber: number) {
    try {
        const result = await api_auth.get(`/api/Category/all/${pageNumber}`)

        let data = result.data as ICategory[]
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


async function createCategory(data: iCategoryDto) {
    try {

        const dataHolder = new FormData();
        dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        const result = await api_auth.post('/api/Category', dataHolder)

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


async function deleteCategory(id: string) {
    try {
        const result = await api_auth.delete(`/api/Category/${id}`, {
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


async function updateCategory(data: iCategoryDto) {
    try {

        const dataHolder = new FormData();
        if (data.id) {
            dataHolder.append("id", data.id);
        }
        if (data.name.trim().length > 0)
            dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        const result = await api_auth.put('/api/Category', dataHolder)

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
    getCategory,
    createCategory,
    deleteCategory,
    updateCategory
}
