import iProductVarientDto from "./iProductVarientDto";

export default interface iProductResponseDto {
    id: string,
    name: string,
    description: string,
    thumbnail: string,
    subcategory: string,
    storeName: string,
    price: number,
    symbol: string,
    productVariants: iProductVarientDto[][] | undefined
    productImages: string[]
}