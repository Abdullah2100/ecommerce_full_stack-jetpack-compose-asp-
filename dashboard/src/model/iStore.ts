export default interface iStore {
    id: string,
    name: string,
    smallImage: string,
    wallpaperImage: string,
    longitude: number,
    latitude: number,
    userName: string,
    isBlocked: boolean,
    created_at: Date
}