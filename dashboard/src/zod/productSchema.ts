import { z } from "zod";

const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
const ACCEPTED_IMAGE_TYPES = ["image/jpeg", "image/jpg", "image/png", "image/webp"];

 

const baseProductSchema = z.object({
    name: z.string().min(1, 'Name is required'),
    description: z.string().min(1, 'Description is required'),
    price: z.coerce.number().positive("Price must be a positive number"),
    symbol: z.string().min(1, 'Symbol is required'),
    thumbnail: z.instanceof(File).optional(),
    images: z.any().optional(),
});


export const createProductSchema = baseProductSchema;
export const updateProductSchema = baseProductSchema;