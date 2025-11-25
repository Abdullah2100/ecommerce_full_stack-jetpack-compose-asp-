import { mockProducts } from "@/lib/mockData";
import { useState } from "react";
import { ToastContainer } from "react-toastify";
import Image from "next/image";

const Product = () => {
    const [currentPage, setCurrentPage] = useState(1);

    // Use mock data directly
    const data = mockProducts;
    const userPages = 1; // Mock pages count

    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">
                    Products
                </h1>
                <button className="bg-primary text-primary-foreground hover:bg-primary/90 px-4 py-2 rounded-lg text-sm font-medium transition-colors shadow-lg shadow-primary/25">
                    Add Product
                </button>
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
                            {data.map((value, index) => (
                                <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-4">
                                            <div className="relative h-12 w-12 rounded-lg overflow-hidden border border-border/50 shadow-sm   transition-transform">
                                                <Image
                                                    src={value.thmbnail}
                                                    alt={value.name}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <span className="font-medium text-foreground group-hover:text-primary transition-colors">
                                                {value.name}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4 font-medium text-foreground">
                                        ${value.price.toFixed(2)}
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-2">
                                            <div className="h-6 w-6 rounded-full bg-blue-100 text-blue-600 flex items-center justify-center text-xs font-bold">
                                                {value.store.charAt(0)}
                                            </div>
                                            <span className="text-muted-foreground">{value.store}</span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-secondary text-secondary-foreground">
                                            {value.subcategory}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-wrap gap-1">
                                            {value.productVarients.length > 0 ? (
                                                value.productVarients.map((group, gIndex) => (
                                                    group.map((variant, vIndex) => (
                                                        <span key={`${gIndex}-${vIndex}`} className="inline-flex items-center px-2 py-0.5 rounded text-xs bg-muted text-muted-foreground border border-border/50">
                                                            {variant.name}
                                                        </span>
                                                    ))
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

            <div className="flex justify-center mt-6">
                <div className="flex gap-2">
                    {Array.from({ length: userPages }, (_, i) => (
                        <button
                            key={i}
                            onClick={() => setCurrentPage(i + 1)}
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
            <ToastContainer />
        </div>
    );
};
export default Product;