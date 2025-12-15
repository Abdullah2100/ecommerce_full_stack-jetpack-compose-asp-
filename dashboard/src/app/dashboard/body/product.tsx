import { useEffect, useState } from "react";
import { ToastContainer } from "react-toastify";
import Image from "next/image";
import { getProductPages, getProductAtPage } from "@/lib/api/product";
import { useQueryClient, useQuery, useMutation } from "@tanstack/react-query";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";
import iProductResponseDto from "@/dto/response/iProductResponseDto";
import { getCurrencies } from "@/lib/api/currency";
import { ICurrency } from "@/model/ICurrency";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { updateCurrency } from "@/util/globle";

const Product = () => {
    const queryClient = useQueryClient()
    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getProductPages()

    })

    const [currnetPage, setCurrentPage] = useState(1);
    const [currentCurrency, setCurrentCurrency] = useState<ICurrency | undefined>(undefined);
    const [selectedProduct, setSelectedProduct] = useState<iProductResponseDto | null>(null);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    const { data, } = useQuery({
        queryKey: ['products', currnetPage],
        queryFn: () => getProductAtPage(currnetPage)

    })

    const { data: currencies, isError } = useQuery(
        {
            queryKey: ['currency'],
            queryFn: () => getCurrencies()
        }
    )

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['products', currnetPage],
            queryFn: () => getProductAtPage(currnetPage),
        })
    }, [currnetPage])

    // Keyboard navigation for dialog
    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (!selectedProduct) return;

            if (e.key === 'Escape') {
                closeImageDialog();
            } else if (e.key === 'ArrowLeft') {
                prevImage();
            } else if (e.key === 'ArrowRight') {
                nextImage();
            }
        };

        window.addEventListener('keydown', handleKeyDown);
        return () => window.removeEventListener('keydown', handleKeyDown);
    }, [selectedProduct, currentImageIndex]);

    const openImageDialog = (product: iProductResponseDto) => {
        setSelectedProduct(product);
        setCurrentImageIndex(0);
    };

    const closeImageDialog = () => {
        setSelectedProduct(null);
        setCurrentImageIndex(0);
    };

    const getProductImages = (product: iProductResponseDto) => {
        return product.productImages;
    };

    const nextImage = () => {
        if (selectedProduct) {
            const images = getProductImages(selectedProduct);
            setCurrentImageIndex((prev) => (prev + 1) % images.length);
        }
    };

    const prevImage = () => {
        if (selectedProduct) {
            const images = getProductImages(selectedProduct);
            setCurrentImageIndex((prev) => (prev - 1 + images.length) % images.length);
        }
    };

    if (data === undefined) return;




    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">
                    Products
                </h1>
                <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                        <Button
                            variant="outline"
                            size="sm"
                            className={` justify-between border-transparent bg-opacity-10 hover:bg-opacity-20 transition-colors text-black`}
                        >
                            {currentCurrency?.name ?? "Select Currency"}
                        </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                        align="start"
                        className="w-[140px]">
                        {currencies?.map((statusItem, sIndex) => (
                            <DropdownMenuItem
                                key={sIndex}
                                onClick={() => {
                                    setCurrentCurrency(statusItem)
                                }}
                            >
                                {statusItem.name}
                            </DropdownMenuItem>
                        ))}
                    </DropdownMenuContent>
                </DropdownMenu>

            </div>

            <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
                            <tr>
                                <th className="px-6 py-4">#</th>
                                <th className="px-6 py-4">Product</th>
                                <th className="px-6 py-4">Price</th>
                                <th className="px-6 py-4">Store</th>
                                <th className="px-6 py-4">Category</th>
                                <th className="px-6 py-4">Variants</th>
                                <th className="px-6 py-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-border/50">
                            {data !== undefined && data?.length > 0 && data.map((value, index) => (
                                <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-4">
                                            <div className="relative h-12 w-12 rounded-lg overflow-hidden border border-border/50 shadow-sm   transition-transform">
                                                <Image
                                                    src={convertImageToValidUrl(value.thumbnail)}
                                                    alt={value.name}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <span
                                                onClick={() => openImageDialog(value)}
                                                className="font-medium text-foreground group-hover:text-primary transition-colors cursor-pointer hover:underline"
                                            >
                                                {value.name}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-medium text-foreground">
                                        {currentCurrency?.symbol ?? value.symbol}{updateCurrency(value.price, currentCurrency?.symbol ?? "", currentCurrency, currencies ?? [])}
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-2">
                                            <div className="h-6 w-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-xs font-bold">
                                                {value.storeName.charAt(0)}
                                            </div>
                                            <span className="text-muted-foreground">{value.storeName}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary text-secondary-foreground">
                                            {value.subcategory}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-col gap-2">
                                            {value.productVariants !== undefined && value.productVariants.length > 0 ? (
                                                value.productVariants
                                                    .filter(group => group && group.length > 0) // Only show groups with items
                                                    .map((group, gIndex) => (
                                                        <div key={gIndex} className="flex items-start gap-2">
                                                            {/* Display the variant type name prominently */}
                                                            <span className="text-xs font-bold text-foreground bg-secondary/50 px-2 py-1 rounded-md min-w-[60px] text-center">
                                                                {group[0].variantName}
                                                            </span>
                                                            {/* Display all values in this variant group */}
                                                            <div className="flex flex-wrap gap-1.5 items-center">
                                                                {group.map((variant, vIndex) => (
                                                                    variant.variantName === "Color" ?
                                                                        <div
                                                                            key={`${gIndex}-${vIndex}`}
                                                                            style={{ backgroundColor: variant.name }}
                                                                            className="w-6 h-6 rounded-md border-1 border-border/50 shadow-sm"
                                                                            title={variant.name}
                                                                        />
                                                                        :
                                                                        <span
                                                                            key={`${gIndex}-${vIndex}`}
                                                                            className="inline-flex items-center px-2.5 py-1 rounded-md text-xs font-medium bg-accent text-accent-foreground border border-border/50"
                                                                        >
                                                                            {variant.name}
                                                                        </span>
                                                                ))}
                                                            </div>
                                                        </div>
                                                    ))
                                            ) : (
                                                <span className="text-muted-foreground text-xs italic">No variants</span>
                                            )}
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        <button className="text-muted-foreground hover:text-primary transition-colors">
                                            <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="lucide lucide-more-horizontal"><circle cx="12" cy="12" r="1" /><circle cx="19" cy="12" r="1" /><circle cx="5" cy="12" r="1" /></svg>
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* pagination number */}
            <div className="flex justify-center mt-6">
                <div className="flex gap-2">
                    {Array.from({ length: Number(userPages) || 0 }, (_, i) => (
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

            {/* Image Dialog Modal */}
            {selectedProduct && (
                <div
                    className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm animate-in fade-in duration-300"
                    onClick={closeImageDialog}
                >
                    <div
                        className="relative w-full max-w-4xl mx-4 bg-card rounded-2xl shadow-2xl border border-border/50 overflow-hidden animate-in zoom-in-95 duration-300"
                        onClick={(e) => e.stopPropagation()}
                    >

                        <div className="flex items-center justify-between p-6 border-b border-border/50 bg-muted/30">
                            <button
                                onClick={closeImageDialog}
                                className="h-10 w-10 flex items-center justify-center rounded-lg hover:bg-muted transition-colors"
                            >
                                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <line x1="18" y1="6" x2="6" y2="18"></line>
                                    <line x1="6" y1="6" x2="18" y2="18"></line>
                                </svg>
                            </button>
                        </div>

                        <div className="relative bg-muted/20 p-8">
                            <div className="relative w-full aspect-square max-h-[500px] rounded-xl overflow-hidden bg-background border border-border/50 shadow-lg">
                                <Image
                                    src={convertImageToValidUrl(getProductImages(selectedProduct)[currentImageIndex])}
                                    alt={`${selectedProduct.name} - Image ${currentImageIndex + 1}`}
                                    fill
                                    className="object-contain"
                                    priority
                                />
                            </div>

                            {/* Navigation Arrows - Only show if multiple images */}
                            {getProductImages(selectedProduct).length > 1 && (
                                <>
                                    <button
                                        onClick={prevImage}
                                        className="absolute left-4 top-1/2 -translate-y-1/2 h-12 w-12 flex items-center justify-center rounded-full bg-background/90 border border-border/50 shadow-lg hover:bg-background hover:scale-110 transition-all duration-200"
                                    >
                                        <svg
                                            style={{ color: currentImageIndex > 0 ? 'black' : 'gray' }}
                                            xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <polyline points="15 18 9 12 15 6"></polyline>
                                        </svg>
                                    </button>
                                    <button
                                        onClick={nextImage}
                                        className="absolute right-4 top-1/2 -translate-y-1/2 h-12 w-12 flex items-center justify-center rounded-full bg-background/90 border border-border/50 shadow-lg hover:bg-background hover:scale-110 transition-all duration-200"
                                    >
                                        <svg
                                            style={{ color: currentImageIndex + 1 < selectedProduct.productImages.length ? 'black' : 'gray' }}
                                            xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                            <polyline points="9 18 15 12 9 6"></polyline>
                                        </svg>
                                    </button>
                                </>
                            )}
                        </div>


                    </div>
                </div>
            )}

            <ToastContainer />
        </div>
    );
};
export default Product;