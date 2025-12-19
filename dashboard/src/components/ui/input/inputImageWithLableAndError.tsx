import { Input } from "./input";
import { Label } from "../label";
import { FieldError } from "react-hook-form";
import React, { useEffect, useRef, useState } from "react";

type IInputImageWithLabelAndErrorProps = {
    label: string;
    error?: FieldError;
    isSingle?: boolean;
    isBorderEnable?: boolean;
    height: number;
    initialPreviews?: string[];
    onChange?: (files: File[]) => void;
};

export const InputImageWithLabelAndError = React.forwardRef<
    HTMLInputElement,
    IInputImageWithLabelAndErrorProps
>(({ label, error, onChange, isSingle = true, height = 200, isBorderEnable = false, initialPreviews = [], ...props }, ref) => {
    const fileRef = (ref as React.RefObject<HTMLInputElement>) || useRef<HTMLInputElement>(null);
    const [isDragActive, setIsDragActive] = useState(false);
    const [previews, setPreviews] = useState<string[]>(initialPreviews);
    const createdObjectUrls = useRef<string[]>([]);

    useEffect(() => {
        if (initialPreviews.length > 0) {
            setPreviews(initialPreviews);
        }
    }, [initialPreviews]);



    useEffect(() => {
        return () => {
            createdObjectUrls.current.forEach((u) => URL.revokeObjectURL(u));
            createdObjectUrls.current = [];
        };
    }, []);

    const handleFiles = (fileList: FileList | null) => {
        if (!fileList || fileList.length === 0) return;
        const files = Array.from(fileList);
        const localPreviews = files.map((f) => {
            const u = URL.createObjectURL(f);
            createdObjectUrls.current.push(u);
            return u;
        });
        if (isSingle) {
            setPreviews(localPreviews.slice(0, 1));
        } else {
            setPreviews((p) => [...p, ...localPreviews]);
        }
        onChange && onChange(files);
    };

    const onInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        handleFiles(e.target.files);
    };

    const onDrop = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragActive(false);
        handleFiles(e.dataTransfer.files);
    };

    const onDragOver = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragActive(true);
    };

    const onDragLeave = (e: React.DragEvent<HTMLDivElement>) => {
        e.preventDefault();
        setIsDragActive(false);
    };

    return (
        <div className="max-h-96">
            <Label className="mb-2">{label}</Label>

            <div
                // onClick={() => fileRef.current?.click()}
                onDrop={onDrop}
                onDragOver={onDragOver}
                onDragLeave={onDragLeave}
                className={`w-full rounded-md p-4 border-2 border-dashed flex items-center justify-center cursor-pointer bg-white transition ${isDragActive ? "border-blue-400 bg-blue-50" : "border-gray-200"
                    }`}
            >
                <div className="text-center">
                    <p className="text-sm text-gray-600">Drag & drop images here</p>
                    <p className="text-xs text-gray-400 mt-1">or click to select files</p>

                    <div className="mt-3">
                        {previews.length > 0 && (
                            <div
                                className="flex gap-2 overflow-x-auto"
                                style={{ height: `${height}px` }}
                            >
                                {previews.map((src, idx) => (
                                    <div key={idx} className={`w-28 h-28 flex-shrink-0 overflow-hidden ${isBorderEnable ? "border" : ""} rounded-2xl p-2`}>
                                        <img src={src} alt={`preview-${idx}`} className="object-cover w-full h-full" />
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>

                </div>
            </div>

            <Input
                type={"file"}
                hidden
                accept="image/*"
                multiple
                {...props}
                ref={fileRef}
                onChange={onInputChange}
            />

            {error && (
                <p className="text-red-500 text-xs mt-2">{error.message as string}</p>
            )}


        </div>
    );
});