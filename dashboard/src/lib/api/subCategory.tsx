
import axios from "axios";
import { Util } from "@/util/globle";
import iSubCategory from "@/model/iSubCategory";

async function getSubCategoriesByStoreId(storeId: string, pageNumber: number) {
    const url = process.env.NEXT_PUBLIC_BASE_URL + `/api/SubCategory/${storeId}/${pageNumber}`;
    try {
        const result = await axios.get(url, {
            headers: {
                'Authorization': `Bearer ${Util.token}`
            }
        });
        return result.data as iSubCategory[];
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
