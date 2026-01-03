
import { useEffect, useState } from "react";
import Image from "next/image";
import {
    createProduct,
    getProductPages,
    getProductAtPage,
    updateProduct,
    deleteProduct
} from "@/lib/api/product";
import { useQueryClient, useQuery, useMutation } from "@tanstack/react-query";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";
import iProductResponseDto from "@/dto/response/iProductResponseDto";
import { getCurrencies } from "@/lib/api/currency";
import { ICurrency } from "@/model/ICurrency";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";
import { Dialog } from "@/components/ui/dialog";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import {
    createProductSchema,
    updateProductSchema
} from "@/zod/productSchema";
import { InputWithLabelAndError } from "@/components/ui/input/InputWithLabelAndError";
import { InputImageWithLabelAndError } from "@/components/ui/input/inputImageWithLableAndError";
import { getStoresByName } from "@/lib/api/store";
import IStore from "@/model/IStore";
import { getSubCategoriesByStoreId } from "@/lib/api/subCategory";
import ISubCategory from "@/model/ISubCategory";
import { getVarient } from "@/lib/api/variant";
import IVariant from "@/model/IVariant";
import { toast } from "react-toastify";
import { Pencil, Trash2 } from "lucide-react";
import { Input } from "@/components/ui/input/input";
import useDebounce from "@/hooks/useDebounce";
import { SelectLableAndError } from "@/components/ui/input/selectLableAndError";
import { IProductVariant } from "@/model/IProductVariant";
import { productVariant } from "@/zod/productVariantSchema";



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
    const [selectedStore, setSelectedStore] = useState<IStore | undefined>(undefined);
    const [selectedSubCategory, setSelectedSubCategory] = useState<ISubCategory | undefined>(undefined);

    const [selectedProductVariants, setSelectedProductVariant] = useState<IProductVariant[] | undefined>([]);
    const [deleteProductVariants, setDeleteProductVariant] = useState<IProductVariant[] | undefined>([]);
    const [currentVariant, setCurrentVariant] = useState<IVariant | undefined>(undefined);


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

    const {
        register: productVariantRegeister,
        handleSubmit: productVariantHandle,
        reset: resetProductVariant,
        formState: { errors: productVariantErrors } } = useForm({
            resolver: zodResolver(productVariant)
        });

    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getProductPages()
    });

    const { data: products, refetch } = useQuery({
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
            setDeleteProductVariant(undefined)
        }
    });

    const deleteProductMutation = useMutation({
        mutationFn: ({ productId, storeId }: { productId: string, storeId: string }) => deleteProduct(storeId, productId),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("Product deleted successfully");
            setIsDialogOpen(false);
            setDeleteProductVariant(undefined)
        }
    });


    const handleFormSubmit = (data: any) => {
        // Step 1: Build a plain object with only non-file fields
        const productObj: any = {};
        if (editingProduct) {
            productObj.Id = editingProduct.id;
            productObj.StoreId = editingProduct.storeId;
        } else {
            // productObj.StoreId = selectedStore?.id;
        }
        productObj.name = data.name;
        productObj.description = data.description;
        productObj.price = data.price;
        productObj.symbol = data.symbol;
        productObj.subcategoryId = selectedSubCategory?.id;

        // Step 2: Create FormData and append non-file fields
        let formData = new FormData();
        Object.keys(productObj).forEach(key => {
            if (productObj[key] !== undefined && productObj[key] !== null) {
                formData.append(key, productObj[key]);
            }
        });

        // Step 3: Handle files (do NOT add to productObj)
        if (data.thumbnail && data.thumbnail instanceof File) {
            formData.append('Thumbnail', data.thumbnail);
        } else if (data.thumbnail?.[0] && data.thumbnail[0] instanceof File) {
            formData.append('Thumbnail', data.thumbnail[0]);
        }

        if (data?.images && Array.isArray(data.images) && data.images.length > 0) {
            for (let i = 0; i < data.images.length; i++) {
                if (data.images[i] instanceof File) {
                    formData.append('Images', data.images[i]);
                }
            }
        }

        // Step 4: Handle variants
        if (selectedProductVariants?.length !== 0)
            selectedProductVariants?.forEach((variant, productVariantIndex) => {
                const index = products?.findIndex(p => p.productVariants.findIndex(x => x.findIndex(d => d.name === variant.name)))
                if (index === -1) {
                    formData.append(`ProductVariants[${productVariantIndex}].VariantId`, variant.variantId!!.toString());
                    formData.append(`ProductVariants[${productVariantIndex}].Name`, variant.name!!.toString());
                    formData.append(`ProductVariants[${productVariantIndex}].Percentage`, variant.percentage!!.toString());
                }
                // else{
                //     toast.error(`Please select variant: ${variants?.find(v => v.id === variant.variantId)?.name}`);
                //   }
                //     formData.append(`ProductVariants[${index}].VariantId`, variant.variantId!!.toString());
                //     formData.append(`ProductVariants[${index}].Name`, variant.name!!.toString());
                //     formData.append(`ProductVariants[${index}].Percentage`, variant.percentage!!.toString());

            });

        if (deleteProductVariants?.length !== 0) {
            deleteProductVariants?.forEach((variant, index) => {
                formData.append(`DeletedProductVariants[${index}].VariantId`, variant.variantId!!.toString());
                formData.append(`DeletedProductVariants[${index}].Name`, variant.name!!.toString());
                formData.append(`DeletedProductVariants[${index}].Percentage`, variant.percentage!!.toString());
                if (variant.id !== undefined) {
                    formData.append(`DeletedProductVariants[${index}].Id`, variant.id!!.toString());
                }

            });
        }


        if (editingProduct) {
            updateProductMutation.mutate(formData);
        } else {
            createProductMutation.mutate(formData);
        }
    }


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



    const addOrUpdateProductVariant = (name: string, precentage: number) => {
        if (currentVariant) {
            const index = selectedProductVariants?.findIndex(v => v.name === name && v.variantId === currentVariant.id);

            if (index !== -1) {
                return
            }

            if (selectedProductVariants === undefined) {
                setSelectedProductVariant([{
                    variantId: currentVariant.id ?? '',
                    name: name,
                    percentage: precentage,
                    id: undefined
                }]);
            }
            else
                setSelectedProductVariant([...selectedProductVariants, {
                    variantId: currentVariant.id ?? '',
                    name: name,
                    percentage: precentage,
                    id: undefined
                }]);
            console
            resetProductVariant({ name: "", percentage: 0 });
        }

    };



    const removeProductVariant = (variant: IProductVariant) => {

        if (variant.id !== undefined)
            setDeleteProductVariant([...deleteProductVariants ?? [], variant]);
        setSelectedProductVariant(selectedProductVariants?.filter(v => v !== variant));
        console.log('this is deleting variant', deleteProductVariants);
    };


    useEffect(() => {
        if (editingProduct) {
            setValue("name", editingProduct.name);
            setValue("description", editingProduct.description);
            setValue("price", editingProduct.price);
            setValue("symbol", editingProduct.symbol);
            setStoreSearchTerm(editingProduct.storeName)
            setSelectedStore({ id: editingProduct.storeId, name: editingProduct.storeName } as IStore)
            setSelectedSubCategory({ id: editingProduct.subcategoryId, name: editingProduct.subCategoryName } as ISubCategory)

            if (editingProduct.productVariants) {
                setSelectedProductVariant(editingProduct.productVariants.flat());
                console.log('this the product variant data', editingProduct.productVariants.flat());
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
            setSelectedProductVariant([]);
            setCurrentVariant(undefined);
            setSelectedProductVariant(undefined);
            setDeleteProductVariant(undefined)

        }
    }, [isDialogOpen, reset]);


    if (products === undefined) return;

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
                                            {stores.map((store: IStore) => (
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
                                    {/* {currencies && currencies?.map((statusItem, sIndex) => (
                                        <DropdownMenuItem
                                            key={sIndex}
                                            onClick={() => {
                                            }}>
                                            {statusItem.name}
                                        </DropdownMenuItem>
                                    ))} */}


                                </div>
                                <div className="h-6" />
                                <InputImageWithLabelAndError
                                    key={editingProduct ? `edit-thumb-${editingProduct.id}` : 'create-thumb'}
                                    initialPreviews={editingProduct?.thumbnail ? [convertImageToValidUrl(editingProduct.thumbnail)] : []}
                                    label="Thumbnail"
                                    error={errors.thumbnail?.message?.toString()}
                                    height={150}
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
                                    initialPreviews={editingProduct && editingProduct.productImages ? editingProduct.productImages.map(i => convertImageToValidUrl(i)) : []}
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
                            <div className="h-2" />


                            <fieldset disabled={false}>

                                <div className="flex flex-col">
                                    <SelectLableAndError
                                        label="Variants"
                                        dataset={variants ?? []}
                                        initialData={currentVariant?.name}
                                        onChange={(e) => {
                                            const variant = variants?.find(v => v.name === e);
                                            if (variant !== undefined)
                                                setCurrentVariant({ id: variant?.id, name: variant.name });
                                        }}
                                    />
                                </div>

                            </fieldset>


                            <fieldset disabled={currentVariant === undefined}>

                                <div className="flex flex-col">
                                    <div className="mt-2 flex flex-wrap gap-2 mb-2">
                                        {selectedProductVariants && selectedProductVariants?.map((variant, index) => (
                                            <div
                                                key={index} className="flex items-center space-x-2 bg-gray-200 px-2 py-1 rounded">
                                                {<span>{variants?.find(x => x.id == variant.variantId)?.name}: {
                                                    variants?.find(v => v.id === variant.variantId)?.name.toLowerCase() === ('color') ? <div
                                                        style={{ background: variant.name, height: 20, width: 20, borderRadius: '50%', borderColor: 'black', borderWidth: 1 }}
                                                    /> :
                                                        variant.name
                                                }</span>}
                                                <div onClick={() => {
                                                    removeProductVariant(variant)
                                                }} className="text-red-500 font-bold cursor-pointer">x</div>
                                            </div>
                                        ))}
                                    </div>
                                    <label>Sub Variant</label>
                                    <div>
                                        <InputWithLabelAndError
                                            error={productVariantErrors.name?.message}
                                            type="text"
                                            placeholder="Name"
                                            {...productVariantRegeister("name")}

                                        />
                                        <div className="h-1" />
                                        <InputWithLabelAndError
                                            error={productVariantErrors.percentage?.message}
                                            type="number"
                                            placeholder="Precentage from Price"
                                            {...productVariantRegeister("percentage", { valueAsNumber: true })}
                                        />
                                        <Button
                                            onClick={
                                                productVariantHandle((data) => {

                                                    addOrUpdateProductVariant(data.name, data.percentage);

                                                })
                                            }
                                            type="button">{"Add"}</Button>

                                    </div>
                                </div>

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
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground whitespace-nowrap">Product Variant</th>
                                <th className="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="[&_tr:last-child]:border-0 bg-white">
                            {products && products?.map((product, pIndex) => (
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
                                    <td className="p-4 align-middle">
                                        <div className="flex flex-row whitespace-nowrap">
                                            {`${product.price} ${currentCurrency?.symbol ?? product.symbol}`}
                                        </div>
                                    </td>
                                    <td className="p-4 align-middle">
                                        <div className="flex flex-row whitespace-nowrap">
                                            {product.productVariants && product.productVariants?.map((variant, index) => (
                                                <div key={index} className="flex">
                                                    {<div className="flex flex-row">
                                                        <span
                                                            className="bg-white"
                                                        >{variants?.find(x => x.id == variant.at(index)?.variantId)?.name}
                                                            {' '}:</span>
                                                        <div className="flex flex-row whitespace-nowrap gap-0.5 ml-2">
                                                            {
                                                                variant?.map((value, variantIndex) => {
                                                                    return <span key={variantIndex}>
                                                                        {
                                                                            variants?.find(x => x.id == variant.at(variantIndex)?.variantId)?.name.toLowerCase() === ('color') ? <div
                                                                                style={{ background: value.name, height: 20, width: 20, borderRadius: '50%', borderColor: 'black', borderWidth: 1 }}
                                                                            /> :
                                                                                value.name
                                                                        }
                                                                    </span>
                                                                })

                                                            }

                                                        </div>
                                                    </div>

                                                    }
                                                </div>
                                            ))}
                                        </div>
                                    </td>
                                    <td className="p-4 align-middle">
                                        <div className="flex gap-2">
                                            <Button variant="outline" size="icon" onClick={() => { setEditingProduct(product); setIsDialogOpen(true); }}>
                                                <Pencil className="h-4 w-4" />
                                            </Button>
                                            <Button variant="destructive" size="icon" onClick={() => {
                                                setEditingProduct(product);
                                                deleteProductMutation.mutate({ productId: product.id, storeId: product.storeId })
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
                            src={selectedProduct.thumbnail}
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
