import { mockOrders, mockOrderStatus } from "@/lib/mockData";
import { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import iOrderStatusUpdateRequestDto from "@/dto/request/iOrderStatusUpdateRequestDto";
import useOrder from "@/stores/order";
import { useMutation } from "@tanstack/react-query";
import iOrderItemResponseDto from "@/dto/response/iOrderItemResponseDto";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";
import Image from "next/image";

const Order = () => {
  const {
    updateOrderStatus,
    getOrdersAt,
    getOrderStatus,
    orders,
    orderStatus,
    pageNumb,
    currentPage,
  } = useOrder();

  const chageOrderStatus = useMutation({
    mutationFn: (orderStatus: iOrderStatusUpdateRequestDto) =>
      updateOrderStatus(orderStatus),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: (res) => {
      toast.success("تم التعديل بنجاح");
    },
  });

  const [selectedItem, setNewSelectedItem] = useState<iOrderItemResponseDto[] | undefined>(undefined)
  const [currentIndex, changeCurrentIndex] = useState<number>(0)

  useEffect(() => {
    getOrdersAt(1);
    getOrderStatus()
  }, []);

  if (orders == null) return;
  console.log(`funtion is Called ${JSON.stringify(orders)}`);

  return (
    <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-blue-600 bg-clip-text text-transparent">
          Orders
        </h1>
      </div>
      {selectedItem !== undefined &&
        <>

          <div className="fixed inset-0 bg-black/50 z-50  flex items-center justify-center" >

            <div className="h-[800px] w-[700px] bg-white rounded-xl flex ">
              <button
                onClick={() => setNewSelectedItem(undefined)}
                className=" fixed    h-10 w-10 flex items-center justify-center rounded-lg bg-muted transition-colors"
              >
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
              </button>
              <div className="h-[800px] w-[700px] flex flex-col   items-center justify-center">
                <Image
                  alt="ffff"
                  src={convertImageToValidUrl(selectedItem[currentIndex].product.thumbnail)}
                  width={450}
                  height={450}
                  className="object-contain"
                  priority
                />
                <div>
                  <h1>{selectedItem[currentIndex].product.name}</h1>
                </div>
                <div>
                  <h1 className="font-bold">{'Quantity : ' + selectedItem[currentIndex].quantity}</h1>
                </div>
              </div>
              <button
                onClick={() => {
                  if (currentIndex != 0)
                    changeCurrentIndex(currentIndex - 1)
                }}
                className="absolute left-4 top-1/2 -translate-y-1/2 h-12 w-12 flex items-center justify-center rounded-full bg-background/90 border border-border/50 shadow-lg hover:bg-background hover:scale-110 transition-all duration-200"
              >
                <svg
                  style={{ color: currentIndex > 0 ? 'black' : 'gray' }}
                  xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="15 18 9 12 15 6"></polyline>
                </svg>
              </button>
              <button
                onClick={() => {
                  if (currentIndex + 1 > selectedItem.length)
                    changeCurrentIndex(currentIndex + 1)
                }}
                className="absolute right-4 top-1/2 -translate-y-1/2 h-12 w-12 flex items-center justify-center rounded-full bg-background/90 border border-border/50 shadow-lg hover:bg-background hover:scale-110 transition-all duration-200"
              >
                <svg
                  style={{ color: currentIndex + 1 < selectedItem.length ? 'black' : 'gray' }}
                  xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                  <polyline points="9 18 15 12 9 6"></polyline>
                </svg>
              </button>
            </div>
          </div>
        </>

      }

      <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-sm text-left">
            <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
              <tr>
                <th className="px-6 py-4">#</th>
                <th className="px-6 py-4">Customer</th>
                <th className="px-6 py-4">Status</th>
                <th className="px-6 py-4 text-right">Total</th>
                <th className="px-6 py-4 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/50">
              {orders.map((value, index) => (
                <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                  <td className="px-6 py-4 text-muted-foreground font-mono text-xs">#{value.id}</td>
                  <td className="px-6 py-4">
                    <div className="flex flex-col">

                      <button
                        onClick={() => { setNewSelectedItem(value.orderItems) }}
                      >
                        <span
                          className="font-medium text-foreground group-hover:text-primary transition-colors">{value.name}</span>
                      </button>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <DropdownMenu>
                      <DropdownMenuTrigger asChild>
                        <Button
                          variant="outline"
                          size="sm"
                          className={` justify-between border-transparent bg-opacity-10 hover:bg-opacity-20 transition-colors text-white
                                ${value.status === "Inprogress" ? 'bg-yellow-500 ' : ''}
                                ${value.status === "Accepted" ? 'bg-blue-500 ' : ''}
                                ${value.status === "In away" ? 'bg-purple-500 ' : ''}
                                ${value.status === "Completed" || value.status == "Received" ? 'bg-green-500 ' : ''}
                                ${value.status === "Rejected" ? 'bg-red-500' : ''}
                            `}
                        >
                          {value.status}
                        </Button>
                      </DropdownMenuTrigger>
                      <DropdownMenuContent align="start" className="w-[140px]">
                        {orderStatus.map((statusItem, sIndex) => (
                          <DropdownMenuItem
                            key={sIndex}
                            onClick={() => {
                              console.log(`Update  ${sIndex}`);

                              chageOrderStatus.mutate({
                                id: value.id,
                                statsu: sIndex,
                              })
                            }}
                          >
                            {statusItem}
                          </DropdownMenuItem>
                        ))}
                      </DropdownMenuContent>
                    </DropdownMenu>
                  </td>
                  <td className="px-6 py-4 font-medium text-right">
                    ${value.totalPrice.toFixed(2)}
                  </td>
                  <td className="px-6 py-4 text-right">
                    <button className="text-muted-foreground hover:text-primary transition-colors">
                      <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-eye"><path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" /><circle cx="12" cy="12" r="3" /></svg>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      <div className="flex justify-center mt-6">
        <div className="flex gap-2">
          {Array.from({ length: pageNumb }, (_, i) => (
            <button
              key={i}
              onClick={() => getOrdersAt(i + 1)}
              className={`
                  h-9 w-9 flex items-center justify-center rounded-lg text-sm font-medium transition-all duration-200
                  ${currentPage === i + 1
                  ? 'bg-primary text-primary-foreground shadow-md scale-105'
                  : 'bg-card border border-border hover:bg-accent hover:text-accent-foreground'
                }
              `}
            >
              {i + 1}
            </button>
          ))}
        </div>
      </div>


    </div>
  );
};
export default Order;
