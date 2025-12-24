
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
    productImages: { id: string; imageUrl: string }[];
    productVariants: { id: string; value: string; variantId: string; variantName: string }[];
}
