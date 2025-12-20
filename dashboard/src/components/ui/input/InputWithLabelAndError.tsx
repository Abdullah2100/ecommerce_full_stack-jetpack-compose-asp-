import { Input } from "./input";
import { Label } from "../label";
import { FieldError } from "react-hook-form";
import React from "react";

type IInputWithLabelAndErrorProps = {
    label: string
    error: FieldError | undefined
    isDisable?:boolean
    type: string
    onChange?: (e: React.ChangeEvent<HTMLInputElement>) => void
};

export const InputWithLabelAndError = React.forwardRef<HTMLInputElement, IInputWithLabelAndErrorProps>(
    ({ label, error, onChange,isDisable=false ,type,...props }, ref) => {
        return (
            <div>
                <Label className="mb-2" >{label}</Label>
                <Input
                disabled={isDisable}
                    type={type}
                {...props} ref={ref} onChange={onChange} />
                {error && <p className="text-red-500 text-xs mt-1 ">{error.message as string}</p>}
            </div>
        );
    }
);


