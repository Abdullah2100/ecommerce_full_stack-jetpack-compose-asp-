import { iAddress } from "./IAddress";

export interface iUserInfo {
      id: string
      name: string,
      phone: string
      email: string,
      storeName: string,
      thumbnail: string,
      address: iAddress[]
      store_id?: string,
      isActive: boolean,
      isAdmin: boolean
}