import { useEffect, useState } from "react";
import Image from "next/image";
import { toast } from "react-toastify";
import { Button } from "@/components/ui/button";
import { getUserPages, getUserAtPage, changeUserStatus, createUser, updateUser } from "@/lib/api/user";
import { useQueryClient, useQuery, useMutation } from "@tanstack/react-query";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";
import { Dialog } from "@/components/ui/dialog";
import { createUserSchema, updateUserSchema } from "@/zod/userSchema";
import { zodResolver } from "@hookform/resolvers/zod";
import { InputWithLabelAndError } from "@/components/ui/input/InputWithLabelAndError";
import { useForm } from "react-hook-form";
import { IUserInfo } from "@/model/IUserInfo";
import { Ban, LockOpen, Pencil } from "lucide-react";
import { InputImageWithLabelAndError } from "@/components/ui/input/inputImageWithLableAndError";
import { iUserUpdateInfoDto } from "@/dto/response/iUserUpdateInfoDto";

const Users = () => {
    const [isDialogOpen, setIsDialogOpen] = useState(false);
    const [isUpdate, setIsUpdate] = useState(false);
    const [user, setUser] = useState<undefined | IUserInfo>(undefined);

    const { register,
        handleSubmit,
        reset,
        formState: { errors } } = useForm({
            resolver: zodResolver(createUserSchema)
        });

    const { register: updateRegister,
        handleSubmit: updateHandleSubbmi,
        setValue,
        reset: updateReset,
        formState: { errors: updateError } } = useForm({
            resolver: zodResolver(updateUserSchema)
        });

    const queryClient = useQueryClient()
    const { data: userPages } = useQuery({
        queryKey: ['usersPage'],
        queryFn: () => getUserPages()

    })

    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch, isPlaceholderData } = useQuery({
        queryKey: ['users', currnetPage],
        queryFn: () => getUserAtPage(currnetPage)

    })



    const changeUserStatusFun = useMutation(
        {
            mutationFn: (userId: string) => changeUserStatus(userId),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم التعديل بنجاح")


            }
        }
    )


    const createUserMutation = useMutation({
        mutationFn: (data: any) => createUser(data),
        onError: (e) => {
            toast.error(e.message)
        },
        onSuccess: (res) => {
            refetch()
            toast.success("User created successfully")
            setIsDialogOpen(false);
        }
    })

    const updateUserMutation = useMutation({
        mutationFn: (data: iUserUpdateInfoDto) => updateUser(data),
        onError: (e) => {
            toast.error(e.message)
        },
        onSuccess: (res) => {
            refetch()
            toast.success("User created successfully")
            setIsDialogOpen(false);
        }
    })

    const handleFormSubmit = (data: any) => {
         
        if (isUpdate) {

            updateUserMutation.mutate({
                name:data?.name?.length > 0 && data.name !== user?.name?data.name:null,
                phone:data?.phone?.length > 0 && data.phone !== user?.phone?data.phone:null,
                password:data?.password?.length > 0?data.password:null,
                newPassword:data?.newPassword?.length > 0?data.newPassword:null,
                thumbnail:data?.thumbnail!=undefined?data.thumbnail:null
            });
        } else {
            createUserMutation.mutate({
                'name': data.name,
                'phone': data.phone,
                'email': data.email,
                'password': data.password
            });
        }
    }

    useEffect(() => {
        queryClient.prefetchQuery({
            queryKey: ['users', currnetPage],
            queryFn: () => getUserAtPage(currnetPage),
        })
    }, [currnetPage])

    useEffect(() => {
        if (isDialogOpen === false) {
            reset();
            updateReset()

        } else {
            setValue("name", user?.name ?? "");
            setValue("phone", user?.phone ?? "");
            setValue("email", user?.email ?? "")
        }
    }, [isDialogOpen]);





    if (data == undefined) return;


    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <div className="flex justify-between items-center">
                <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-indigo-600 bg-clip-text text-transparent">
                    Users
                </h1>
                <Dialog
                    open={isDialogOpen}
                    onOpenChange={setIsDialogOpen}
                    trigger={
                        <Button
                            onClick={() => {
                                setIsDialogOpen(true);
                            }}
                            size="sm" className="bg-primary text-white w-[100px]">
                            Create User
                        </Button>
                    }
                    title={isUpdate ? "Update Store" : "Create New User"}
                    footer={<>
                        {isUpdate ? <Button
                            onClick={updateHandleSubbmi(handleFormSubmit)}
                            type="submit" form="create-store-form"
                            disabled={createUserMutation.isPending || updateUserMutation.isPending}
                        >
                            {isUpdate ? 'Update' : "Create"}
                        </Button> :
                            <Button
                                onClick={handleSubmit(handleFormSubmit)}
                                type="submit" form="create-store-form"
                                disabled={createUserMutation.isPending || updateUserMutation.isPending}
                            >
                                {isUpdate ? 'Update' : "Create"}
                            </Button>}</>
                    }
                >
                    <form id="create-store-form" className="space-y-4 overflow-y-auto ">
                        {isUpdate ? <div>
                            <InputWithLabelAndError
                                type="text"
                                {...updateRegister("name")}
                                label="Name" error={updateError.name?.message} />
                        </div> :
                            <div>
                                <InputWithLabelAndError
                                    type="text"
                                    {...register("name")}
                                    label="Name" error={errors.name?.message} />
                            </div>}


                        {isUpdate ? <div>
                            <InputWithLabelAndError
                                type="text"
                                {...updateRegister("phone")}
                                label="Phone" error={updateError.phone?.message} />
                        </div> :
                            <div>
                                <InputWithLabelAndError
                                    type="text"
                                    {...register("phone")}
                                    label="Phone" error={errors.phone?.message} />
                            </div>}
                        {isUpdate ? <div>
                            <InputWithLabelAndError
                                isDisable={isUpdate}
                                type="text"
                                {...updateRegister("email")}
                                label="Email" error={updateError.email?.message} />
                        </div> :
                            <div>
                                <InputWithLabelAndError
                                    isDisable={isUpdate}
                                    type="text"
                                    {...register("email")}
                                    label="Email" error={errors.email?.message} />
                            </div>
                        }

                        {isUpdate && user &&
                            <InputImageWithLabelAndError
                                key={`edit-sm-${user.id}`}
                                initialPreviews={user ? [convertImageToValidUrl(user.thumbnail)] : []}
                                height={200}
                                onChange={(files: File[]) => {
                                    if (files && files.length > 0) {
                                        setValue("thumbnail", files[0]);

                                    }
                                    updateRegister("thumbnail")
                                }}
                                label="Small Image" 
                                error={updateError.thumbnail?.message} />}

                        {isUpdate ? <div>
                            <InputWithLabelAndError
                                type="text"
                                {...updateRegister("password")}
                                label="Password" 
                                error={updateError.password?.message} />
                        </div> :
                            <div>
                                <InputWithLabelAndError
                                    isDisable={isUpdate}
                                    type="text"
                                    {...register("password")}
                                    label="Email" 
                                    error={errors.password?.message} />
                            </div>}
                        {isUpdate && <div>
                            <InputWithLabelAndError
                                type="text"
                                {...updateRegister("newPassword")}
                                label="Password" 
                                error={updateError.newPassword?.message} />
                        </div>}
                    </form>
                </Dialog>

            </div>

            <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
                <div className="overflow-x-auto">
                    <table className="w-full text-sm text-left">
                        <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
                            <tr>
                                <th className="px-6 py-4">#</th>
                                <th className="px-6 py-4">User</th>
                                <th className="px-6 py-4">Contact</th>
                                <th className="px-6 py-4">Store Info</th>
                                <th className="px-6 py-4 text-center">Status</th>
                                <th className="px-6 py-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-border/50">
                            {data.map((value, index) => (
                                <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                                    <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                                    <td className="px-6 py-4">
                                        <div className="flex items-center gap-4">
                                            <div className="relative h-10 w-10 rounded-full overflow-hidden border border-border/50 shadow-sm">
                                                <Image
                                                    src={convertImageToValidUrl(value.thumbnail)}
                                                    alt={value.name}
                                                    fill
                                                    className="object-cover"
                                                />
                                            </div>
                                            <div className="flex flex-col">
                                                <span className="font-medium text-foreground group-hover:text-primary transition-colors">{value.name}</span>
                                                <span className="text-xs text-muted-foreground">{value.isAdmin ? 'Administrator' : 'User'}</span>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        <div className="flex flex-col gap-1">
                                            <span className="text-xs flex items-center gap-2">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-muted-foreground"><path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" /></svg>
                                                {value.phone}
                                            </span>
                                            <span className="text-xs flex items-center gap-2">
                                                <svg xmlns="http://www.w3.org/2000/svg" width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="text-muted-foreground"><rect width="20" height="16" x="2" y="4" rx="2" /><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" /></svg>
                                                {value.email}
                                            </span>
                                        </div>
                                    </td>
                                    <td className="px-6 py-4">
                                        {value.storeName ? (
                                            <div className="flex items-center gap-2">
                                                <span className="h-2 w-2 rounded-full bg-green-500"></span>
                                                <span className="font-medium">{value.storeName}</span>
                                            </div>
                                        ) : (
                                            <span className="text-muted-foreground text-xs italic">No Store</span>
                                        )}
                                    </td>
                                    <td className="px-6 py-4 text-center">
                                        <span className={`
                                            inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium
                                            ${value.isActive
                                                ? 'bg-green-100 text-green-700 dark:bg-green-900/30 dark:text-green-400'
                                                : 'bg-red-100 text-red-700 dark:bg-red-900/30 dark:text-red-400'}
                                        `}>
                                            {value.isActive ? 'Active' : 'Blocked'}
                                        </span>
                                    </td>
                                    <td className="px-6 py-4 text-right">
                                        {value.isAdmin ? (
                                            <span className="text-xs font-medium text-muted-foreground bg-muted px-2 py-1 rounded">Current User</span>
                                        ) : (
                                            // <Button
                                            //     variant={value.isActive ? "destructive" : "default"}
                                            //     size="sm"
                                            //     className="h-7 text-xs"
                                            //     onClick={() => {
                                            //         // Mock toggle logic
                                            //         changeUserStatusFun.mutate(value.id)
                                            //     }}
                                            // >
                                            //     {value.isActive ? 'Block' : 'Unblock'}
                                            // </Button>
                                            <div className="flex flex-row gap-2">
                                                <Button
                                                    variant="outline"
                                                    size="icon"
                                                    className="h-8 w-8"
                                                    onClick={() => {
                                                        setUser(value)
                                                        setIsUpdate(true)
                                                        setIsDialogOpen(true)
                                                    }}
                                                >
                                                    <Pencil className="h-4 w-4" />
                                                </Button>
                                                <Button
                                                    variant={!value.isActive ? "destructive" : "default"}
                                                    size="icon"
                                                    className="h-8 w-8"
                                                    onClick={() => {
                                                        changeUserStatusFun.mutate(value.id)
                                                    }}
                                                >
                                                    {value.isActive ? <LockOpen className="h-4 w-4" /> : <Ban className="h-4 w-4" />}
                                                </Button>
                                            </div>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            </div>

            <div className="flex justify-center mt-6">
                <div className="flex gap-2">
                    {Array.from({ length: userPages ?? 1 }, (_, i) => (
                        <button
                            key={i}
                            onClick={() => setCurrentPage(i + 1)}
                            className={`
                                h-9 w-9 flex items-center justify-center rounded-lg text-sm font-medium transition-all duration-200
                                ${currnetPage === i + 1
                                    ? 'bg-primary text-primary-foreground shadow-md scale-105'
                                    : 'bg-card border border-border hover:bg-accent hover:text-accent-foreground'
                                }
                            `}
                        >
                            {i + 1}
                        </button>
                    ))}
                </div>
            </div>
        </div>
    );
};
export default Users;