import axios from "axios";
import { Util } from "@/util/globle";
import iCategory from "@/model/iCategory";
import iCategoryDto from "@/dto/response/iCategoryDto";




async function getCategory(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Category/all/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })

        let data = result.data as iCategory[]
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
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Category`;
    try {

        const dataHolder = new FormData();
        dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        const result = await axios.post(url, dataHolder, {

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


async function deleteCategory(id: string) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Category/${id}`;
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


async function updateCategory(data: iCategoryDto) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/Category`;
    try {

        const dataHolder = new FormData();
        if (data.id) {
            dataHolder.append("id", data.id);
        }
        if (data.name.trim().length > 0)
            dataHolder.append("name", data.name)
        if (data.image != undefined)
            dataHolder.append("image", data.image)

        const result = await axios.put(url, dataHolder, {

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
    getCategory,
    createCategory,
    deleteCategory,
    updateCategory
}
