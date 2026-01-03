import { iAddress } from "./IAddress";

export interface IUserInfo {
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