import z from "zod";

export const productVariant = z.object({
    name: z.string().min(1, { message: "product variant name is required" }),
    percentage: z.number().gt(0, { message: "product variant percentage must be greater than 0" }),
});