import iOrderProductDto from "./iOrderProductDto";
import iOrderProductVarientDto from "./iOrderProductVarientDto";

export default interface iOrderItemResponseDto {
    price: number,
    quantity: number,
    product: iOrderProductDto,
    productVariant: iOrderProductVarientDto
}