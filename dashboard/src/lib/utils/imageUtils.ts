/**
 * Converts image URLs from 0.0.0.0 to localhost for local development
 */
export function convertImageToValidUrl(imageUrl: string): string {
    if(imageUrl.length==0)return imageUrl;
    return imageUrl.replace("0.0.0.0", "localhost");
}
