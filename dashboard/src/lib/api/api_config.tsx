import { Util } from "@/util/globle";
import axios from "axios";

const BASE_URL = process.env.NEXT_PUBLIC_BASE_URL || "";

export const api_json = axios.create({
    baseURL: BASE_URL,

})


export const api_auth = axios.create({
    baseURL: BASE_URL,
})

// Add a request interceptor to inject the token dynamically
api_auth.interceptors.request.use(
    (config) => {
        if (Util.token) {
            config.headers.Authorization = `Bearer ${Util.token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);
