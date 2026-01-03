'use client';

import MyInfoPage from "./myinfo"
import Users from "./users"
import Stores from "./stores"
import Category from "./category"
import Product from "./product"
import Order from "./order"
import Analytics from "./analytics"
import { ToastContainer } from "react-toastify";
import Variant from "./varient";

interface iMainPageProp {
    currentPage: number
}

const Main = ({ currentPage }: iMainPageProp) => {


    return (<div className="py-10 px-5  w-full h-full" >
        {(() => {
            switch (currentPage) {
                case 0:
                    return <Analytics />;
                case 1:
                    return <Product />
                case 2:
                    return <Order />
                case 3:
                    return <Users />
                case 4:
                    return <Stores />
                case 5:
                    return <Category />
                case 6:
                    return <Variant />
                case 7:
                    return <MyInfoPage />;
                default:
                    return null;
            }
        })()}
 <ToastContainer />
    </div>
    )
}
export default Main