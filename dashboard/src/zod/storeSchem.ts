import { z } from "zod";

const createStoreSchema = z.object({
    name: z.string().min(1, { message: "Store name is required" }),
    wallpaperImage: z.instanceof(File, { message: "Wallpaper image is required" }),
    smallImage: z.instanceof(File, { message: "Small image is required" }),
    longitude: z.coerce.number({ message: "Longitude must be a number" }),
    latitude: z.coerce.number({ message: "Latitude must be a number" }),
});

const updateStoreSchema = z.object({
    id: z.string().optional(),
    name: z.string().min(1, { message: "Store name is required" }),
    wallpaperImage: z.instanceof(File).optional(),
    smallImage: z.instanceof(File).optional(),
    longitude: z.coerce.number({ message: "Longitude must be a number" }),
    latitude: z.coerce.number({ message: "Latitude must be a number" }),
});

export { createStoreSchema, updateStoreSchema };