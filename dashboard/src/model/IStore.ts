export default interface IStore {
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