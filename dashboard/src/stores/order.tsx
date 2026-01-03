import axios from "axios";
import { api_auth } from "../lib/api/api_config";
import iOrderStatusUpdateRequestDto from "../dto/request/iOrderStatusUpdateRequestDto";
import { create } from "zustand";
import {
  IAdminReposeDto,
  IOrderResponseDto,
} from "@/dto/response/iOrderResponseDto";

interface IOrder {
  currentPage: number;
  pageNumb: number;
  isHasNewOrder: boolean;
  orderStatus: string[];
  orders: IOrderResponseDto[];
  getOrdersAt: (pageNumb: number) => void;
  getOrderStatus: () => void;
  updateOrderStatus: (
    orderStatus: iOrderStatusUpdateRequestDto
  ) => Promise<string>;
  changeHasNewOrderStatus: (status: boolean) => void;
}

const useOrder = create<IOrder>((set, get) => ({
  currentPage: 0,
  pageNumb: 1,
  isHasNewOrder: false,
  orderStatus: [],
  orders: [],
  getOrdersAt: async (pageNum: number) => {
    // if (pageNum === get().currentPage) return;
    set({ orders: [] });
    try {
      const result = await api_auth.get(`/api/Order/all/${pageNum}`);
      let data = result.data as IAdminReposeDto;
      set({
        orders: [...data.orders],
        pageNumb: data.pageNum,
        currentPage: pageNum,
      });

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

      // throw new Error(errorMessage);
    }
  },
  getOrderStatus: async () => {
    try {
      const result = await api_auth.get(`/api/Order/orderStatusDefinition`);
      let data = result.data as string[];
      set({ orderStatus: data });
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

      // throw new Error(errorMessage);
    }
  },
  updateOrderStatus: async (
    orderStatus: iOrderStatusUpdateRequestDto
  ): Promise<string> => {
    try {
      const result = await api_auth.put(
        `/api/Order`,
        {
          id: orderStatus.id,
          status: orderStatus.statsu,
        },
        {
          validateStatus: (status) => status >= 200 && status < 300,
        }
      );
      if (result.status == 204) {
        var orders = get().orders;
        let orderIndex = orders.findIndex((x) => x.id == orderStatus.id);
        if (orderIndex === -1) throw new Error("order not found");


        const newStatus = get().orderStatus[orderStatus.statsu];
        if (newStatus) {
          orders[orderIndex].status = newStatus;
        }
        set({ orders: orders });
        return "";
      } else {
        throw new Error("some thing wrong at server ");
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
  },
  changeHasNewOrderStatus: (status: boolean) => {
    set({ isHasNewOrder: status })
  }
}));
export default useOrder;
