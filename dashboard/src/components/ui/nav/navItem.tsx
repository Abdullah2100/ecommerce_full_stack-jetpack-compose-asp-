import { Label } from "@radix-ui/react-label";
import Image from "next/image";


export interface iNavItemProp {
    name: string,
    icon: string,
    currentIndex: number,
    selectedIndex: number,
    isNewOrder: boolean,
    chageSelectedIndex: (index: number) => void
}



export interface iNavItemProp {
    name: string,
    icon: string,
    currentIndex: number,
    selectedIndex: number,
    isNewOrder: boolean,
    chageSelectedIndex: (index: number) => void
}

const NavItem = ({ name,
    icon,
    currentIndex,
    selectedIndex,
    chageSelectedIndex,
    isNewOrder = false }: iNavItemProp) => {

    const isSelected = selectedIndex === currentIndex;

    return (
        <button
            onClick={() => chageSelectedIndex(currentIndex)}
            className={`
                group flex w-full items-center gap-3 rounded-md px-3 py-2 text-sm font-medium transition-colors
                ${isSelected
                    ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                    : 'text-sidebar-foreground hover:bg-sidebar-accent/50 hover:text-sidebar-accent-foreground'
                }
            `}
        >
            <div className="relative h-5 w-5 shrink-0">
                <Image
                    src={icon}
                    alt={name}
                    fill
                    className={`object-contain transition-opacity ${isSelected ? 'opacity-100' : 'opacity-70 group-hover:opacity-100'}`}
                />
            </div>

            <span className="flex-1 text-left truncate">{name}</span>

            {isNewOrder && (
                <span className="flex h-2 w-2 shrink-0 rounded-full bg-destructive" />
            )}
        </button>
    )
}

export default NavItem;
