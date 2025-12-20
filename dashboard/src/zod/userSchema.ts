import z from "zod";

 const createUserSchema = z.object({
    name: z.string().max(50, { message: "Name must be at most 50 characters long" }).min(10, { message: "Name is required" }),
    phone: z.string().regex(/^([+]?[\s0-9]+)?(\d{3}|[(]?[0-9]+[)])?([-]?[\s]?[0-9])+$/, { message: "Phone number is required" }).max(15, { message: "Phone number must be 15 characters long including country code" }),
    email: z.string()
    .email().min(20, { message: "Email is required" }).min(20, { message: "Email must be at least 20 characters long" }),
    password: z.string().min(8, { message: "Password must be at least 8 characters long" })
    .max(12, { message: "Password must be at most 12 characters long" })
    .regex( /^(?=(.*[A-Z]){2})(?=(.*\d){2})(?=(.*[a-z]){2})(?=(.*[!@#$%^&*()_+|\\/?<>:;'""-]){2})[A-Za-z\d!@#$%^&*()_+|\\/?<>:;'""-]*$/,
 { message: "Password must contain at least two uppercase letter and two special character and two lowercase letter and two numbers" }),
});

 const updateUserSchema = z.object({
    name: z.string().max(50, { message: "Name must be at most 50 characters long" }),
    phone: z.string().regex(/^([+]?[\s0-9]+)?(\d{3}|[(]?[0-9]+[)])?([-]?[\s]?[0-9])+$/, { message: "Phone number is required" }).max(15, { message: "Phone number must be 15 characters long including country code" }),
     email: z.string().optional(),
    password: z.string().min(8, { message: "Password must be at least 8 characters long" })
    .max(12, { message: "Password must be at most 12 characters long" })
    .regex( /^(?=(.*[A-Z]){2})(?=(.*\d){2})(?=(.*[a-z]){2})(?=(.*[!@#$%^&*()_+|\\/?<>:;'""-]){2})[A-Za-z\d!@#$%^&*()_+|\\/?<>:;'""-]*$/,
 { message: "Password must contain at least two uppercase letter and two special character and two lowercase letter and two numbers" }),
  newPassword: z.string().min(8, { message: "Password must be at least 8 characters long" })
    .max(12, { message: "Password must be at most 12 characters long" })
    .regex( /^(?=(.*[A-Z]){2})(?=(.*\d){2})(?=(.*[a-z]){2})(?=(.*[!@#$%^&*()_+|\\/?<>:;'""-]){2})[A-Za-z\d!@#$%^&*()_+|\\/?<>:;'""-]*$/,
 { message: "Password must contain at least two uppercase letter and two special character and two lowercase letter and two numbers" }),
     thumbnail: z.instanceof(File, { message: "Wallpaper image is required" }),

});

export {createUserSchema,updateUserSchema}