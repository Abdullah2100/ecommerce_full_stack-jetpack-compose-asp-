
import axios from "axios";
import { api_auth } from "./api_config";
import ISubCategory from "@/model/ISubCategory";

async function getSubCategoriesByStoreId(storeId: string, pageNumber: number) {
    try {
        const result = await api_auth.get(`/api/SubCategory/${storeId}/${pageNumber}`);
        return result.data as ISubCategory[];
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

export { getSubCategoriesByStoreId };
