import { mockStores } from "@/lib/mockData";
import { useEffect, useState } from "react";
import { toast, ToastContainer } from "react-toastify";
import { Button } from "@/components/ui/button";
import Image from "next/image";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { changeStoreStatus, getStoreAtPage, getStorePages } from "@/lib/api/store";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";

const Stores = () => {

    const queryClient = useQueryClient()
    const { data: storePages } = useQuery({
        queryKey: ['storePages'],
        queryFn: () => getStorePages()

    })

    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['stores', currnetPage],
        queryFn: () => getStoreAtPage(currnetPage)

    })

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['stores', currnetPage],
            queryFn: () => getStoreAtPage(currnetPage),
        })
    }, [currnetPage])


    const changeStoreStatusFun = useMutation(
        {
            mutationFn: (store_id: string) => changeStoreStatus(store_id),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم التعديل بنجاح")


            }
        }
    )

    if (data == null) return;
    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-pink-600 bg-clip-text text-transparent">
                    Stores
                </h1>
            </div>

            <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
                            <tr>
                                <th className="px-6 py-4">#</th>
                                <th className="px-6 py-4">Store</th>
                                <th className="px-6 py-4">Owner</th>
                                <th className="px-6 py-4">Created At</th>
                                <th className="px-6 py-4 text-center">Status</th>
                                <th className="px-6 py-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-border/50">
                            {data.map((value, index) => (
                                <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-4">
                                            <div className="relative h-10 w-10 rounded-lg overflow-hidden border border-border/50 shadow-sm">
                                                <Image
                                                    src={convertImageToValidUrl(value.smallImage)}
                                                    alt={value.name}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <span className="font-medium text-foreground group-hover:text-primary transition-colors">{value.name}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-2">
                                            <div className="h-6 w-6 rounded-full bg-secondary flex items-center justify-center text-xs font-bold text-secondary-foreground">
                                                {value.userName.charAt(0)}
                                            </div>
                                            <span className="text-muted-foreground">{value.userName}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">
                                        {new Date(value.created_at).toLocaleDateString()}
                                    </td>
                                    <td className="px-6 py-4 text-center">
                                        <span className={`
                                            inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                                            ${!value.isBlocked
                                                ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                                                : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'}
                                        `}>
                                            {!value.isBlocked ? 'Active' : 'Blocked'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <Button

                                            variant={!value.isBlocked ? "destructive" : "default"}
                                            size="sm"
                                            className="h-7 text-xs"
                                            onClick={() => {
                                                // Mock toggle logic
                                                changeStoreStatusFun.mutate(value.id)
                                            }}
                                        >
                                            {!value.isBlocked ? 'Block' : 'Unblock'}
                                        </Button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="flex justify-center mt-6">
                <div className="flex gap-2">
                    {Array.from({ length: storePages ?? 1 }, (_, i) => (
                        <button
                            key={i}
                            onClick={() => setCurrentPage(i + 1)}
                            className={`
                                h-9 w-9 flex items-center justify-center rounded-lg text-sm font-medium transition-all duration-200
                                ${currnetPage === i + 1
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
            <ToastContainer />
        </div>
    );
};
export default Stores;