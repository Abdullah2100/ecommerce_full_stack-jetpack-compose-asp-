import { Input } from "./input";
import { Label } from "../label";
import { FieldError } from "react-hook-form";
import React, { useEffect, useRef, useState } from "react";
import IVariant from "@/model/IVariant";

type ISelectLableAndErrorProps = {
    label: string;
    error?: String | undefined;
    dataset?: IVariant[];
    initialData?: string;
    onChange?: (value: string) => void;
};

export const SelectLableAndError = React.forwardRef<
    HTMLInputElement,
    ISelectLableAndErrorProps
>(({ label, error, onChange, dataset, initialData: initialPreviews = "", ...props }, ref) => {
    const [previews, setPreviews] = useState<string>(initialPreviews);

    useEffect(()=>{
        setPreviews(initialPreviews);
    },[initialPreviews])
    
    return (
        <div className="max-h-96">
            <Label className="mb-2">{label}</Label>

            <select
                value={previews ?? ""}
                onChange={(e) => {
                    setPreviews(e.target.value);
                    onChange?.(e.target.value);
                }}
                className="w-full p-2 border rounded"
            >
                <option value="">Select a variant</option>
                {dataset && dataset?.map(v => (
                    <option key={v.id} value={v.name}>{v.name}</option>
                ))}
            </select>


            {error && (
                <p className="text-red-500 text-xs mt-2">{error}</p>
            )}


        </div>
    );
});