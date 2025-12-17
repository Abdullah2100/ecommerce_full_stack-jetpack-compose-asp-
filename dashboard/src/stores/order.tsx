import axios from "axios";
import { Util } from "@/util/globle";
import iOrderStatusUpdateRequestDto from "../dto/request/iOrderStatusUpdateRequestDto";
import { create } from "zustand";
import {
  IAdminReposeDto,
  IOrderResponseDto,
} from "@/dto/response/iOrderResponseDto";
import { ToastOptions } from "react-toastify";

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
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Order/all/${pageNum}`;
    try {
      const result = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${Util.token}`,
        },
      });
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
    const url =
      process.env.NEXT_PUBLIC_PASE_URL + `/api/Order/orderStatusDefinition`;
    try {
      const result = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${Util.token}`,
        },
      });
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
    const url = process.env.NEXT_PUBLIC_PASE_URL + `/api/Order`;
    try {
      const result = await axios.put(
        url,
        {
          id: orderStatus.id,
          status: orderStatus.statsu,
        },
        {
          headers: {
            Authorization: `Bearer ${Util.token}`,
          },
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
