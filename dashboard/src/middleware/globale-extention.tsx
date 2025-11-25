export { }

declare global {
    interface String {
        convertImageToValideUrl(): string;
    }
}

String.prototype.convertImageToValideUrl = function (): string {
    return this.replace("0.0.0.0", "localhost");
}