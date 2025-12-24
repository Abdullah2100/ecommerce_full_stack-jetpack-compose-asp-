
import { useEffect, useState } from "react";
import Image from "next/image";
import { createProduct, getProductPages, getProductAtPage, updateProduct, deleteProduct } from "@/lib/api/product";
import { useQueryClient, useQuery, useMutation } from "@tanstack/react-query";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";
import iProductResponseDto from "@/dto/response/iProductResponseDto";
import { getCurrencies } from "@/lib/api/currency";
import { ICurrency } from "@/model/ICurrency";
import { DropdownMenu, DropdownMenuContent, DropdownMenuItem, DropdownMenuTrigger } from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { updateCurrency } from "@/util/globle";
import { Dialog } from "@/components/ui/dialog";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { createProductSchema, updateProductSchema } from "@/zod/productSchema";
import { InputWithLabelAndError } from "@/components/ui/input/InputWithLabelAndError";
import { InputImageWithLabelAndError } from "@/components/ui/input/inputImageWithLableAndError";
import { getStoresByName } from "@/lib/api/store";
import iStore from "@/model/iStore";
import { getSubCategoriesByStoreId } from "@/lib/api/subCategory";
import iSubCategory from "@/model/iSubCategory";
import { getVarient } from "@/lib/api/variant";
import iVariant from "@/model/iVariant";
import { toast } from "react-toastify";
import { Pencil, Trash2 } from "lucide-react";
import { Input } from "@/components/ui/input/input";
import useDebounce from "@/hooks/useDebounce";

type SelectedVariant = {
    variantId: string;
    variantName: string;
    value: string;
};

const Product = () => {
    const queryClient = useQueryClient();
    const [currnetPage, setCurrentPage] = useState(1);
    const [currentCurrency, setCurrentCurrency] = useState<ICurrency | undefined>(undefined);
    const [selectedProduct, setSelectedProduct] = useState<iProductResponseDto | undefined>(undefined);
    const [currentImageIndex, setCurrentImageIndex] = useState(0);

    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [editingProduct, setEditingProduct] = useState<iProductResponseDto | undefined>(undefined);

    const [storeSearchTerm, setStoreSearchTerm] = useState("");
    const debouncedStoreSearchTerm = useDebounce(storeSearchTerm, 300);
    const [showStoreResults, setShowStoreResults] = useState(false);
    const [selectedStore, setSelectedStore] = useState<iStore | undefined>(undefined);
    const [selectedSubCategory, setSelectedSubCategory] = useState<iSubCategory | undefined>(undefined);
    const [selectedVariants, setSelectedVariants] = useState<SelectedVariant[]>([]);
    const [currentVariant, setCurrentVariant] = useState<iVariant | undefined>(undefined);
    const [variantValue, setVariantValue] = useState("");


    const { data: stores } = useQuery({
        queryKey: ['stores', debouncedStoreSearchTerm],
        queryFn: () => getStoresByName(debouncedStoreSearchTerm),
        enabled: !!debouncedStoreSearchTerm,
    });

    const { data: subCategories } = useQuery({
        queryKey: ['subCategories', selectedStore?.id],
        queryFn: () => getSubCategoriesByStoreId(selectedStore!.id, 1),
        enabled: !!selectedStore,
    });

    const { data: variants } = useQuery({
        queryKey: ['variants'],
        queryFn: () => getVarient(1),
    });

    const { register, handleSubmit, setValue, reset, formState: { errors } } = useForm({
        resolver: zodResolver(editingProduct ? updateProductSchema : createProductSchema)
    });

    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getProductPages()
    });

    const { data, refetch } = useQuery({
        queryKey: ['products', currnetPage],
        queryFn: () => getProductAtPage(currnetPage)
    });

    const { data: currencies } = useQuery({
        queryKey: ['currency'],
        queryFn: () => getCurrencies()
    });

    const createProductMutation = useMutation({
        mutationFn: (data: FormData) => createProduct(data),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("Product created successfully");
            setIsDialogOpen(false);
        }
    });

    const updateProductMutation = useMutation({
        mutationFn: (data: FormData) => updateProduct(data),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("Product updated successfully");
            setIsDialogOpen(false);
        }
    });

    const deleteProductMutation = useMutation({
        mutationFn: (productId: string) => deleteProduct(editingProduct!.storeId, productId),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("Product deleted successfully");
            setIsDialogOpen(false);
        }
    });


    const handleFormSubmit = (data: any) => {
        const formData = new FormData();
        if (editingProduct) {
            formData.append('Id', editingProduct.id);
            formData.append('StoreId', editingProduct.storeId)
        } else {
            formData.append('StoreId', selectedStore!.id);
        }
        formData.append('Name', data.name);
        formData.append('Description', data.description);
        formData.append('Price', data.price.toString());
        formData.append('Symbol', data.symbol);
        formData.append('SubcategoryId', selectedSubCategory!.id);

        if (data.thumbnail?.[0]) {
            formData.append('Thumbnail', data.thumbnail[0]);
        }

        if (data.images) {
            for (let i = 0; i < data.images.length; i++) {
                formData.append('Images', data.images[i]);
            }
        }

        selectedVariants.forEach((variant, index) => {
            formData.append(`ProductVariants[${index}].VariantId`, variant.variantId);
            formData.append(`ProductVariants[${index}].Value`, variant.value);
        });


        if (editingProduct) {
            updateProductMutation.mutate(formData);
        } else {
            createProductMutation.mutate(formData);
        }
    }


    useEffect(() => {
        if (editingProduct) {
            setValue("name", editingProduct.name);
            setValue("description", editingProduct.description);
            setValue("price", editingProduct.price);
            setValue("symbol", editingProduct.symbol);

            if (editingProduct.storeId && editingProduct.storeName) {
                const storeForEdit = { id: editingProduct.storeId, name: editingProduct.storeName } as iStore;
                setSelectedStore(storeForEdit);
                setStoreSearchTerm(editingProduct.storeName);
            }
            // @ts-ignore
            if (editingProduct.subcategoryId && editingProduct.subCategoryName) {
                // @ts-ignore
                const subCategoryForEdit = { id: editingProduct.subcategoryId, name: editingProduct.subCategoryName } as iSubCategory;
                setSelectedSubCategory(subCategoryForEdit);
            }
            if (editingProduct.productVariants) {
                const variantsForEdit = editingProduct.productVariants.map(pv => ({ variantId: pv.variantId, variantName: pv.variantName, value: pv.value }));
                setSelectedVariants(variantsForEdit);
            }

        }
    }, [editingProduct, setValue]);


    useEffect(() => {
        if (!isDialogOpen) {
            reset();
            setEditingProduct(undefined);
            setSelectedStore(undefined);
            setSelectedSubCategory(undefined);
            setStoreSearchTerm("");
            setSelectedVariants([]);
            setCurrentVariant(undefined);
            setVariantValue("");
        }
    }, [isDialogOpen, reset]);


    const openImageDialog = (product: iProductResponseDto) => {
        setSelectedProduct(product);
        setCurrentImageIndex(0);
    };

    const closeImageDialog = () => {
        setSelectedProduct(undefined);
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

    const addVariant = () => {
        if (currentVariant && variantValue && !selectedVariants.some(v => v.variantId === currentVariant.id)) {
            setSelectedVariants([...selectedVariants, { variantId: currentVariant.id??'', variantName: currentVariant.name, value: variantValue }]);
            setCurrentVariant(undefined);
            setVariantValue("");
        }
    };

    const removeVariant = (variantId: string) => {
        setSelectedVariants(selectedVariants.filter(v => v.variantId !== variantId));
    };


    if (data === undefined) return;


    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-purple-600 bg-clip-text text-transparent">
                    Products
                </h1>
                <div className="flex gap-4">
                    <DropdownMenu>
                        <DropdownMenuTrigger asChild>
                            <Button
                                variant="outline"
                                size="sm"
                                className={` justify-between border-transparent bg-opacity-10 hover:bg-opacity-20 transition-colors text-black`}>
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
                                    }}>
                                    {statusItem.name}
                                </DropdownMenuItem>
                            ))}
                        </DropdownMenuContent>
                    </DropdownMenu>

                    <Dialog
                        open={isDialogOpen}
                        onOpenChange={setIsDialogOpen}
                        trigger={
                            <Button
                                onClick={() => { setEditingProduct(undefined); setIsDialogOpen(true); }}
                                size="sm" className="bg-primary text-white w-[120px]">
                                Create Product
                            </Button>
                        }
                        title={editingProduct ? "Update Product" : "Create New Product"}
                        footer={
                            <Button
                                onClick={handleSubmit(handleFormSubmit)}
                                type="submit" form="create-product-form"
                                disabled={createProductMutation.isPending || updateProductMutation.isPending || !selectedSubCategory}>
                                {editingProduct ? "Update" : "Create"}
                            </Button>
                        }
                    >
                        <form id="create-product-form" className="space-y-4 overflow-y-auto ">
                            <fieldset disabled={!!editingProduct}>
                                <div>
                                    <label>Store</label>
                                    <Input
                                        type="text"
                                        value={storeSearchTerm}
                                        onChange={(e) => {
                                            setStoreSearchTerm(e.target.value)
                                            setSelectedStore(undefined)
                                            setSelectedSubCategory(undefined)
                                            setShowStoreResults(true)
                                        }}
                                        placeholder="Search for a store"
                                    />
                                    {showStoreResults && stores && (
                                        <ul className="border rounded mt-1 max-h-40 overflow-y-auto">
                                            {stores.map((store: iStore) => (
                                                <li key={store.id} onClick={() => {
                                                    setSelectedStore(store);
                                                    setStoreSearchTerm(store.name);
                                                    setShowStoreResults(false);
                                                }} className="p-2 hover:bg-gray-200 cursor-pointer">
                                                    {store.name}
                                                </li>
                                            ))}
                                        </ul>
                                    )}
                                </div>

                                <div>
                                    <label>Subcategory</label>
                                    <select
                                        disabled={!selectedStore}
                                        value={selectedSubCategory?.id ?? ""}
                                        onChange={(e) => {
                                            const subCategory = subCategories?.find(sc => sc.id === e.target.value);
                                            setSelectedSubCategory(subCategory);
                                        }}
                                        className="w-full p-2 border rounded"
                                    >
                                        <option value="">Select a subcategory</option>
                                        {subCategories?.map(sc => (
                                            <option key={sc.id} value={sc.id}>{sc.name}</option>
                                        ))}
                                    </select>
                                </div>
                            </fieldset>

                            <fieldset disabled={!selectedSubCategory}>
                                <InputWithLabelAndError
                                    label="Name"
                                    type="txt"

                                    {...register("name")}
                                    error={errors.name?.message}
                                    {...register("name")}
                                />

                                <InputWithLabelAndError
                                    label="Description"
                                    type="txt"
                                    {...register("description")}
                                    error={errors.description?.message}
                                    {...register("description")}
                                />
                                <div className="flex gap-2">
                                    <InputWithLabelAndError
                                        label="Price"

                                        type="number"
                                        error={errors.price?.message}
                                        {...register("price")}
                                    />

                                    <InputWithLabelAndError
                                        label="Symbol"
                                        type="txt"

                                        {...register("symbol")}
                                        error={errors.symbol?.message}
                                        {...register("symbol")}
                                    />
                                </div>

                                <InputImageWithLabelAndError
                                    key={editingProduct ? `edit-thumb-${editingProduct.id}` : 'create-thumb'}
                                    initialPreviews={editingProduct?.thumbnail ? [convertImageToValidUrl(editingProduct.thumbnail)] : []}
                                    label="Thumbnail"
                                    error={errors.thumbnail?.message?.toString()}
                                      height={300}
                                    onChange={

                                        (files: File[]) => {
                                            if (files?.length > 0) {

                                                register("thumbnail")
                                                setValue("thumbnail", files[0])
                                            }


                                        }
                                    }
                                />

                                <InputImageWithLabelAndError
                                    key={editingProduct ? `edit-imgs-${editingProduct.id}` : 'create-imgs'}
                                    initialPreviews={editingProduct ? editingProduct.productImages.map(i => convertImageToValidUrl(i.imageUrl)) : []}
                                    label="Images"
                                    error={errors.images?.message?.toString()}
                                    isSingle={false}
                                    height={300}
                                    onChange={

                                        (files: File[]) => {
                                            if (files?.length > 0) {

                                                register("images")
                                                setValue("images", files)
                                            }


                                        }
                                    }
                                />
                            </fieldset>


                            <fieldset disabled={!selectedSubCategory}>
                                <label>Variants</label>
                                <div className="flex gap-2 items-center">
                                    <select
                                        value={currentVariant?.id ?? ""}
                                        onChange={(e) => {
                                            const variant = variants?.find(v => v.id === e.target.value);
                                            if(variant!==undefined)
                                            setCurrentVariant({id:variant?.id,name:variant.name});
                                        }}
                                        className="w-full p-2 border rounded"
                                    >
                                        <option value="">Select a variant</option>
                                        {variants&&variants?.map(v => (
                                            <option key={v.id} value={v.id}>{v.name}</option>
                                        ))}
                                    </select>
                                    <Input
                                        type="text"
                                        placeholder="Value"
                                        value={variantValue}
                                        onChange={(e) => setVariantValue(e.target.value)}
                                    />
                                    <Button type="button" onClick={addVariant}>Add</Button>
                                </div>
                                <ul className="mt-2 space-y-2">
                                    {selectedVariants.map(v => (
                                        <li key={v.variantId} className="flex justify-between items-center p-2 border rounded">
                                            <span>{v.variantName}: {v.value}</span>
                                            <Button type="button" variant="destructive" size="sm" onClick={() => removeVariant(v.variantId)}>Remove</Button>
                                        </li>
                                    ))}
                                </ul>
                            </fieldset>
                        </form>
                    </Dialog>
                </div>
            </div>

            <div className="border rounded-lg overflow-hidden w-full">
                <div className="relative w-full overflow-auto">
                    <table className="w-full caption-bottom text-sm">
                        <thead className="[&_tr]:border-b">
                            <tr className="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground w-[100px]">Image</th>
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Name</th>
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Store</th>
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Price</th>
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="[&_tr:last-child]:border-0">
                            {data?.map((product, pIndex) => (
                                <tr key={pIndex} className="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted">
                                    <td className="p-4 align-middle">
                                        <Image
                                            onClick={() => openImageDialog(product)}
                                            alt={product.name}
                                            className="aspect-square rounded-md object-cover cursor-pointer"
                                            height="64"
                                            src={convertImageToValidUrl(product.thumbnail)}
                                            width="64" />
                                    </td>
                                    <td className="p-4 align-middle font-medium">{product.name}</td>
                                    <td className="p-4 align-middle">{product.storeName}</td>
                                    <td className="p-4 align-middle">{`${product.price} ${currentCurrency?.symbol}`}</td>
                                    <td className="p-4 align-middle">
                                        <div className="flex gap-2">
                                            <Button variant="outline" size="icon" onClick={() => { setEditingProduct(product); setIsDialogOpen(true); }}>
                                                <Pencil className="h-4 w-4" />
                                            </Button>
                                            <Button variant="destructive" size="icon" onClick={() => {
                                                setEditingProduct(product);
                                                deleteProductMutation.mutate(product.id)
                                            }}>
                                                <Trash2 className="h-4 w-4" />
                                            </Button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            {selectedProduct && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50" onClick={closeImageDialog}>
                    <div className="relative" onClick={(e) => e.stopPropagation()}>
                        <Image
                            alt={selectedProduct.name}
                            className="max-w-screen-xl max-h-screen-xl object-contain"
                            height={window.innerHeight * 0.8}
                            src={ selectedProduct.thumbnail}
                            width={window.innerWidth * 0.8}
                        />
                        <Button
                            variant="outline"
                            size="icon"
                            className="absolute top-1/2 left-4 -translate-y-1/2"
                            onClick={prevImage}
                        >
                            {"<"}
                        </Button>
                        <Button
                            variant="outline"
                            size="icon"
                            className="absolute top-1/2 right-4 -translate-y-1/2"
                            onClick={nextImage}
                        >
                            {">"}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default Product;
