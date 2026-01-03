import { IProductVariant } from "@/model/IProductVariant";

export default interface iProductResponseDto {
    id: string;
    name: string;
    description: string;
    price: number;
    symbol: string;
    thumbnail: string;
    storeId: string;
    storeName: string;
    subcategoryId: string;
    subCategoryName: string;
    productImages: string[];
    productVariants: IProductVariant[][];
}
