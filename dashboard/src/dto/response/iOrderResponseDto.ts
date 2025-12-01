import iOrderItemResponseDto from "./iOrderItemResponseDto";

interface IOrderResponseDto {
    id: string,
    name: string,
    userPhone: string,
    status: string,
    totalPrice: number,
    orderItems: iOrderItemResponseDto[]
}

interface IAdminReposeDto {
    orders: IOrderResponseDto[],
    pageNum: number
}
export type {
    IOrderResponseDto,
    IAdminReposeDto
}