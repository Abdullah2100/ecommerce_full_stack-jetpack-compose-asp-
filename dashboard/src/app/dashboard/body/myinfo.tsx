import { mockMyInfo } from "@/lib/mockData";
import { useState, useRef } from "react";
import Image from "next/image";
import { Button } from "@/components/ui/button";
import edite from '../../../../public/images/edite.svg';
import { useMutation, useQuery } from "@tanstack/react-query";
import { iUserUpdateInfoDto } from "@/dto/response/iUserUpdateInfoDto";
import { getMyInfo, updateUser } from "@/lib/api/user";
import { toast } from "react-toastify";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";

const MyInfoPage = () => {
    // const data = mockMyInfo;

    const { data, refetch } = useQuery({
        queryKey: ['myinfo'],
        queryFn: () => getMyInfo()
    })

    const [userUpdate, setUserUpdate] = useState({
        name: data?.name ?? "",
        phone: data?.phone ?? "",
        email: data?.email ?? "",
        password: '',
        newPassword: '',
        thumbnail: data?.thumbnail ?? undefined
    });
    const [previewImage, setPreviewImage] = useState(convertImageToValidUrl(data?.thumbnail ?? ""));

    const inputRef = useRef<HTMLInputElement>(null);


    const updateUserData = useMutation(
        {
            mutationFn: (userData: iUserUpdateInfoDto) => updateUser(userData),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: () => {
                refetch();
                toast.success("تم التعديل بنجاح");
                setUserUpdate(prev => ({
                    ...prev,
                    thumbnail: undefined,
                    password: '',
                    newPassword: ''
                }));
            }
        }
    )


    if (data == undefined) return;

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

                    <div className="relative group cursor-pointer" onClick={() => inputRef.current?.click()}>
                        <div className="h-32 w-32 rounded-full border-4 border-background shadow-xl overflow-hidden ring-2 ring-border group-hover:ring-primary transition-all">
                            <Image
                                src={previewImage}
                                alt="Profile"
                                fill
                                className="object-cover"
                            />
                        </div>
                        <div className="absolute bottom-0 right-0 p-2 bg-primary rounded-full shadow-lg transform translate-x-1/4 translate-y-1/4 border-4 border-background">
                            <div className="relative h-4 w-4">
                                <Image src={edite} alt="Edit" fill className="object-contain brightness-0 invert" />
                            </div>
                        </div>
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
                                value={userUpdate.name}
                                onChange={(e) => setUserUpdate({ ...userUpdate, name: e.target.value })}
                            />
                        </div>
                        <div className="space-y-2">
                            <label className="text-sm font-medium">Phone Number</label>
                            <input
                                type="tel"
                                className="flex h-10 w-full rounded-md border border-input bg-background/50 px-3 py-2 text-sm ring-offset-background placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                                value={userUpdate.phone}
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
                            value={userUpdate.email}
                        />
                    </div>

                    <div className="pt-4">
                        <Button
                            // onClick={ }
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