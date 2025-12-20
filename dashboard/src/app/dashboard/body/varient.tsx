"use client"
import InputWithTitle from "@/components/ui/input/inputWithTitle";
import { Label } from "@/components/ui/label";
import { useMutation, useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { toast } from "react-toastify";
import { iVarient } from "../../../model/iVarient";
import { Button } from "@/components/ui/button";
import { EditeIcon } from "../../../../public/images/editeIcon";
import { DeleteIcon } from "../../../../public/images/delete";
import { createVarient, deleteVarient, getVarient, updateVarient } from "@/lib/api/variant";

const Variant = () => {
    const [varient, setVarient] = useState<iVarient>({
        id: undefined,
        name: ''
    });
    const [currnetPage, setCurrentPage] = useState(1);

    const { data, refetch } = useQuery({
        queryKey: ['vareints'],
        queryFn: () => getVarient(currnetPage)

    })

    const deleteVarientFunc = useMutation(
        {
            mutationFn: (id: string) => deleteVarient(id),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  الحذف بنجاح")
            }
        }
    )
    const createVarientFunc = useMutation(
        {
            mutationFn: (data: iVarient) => createVarient(data),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  الاضافة بنجاح")
                setVarient({ id: undefined, name: '' })

            }
        }
    )

    const updateVarientFunc = useMutation(
        {
            mutationFn: (data: iVarient) => updateVarient(data),
            onError: (e) => {
                toast.error(e.message)
            },
            onSuccess: (res) => {
                refetch()
                toast.success("تم  الاضافة بنجاح")
                setVarient({ id: undefined, name: '' })

            }
        }
    )


    return (
        <div className="flex flex-col w-auto h-auto">
            <Label className="text-5xl">Variant</Label>
            <div className="h-10" />
            <div className="flex flex-col w-40">
                <InputWithTitle
                    maxLength={40}
                    title="Name"
                    name={varient.name}
                    placeHolder="Enter Your Variant"
                    onchange={
                        (value: string) => { setVarient((data) => ({ ...data, name: value })) }
                    }
                />
                <div className="h-2" />

                <Button
                    disabled={varient.name.trim().length < 1 || createVarientFunc.isPending || deleteVarientFunc.isPending}
                    className='bg-[#452CE8]'
                    onClick={() => varient.id == undefined ? createVarientFunc.mutate(varient) : updateVarientFunc.mutate(varient)}
                >
                    {varient.id != undefined ? 'Update' : 'Create'}
                </Button>
            </div>
            <div className="h-10" />

            {data != undefined && <div className="w-fit">

                <div className="p-3">
                    {/* Table */}
                    <div className="overflow-x-auto border-2 border-[#F0F2F5]  rounded-[9px]">
                        <table className="table-auto w-fit  ">
                            {/* Table header */}
                            <thead className="text-[13px]">
                                <tr
                                    className={`${data != undefined ? 'border-b-1 ' : undefined}`}>
                                    <th >
                                        <div className="font-medium text-left"></div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Name</div>
                                    </th>
                                    <th className="py-4 px-10">
                                        <div className="font-medium text-left">Action</div>
                                    </th>
                                </tr>
                            </thead>
                            {/* Table body */}
                            <tbody className="text-sm font-medium">
                                {
                                    
                                   data!==undefined&&data.length>0&& data?.map((value, index) => (
                                        <tr key={index}
                                            className={`${index != data.length - 1 ? 'border-b-1' : undefined}`}
                                        >
                                            <td className="ps-2 py-4">
                                                <div className="text-slate-500">{index + 1}</div>
                                            </td>
                                            <td className="px-10">
                                                <div className="text-slate-500">{value.name}</div>
                                            </td>
                                            <td>
                                                <div className="flex flex-row">
                                                    <div 
                                                     onClick={() => setVarient(value)}
                                                    className="bg-[#f5fafb] border-1 border-[#107980] rounded-sm">
                                                        <EditeIcon
                                                            className="h-6 w-6 fill-[#107980] cursor-pointer mt-0 "
                                                        />
                                                    </div>
                                                    <div className="w-2" />
                                                    <div
                                                        onClick={() => deleteVarientFunc.mutate(value.id ?? "")}
                                                        className=" border-1 border-[#107980] rounded-sm relative">
                                                         <DeleteIcon
                                                            className="h-6 w-6  fill-red-700   cursor-pointer"
                                                        /> 
                                                    </div>
                                                </div>

                                            </td>

                                        </tr>
                                    ))
                                }
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>}

        </div>
    );

};
export default Variant;