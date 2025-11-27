using api.application.Interface;
using api.util;

namespace api.application.Services
{


    public class FileServices(IWebHostEnvironment host) : IFileServices
    {
        private static readonly string LocalPath = "images";

        private static string GetFileExtention(IFormFile filename)=> Path.GetExtension(filename.FileName);

        private static bool CreateDirectory(string dir)
        {
            try
            {
                Directory.CreateDirectory(dir);
                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from creating file to save image on it " + ex.Message);
                return false;
            }
        }

        public async Task<string?> SaveFile(IFormFile file, EnImageType type)
        {
            // string filePath = localPath + type.ToString()+"/";
            string filePath = Path.Combine(host.ContentRootPath, LocalPath, type.ToString());
            try
            {
                if (!Directory.Exists(filePath))
                {
                    if (!CreateDirectory(filePath))
                    {
                        return null;
                    }
                }

                var fileFullName = Path.Combine(filePath, ClsUtil.GenerateGuid() + GetFileExtention(file));

                await using (var stream = new FileStream(fileFullName, FileMode.Create))
                {
                    await file.CopyToAsync(stream);
                }

                switch (fileFullName.Contains("//"))
                {
                    case true: return fileFullName.Replace(host.ContentRootPath + "//images", "");
                    default: return fileFullName.Replace(host.ContentRootPath + "/images", "");
                }
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from saving image to local" + ex.Message);
                return null;
            }

        }

        public async Task<List<string>?> SaveFile(List<IFormFile> file, EnImageType type)
        {
            List<string> images = new List<string>();
            for (int i=0;i<file.Count;i++)
            {
                string? path = await SaveFile(file[i], type);
                if (path is null)
                {
                    DeleteFile(images);
                    return null;
                }

                images.Add(path);
            }

            return images;
        }

        public bool DeleteFile(string filePath)
        {
            try
            {
                var newFilPath = filePath.Substring(1);
                string fileRealPath = Path.Combine(host.ContentRootPath,"images", newFilPath);
                if (File.Exists(fileRealPath))
                {
                    File.Delete(fileRealPath);
                    return true;
                }

                return false;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from delete image  " + ex.Message);
                return false;
            }
        }

        public bool DeleteFile(List<string> filePaths)
        {
            try
            {
                foreach (var filePath in filePaths)
                {
                    DeleteFile(filePath);
                }

                return true;
            }
            catch (Exception ex)
            {
                Console.WriteLine("this the error from delete image  " + ex.Message);
                return false;
            }
        }
    }
    
}