import { useState, useRef } from "react";
import { toast } from "react-toastify";
import { Button } from "@/components/ui/button";
import Image from "next/image";
import edite from "../../../../public/images/edite.svg";
import { DeleteIcon } from "../../../../public/images/delete";
import { useMutation, useQuery } from "@tanstack/react-query";
import { createCategory, deleteCategory, getCategory, updateCategory } from "@/lib/api/category";
import iCategoryDto from "@/dto/response/iCategoryDto";
import { convertImageToValidUrl } from "@/lib/utils/imageUtils";

const Category = () => {
  const [currentPage] = useState(1);
  const [isUpdate, changeUpdateStatus] = useState(false);
  const [category, setCategory] = useState<iCategoryDto>({ name: "", image: undefined });
  const [fileUrlHolder, setFileUrlHolder] = useState("");
  const inputRef = useRef<HTMLInputElement>(null);

  const { data, refetch } = useQuery({
    queryKey: ["categories"],
    queryFn: () => getCategory(currentPage),
  });

  const deleteCategoryFunc = useMutation({
    mutationFn: (id: string) => deleteCategory(id),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تم الحذف بنجاح");
    },
  });
  const createCategoryFun = useMutation({
    mutationFn: (data: iCategoryDto) => createCategory(data),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تمت الإضافة بنجاح");
      setCategory({ name: "", image: undefined });
      setFileUrlHolder("");
    },
  });

  const updateCategoryFun = useMutation({
    mutationFn: (data: iCategoryDto) => updateCategory(data),
    onError: (e) => {
      toast.error(e.message);
    },
    onSuccess: () => {
      refetch();
      toast.success("تم التعديل بنجاح");
      setCategory({ name: "", image: undefined });
      setFileUrlHolder("");
    },
  });


  const updateOrInsertFunc = () => {
    if (isUpdate) {
      updateCategoryFun.mutate({
        id: category.id,
        name: category.name,
        image: category.image,
      })
    } else {
      createCategoryFun.mutate({
        name: category.name,
        image: category.image,
      })
    }
  }
  return (
    <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
      <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-orange-600 bg-clip-text text-transparent">
        Categories
      </h1>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
        {/* Create/Edit Section */}
        <div className="md:col-span-1 space-y-6">
          <div className="p-6 rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm space-y-6">
            <h2 className="text-lg font-semibold">
              {category.name ? "Edit Category" : "New Category"}
            </h2>

            <div className="flex flex-col items-center space-y-4">
              <input
                type="file"
                hidden
                ref={inputRef}
                onChange={(e) => {
                  if (e.target.files && e.target.files.length > 0) {
                    // Mock file handling
                    const file = e.target.files[0];
                    if (file !== undefined) {
                      setCategory({ ...category, image: file })
                      setFileUrlHolder(URL.createObjectURL(file));
                    }
                  }
                }}
              />

              <div className="relative group cursor-pointer" onClick={() => inputRef.current?.click()}>
                <div className="h-32 w-32 rounded-full border-2 border-dashed border-border flex justify-center items-center overflow-hidden bg-muted/30 transition-colors group-hover:border-primary/50">
                  {fileUrlHolder ? (
                    <Image
                      src={fileUrlHolder}
                      alt="Category preview"
                      fill
                      className="object-cover"
                    />
                  ) : (
                    <div className="flex flex-col items-center text-muted-foreground">
                      <span className="text-xs">Upload Image</span>
                    </div>
                  )}
                </div>
                <div className="absolute bottom-0 right-0 p-2 bg-primary rounded-full shadow-lg transform translate-x-1/4 translate-y-1/4">
                  <div className="relative h-4 w-4">
                    <Image src={edite} alt="Edit" fill className="object-contain brightness-0 invert" />
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-2">
              <label className="text-sm font-medium">Category Name</label>
              <input
                type="text"
                className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:cursor-not-allowed disabled:opacity-50 mt-1"
                placeholder="e.g. Electronics"
                value={category.name}
                onChange={(e) => setCategory({ ...category, name: e.target.value })}
              />
            </div>

            <Button
              disabled={updateCategoryFun.isPending || createCategoryFun.isPending}
              onClick={updateOrInsertFunc}
              className="w-full shadow-lg shadow-primary/25">
              {isUpdate ? "Update Category" : "Create Category"}
            </Button>
          </div>
        </div>

        {/* List Section */}
        <div className="md:col-span-2">
          <div className="w-full overflow-hidden rounded-xl border border-border/50 bg-card/50 backdrop-blur-sm shadow-sm">
            <div className="overflow-x-auto">
              <table className="w-full text-sm text-left">
                <thead className="bg-muted/30 text-muted-foreground uppercase text-xs font-semibold tracking-wider">
                  <tr>
                    <th className="px-6 py-4">#</th>
                    <th className="px-6 py-4">Image</th>
                    <th className="px-6 py-4">Name</th>
                    <th className="px-6 py-4 text-right">Actions</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-border/50">
                  {data != undefined && data.length > 0 && data.map((value, index) => (
                    <tr key={index} className="group hover:bg-muted/30 transition-all duration-200">
                      <td className="px-6 py-4 text-muted-foreground font-mono text-xs">{index + 1}</td>
                      <td className="px-6 py-4">
                        <div className="relative h-12 w-12 rounded-lg overflow-hidden border border-border/50 shadow-sm group-hover:scale-105 transition-transform">
                          <Image
                            src={convertImageToValidUrl(value.image)}
                            alt={value.name}
                            fill
                            className="object-cover"
                          />
                        </div>
                      </td>
                      <td className="px-6 py-4 font-medium text-foreground group-hover:text-primary transition-colors">
                        {value.name}
                      </td>
                      <td className="px-6 py-4 text-right">
                        <div className="flex justify-end gap-2">
                          <button
                            onClick={() => {
                              setCategory({ id: value.id, name: value.name, image: undefined });
                              setFileUrlHolder(convertImageToValidUrl(value.image));
                              changeUpdateStatus(true)
                            }}
                            className="p-2 rounded-md hover:bg-primary/10 text-primary transition-colors"
                          >
                            <div className="relative h-4 w-4">
                              <Image src={edite} alt="Edit" fill className="object-contain" />
                            </div>
                          </button>
                          <button
                            onClick={() => deleteCategoryFunc.mutate(value.id)}
                            className="p-2 rounded-md hover:bg-destructive/10 text-destructive transition-colors">
                            <DeleteIcon className="h-4 w-4 fill-current" />
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};
export default Category;
