
import axios from "axios";
import { api_auth } from "./api_config";
import { IUserInfo } from "../../model/IUserInfo";
import { iUserUpdateInfoDto } from "../../dto/response/iUserUpdateInfoDto";

async function getMyInfo() {
    try {
        const result = await api_auth.get('/api/User/me')
        return result.data as IUserInfo
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


async function updateUser({
    name,
    newPassword,
    password,
    phone,
    thumbnail
}: iUserUpdateInfoDto) {
    const userData = new FormData()
    if (name != null && name?.trim().length > 1)
        userData.append("name", name)
    if (phone != null && phone.trim().length > 1)
        userData.append("phone", phone)
    if (thumbnail != null && thumbnail != undefined)
        userData.append("thumbnail", thumbnail)
    if (password != null && password != undefined)
        userData.append("password", password)
    if (newPassword != null && newPassword != undefined)
        userData.append("newPassword", newPassword)

    try {
        const result = await api_auth.put('/api/User', userData)
        if (result.status == 200) {
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

async function getUserPages() {
    try {
        const result = await api_auth.get(`/api/User/pages`)
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

async function getUserAtPage(pageNumber: number) {
    try {
        const result = await api_auth.get(`/api/User/${pageNumber}`)
        return (result.data as IUserInfo[])
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

async function changeUserStatus(userId: string) {
    try {
        const result = await api_auth.delete(`/api/User/status/${userId}`)
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


async function createUser(userData: any) {

    try {
        const result = await api_auth.post('/api/User/signup', userData)
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

async function updateUserData(userData: any) {

    try {
        const result = await api_auth.post('/api/User/signup', userData)
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
    updateUser,
    getMyInfo,
    changeUserStatus,
    getUserPages,
    getUserAtPage,
    createUser
}