import { useState, useRef, useEffect } from "react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import { useMutation, useQuery } from "@tanstack/react-query";
import { iUserUpdateInfoDto } from "@/dto/response/iUserUpdateInfoDto";
import { getMyInfo, updateUser as updateFun } from "@/lib/api/user";
import { toast } from "react-toastify";
 

const MyInfoPage = () => {
    // const data = mockMyInfo;

    const { data, refetch, isSuccess } = useQuery({
        queryKey: ['myinfo'],
        queryFn: () => getMyInfo()
    })

    const updateUserData = useMutation(
        {
            mutationFn: (userData: iUserUpdateInfoDto) => updateFun(userData),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (data) => {
                console.log("user updated ", data);
                toast.success("تم التعديل بنجاح");
                refetch();
                setUserUpdate(prev => ({
                    ...prev,
                    password: '',
                    newPassword: ''
                }));
                setThumbnailFile(undefined);
            }
        }
    )

    const [userUpdate, setUserUpdate] = useState<iUserUpdateInfoDto>({
        name: data?.name ?? null,
        phone: data?.phone ?? null,
        password: null,
        newPassword: null,
        thumbnail: null
    });

    const [isDraggable, setDraggable] = useState(false)
    const [thumbnailFile, setThumbnailFile] = useState<File | undefined>(undefined);
    const [previewImage, setPreviewImage] = useState<undefined | string>();
    const inputRef = useRef<HTMLInputElement>(null);


    useEffect(() => {
        if (data?.thumbnail !== undefined) {
            setPreviewImage(data?.thumbnail);
        }
        console.log("data changed ", data);
    }
        , [previewImage, data]);


    if (data === undefined) return null;



    return (
        <div className="flex flex-col w-full h-full space-y-8 p-6 animate-in fade-in duration-500 max-w-2xl mx-auto">
            <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-teal-600 bg-clip-text text-transparent text-center">
                My Profile
            </h1>

            <div className="bg-card/50 backdrop-blur-sm border border-border/50 rounded-2xl p-8 shadow-sm space-y-8">
                {/* Profile Image Section */}
                <div className="flex flex-col items-center space-y-4">
                    <input
                        type="file"
                        hidden
                        ref={inputRef}
                        onChange={(e) => {
                            if (e.target.files && e.target.files.length > 0) {
                                const file = e.target.files[0];
                                setPreviewImage(URL.createObjectURL(file));
                            }
                        }}
                    />

                    <div
                        onDragOver={(e) => {
                            e.preventDefault();
                            setDraggable(true);
                            console.log('is draggable now ')
                        }}
                        onDragLeave={() => {
                            console.log("not draggable any more")
                            setDraggable(false)
                        }}
                        onDrop={(e) => {
                            e.preventDefault();

                            const file = e.dataTransfer.files[0];
                            if (file) {
                                setThumbnailFile(file);
                                setPreviewImage(URL.createObjectURL(file));
                            }
                        }}

                        className="relative group cursor-pointer" onClick={() => inputRef.current?.click()}>
                        <div className={`h-32 w-32 rounded-full border-4 border-background shadow-xl overflow-hidden ring-2 ring-border 
                           ${isDraggable ? 'border-dashed border-primary' : 'group-hover:ring-primary'}  transition-all`}>
                            {previewImage && <Image
                                src={previewImage ?? ""}
                                alt="Profile"
                                fill
                                className="object-cover"
                            />}

                        </div>
                        {/* <div className="absolute bottom-0 right-0 p-2 bg-primary rounded-full shadow-lg transform translate-x-1/4 translate-y-1/4 border-4 border-background">
                            <div className="relative h-4 w-4">
                                <Image 
                                src={edite} alt="Edit" fill className="object-contain brightness-0 invert" />
                            </div>
                        </div> */}
                    </div>
                    <div className="text-center">
                        <h2 className="text-xl font-semibold">{data.name}</h2>
                        <p className="text-sm text-muted-foreground">{data.email}</p>
                    </div>
                </div>

                {/* Form Section */}
                <div className="space-y-6">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Full Name</label>
                            <input
                                type="text"
                                className="flex h-10 w-full rounded-md border border-input bg-background/50 px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                                value={userUpdate.name ?? data.name}
                                onChange={(e) => setUserUpdate({ ...userUpdate, name: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Phone Number</label>
                            <input
                                type="tel"
                                className="flex h-10 w-full rounded-md border border-input bg-background/50 px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                                value={userUpdate.phone ?? data.phone}
                                onChange={(e) => setUserUpdate({ ...userUpdate, phone: e.target.value })}
                            />
                        </div>
                    </div>

                    <div className="space-y-2">
                        <label className="text-sm font-medium">Email Address</label>
                        <input
                            type="email"
                            disabled
                            className="flex h-10 w-full rounded-md border border-input bg-muted/50 px-3 py-2 text-sm text-muted-foreground cursor-not-allowed"
                            value={data.email}
                        />
                    </div>

                    <div className="pt-4">
                        <Button
                            onClick={() => {
                                const updateData: iUserUpdateInfoDto = {
                                    ...userUpdate,
                                    ...(thumbnailFile && { thumbnail: thumbnailFile })
                                };
                                updateUserData.mutate(updateData);
                            }}
                            className="w-full shadow-lg shadow-primary/25 h-11 text-base">
                            Save Changes
                        </Button>
                    </div>
                </div>
            </div>

        </div>
    );
}
export default MyInfoPage;