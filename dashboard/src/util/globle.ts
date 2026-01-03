import { ICurrency } from "@/model/ICurrency";

class Util {
  static token: string = "";
}

function replaceUrlWithNewUrl(url: string): string {
  if (url === undefined || url.length === 0) return "";
  return url.replace("http://0.0.0.0", "http://localhost");
}

const updateCurrency = (
  mony: number,
  currentSymbol: String,
  newCurrency: ICurrency | undefined,
  currcies: ICurrency[]) => {
  if (newCurrency == undefined) return mony.toFixed(2)

  const currentCurrency = currcies.find(x => x.symbol == currentSymbol)

  switch (newCurrency.isDefault) {
    case true:
      return mony / (currentCurrency?.value ?? 1)
    default:
      {
        switch (!newCurrency.isDefault) {
          case true: return mony * (currentCurrency?.value ?? 1)
          default: return (mony / (currentCurrency?.value ?? 1)) * (newCurrency?.value ?? 1)
        }
      }
  }

}

export { Util, replaceUrlWithNewUrl, updateCurrency };
