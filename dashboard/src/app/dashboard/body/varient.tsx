"use client"
import { useMutation, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "react-toastify";
import IVarient from "../../../model/IVariant";
import { Button } from "@/components/ui/button";
import { EditeIcon } from "../../../../public/images/editeIcon";
import { DeleteIcon } from "../../../../public/images/delete";
import { createVarient, deleteVarient, getVarient, updateVarient } from "@/lib/api/variant";

const Variant = () => {
    const [currentPage] = useState(1);
    const [isUpdate, setIsUpdate] = useState(false);
    const [variant, setVariant] = useState<IVarient>({ id: undefined, name: "" });

    const { data, refetch } = useQuery({
        queryKey: ["vareints"],
        queryFn: () => getVarient(currentPage),
    });

    const deleteVarientFunc = useMutation({
        mutationFn: (id: string) => deleteVarient(id),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("تم الحذف بنجاح");
        },
    });

    const createVarientFunc = useMutation({
        mutationFn: (data: IVarient) => createVarient(data),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("تمت الإضافة بنجاح");
            setVariant({ id: undefined, name: "" });
            setIsUpdate(false);
        },
    });

    const updateVarientFunc = useMutation({
        mutationFn: (data: IVarient) => updateVarient(data),
        onError: (e: any) => toast.error(e.message),
        onSuccess: () => {
            refetch();
            toast.success("تم التعديل بنجاح");
            setVariant({ id: undefined, name: "" });
            setIsUpdate(false);
        },
    });

    const submit = () => {
        if (isUpdate && variant.id) {
            updateVarientFunc.mutate(variant);
        } else {
            createVarientFunc.mutate(variant);
        }
    };

    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-orange-600 bg-clip-text text-transparent">
                Variants
            </h1>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                {/* Form */}
                <div className="md:col-span-1">
                    <div className="p-6 rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm space-y-6">
                        <h2 className="text-lg font-semibold">{variant.name ? "Edit Variant" : "New Variant"}</h2>

                        <div className="space-y-2">
                            <label className="text-sm font-medium">Variant Name</label>
                            <input
                                type="text"
                                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 mt-1"
                                placeholder="e.g. Color, Size"
                                value={variant.name}
                                onChange={(e) => setVariant({ ...variant, name: e.target.value })}
                            />
                        </div>

                        <Button
                            disabled={variant.name.trim().length < 1 || updateVarientFunc.isPending || createVarientFunc.isPending}
                            onClick={submit}
                            className="w-full shadow-lg shadow-primary/25"
                        >
                            {isUpdate ? "Update Variant" : "Create Variant"}
                        </Button>
                    </div>
                </div>

                {/* List */}
                <div className="md:col-span-2">
                    <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
                        <div className="overflow-x-auto">
                            <table className="w-full text-sm text-left">
                                <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
                                    <tr>
                                        <th className="px-6 py-4">#</th>
                                        <th className="px-6 py-4">Name</th>
                                        <th className="px-6 py-4 text-right">Actions</th>
                                    </tr>
                                </thead>
                                <tbody className="divide-y divide-border/50">
                                    {data != undefined && data.length > 0 &&
                                        data.map((value, index) => (
                                            <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                                                <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                                                <td className="px-6 py-4 font-medium text-foreground group-hover:text-primary transition-colors">{value.name}</td>
                                                <td className="px-6 py-4 text-right">
                                                    <div className="flex justify-end gap-2">
                                                        <button
                                                            onClick={() => {
                                                                setVariant(value);
                                                                setIsUpdate(true);
                                                            }}
                                                            className="p-2 rounded-md hover:bg-primary/10 text-primary transition-colors"
                                                        >
                                                            <EditeIcon className="h-4 w-4" />
                                                        </button>
                                                        <button
                                                            onClick={() => deleteVarientFunc.mutate(value.id ?? "")}
                                                            className="p-2 rounded-md hover:bg-destructive/10 text-destructive transition-colors"
                                                        >
                                                            <DeleteIcon className="h-4 w-4 fill-current" />
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        ))}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default Variant;