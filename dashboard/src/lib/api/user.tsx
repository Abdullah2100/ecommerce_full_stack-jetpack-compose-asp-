import { Util } from "@/util/globle";
import axios from "axios";
import { iUserInfo } from "../../model/iUserInfo";
import { iUserUpdateInfoDto } from "../../dto/response/iUserUpdateInfoDto";

async function getMyInfo() {
    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User/me';
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return result.data as iUserInfo
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

    const url = process.env.NEXT_PUBLIC_PASE_URL + '/api/User';
    try {
        const result = await axios.put(url,
            userData, {
            headers: {
                'Authorization': `Bearer ${Util.token}`

            }
        })
        if (result.status == 200) {
            console.log("result.data ", result.data);
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
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/pages`;
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

async function getUserAtPage(pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        })
        return (result.data as iUserInfo[])
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
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/User/status/${userId}`;
    try {
        const result = await axios.delete(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
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
    updateUser,
    getMyInfo,
    changeUserStatus,
    getUserPages,
    getUserAtPage
}