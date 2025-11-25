/**
 * Converts image URLs from 0.0.0.0 to localhost for local development
 */
export function convertImageToValidUrl(imageUrl: string): string {
    return imageUrl.replace("0.0.0.0", "localhost");
}
