import { useNavItems, iNavProp } from "./nav/navLinks";
import NavItem from "@/components/ui/nav/navItem";
import Image from "next/image";
import logo from "../../../public/images/logo.svg";
import { X } from "lucide-react";
import { Button } from "@/components/ui/button";

interface SidebarProps extends iNavProp {
    isOpen: boolean;
    onClose: () => void;
}

export const Sidebar = (props: SidebarProps) => {
    const navItems = useNavItems(props);
    const { isOpen, onClose } = props;

    return (
        <>
            {/* Mobile Overlay */}
            {isOpen && (
                <div
                    className="fixed inset-0 z-40 bg-black/50 lg:hidden"
                    onClick={onClose}
                />
            )}

            {/* Sidebar Container */}
            <aside
                className={`fixed top-0 left-0 z-50 h-screen w-64 bg-sidebar border-r border-sidebar-border transition-transform duration-300 ease-in-out lg:translate-x-0 lg:static lg:block ${isOpen ? "translate-x-0" : "-translate-x-full"}`}
            >
                <div className="flex h-full flex-col">
                    {/* Header */}
                    <div className="flex h-16 items-center justify-between px-4 py-4 border-b border-sidebar-border">
                        <div className="relative h-10 w-32">
                            <Image
                                src={logo}
                                alt="Logo"
                                fill
                                className="object-contain object-left"
                            />
                        </div>
                        <Button variant="ghost" size="icon" className="lg:hidden" onClick={onClose}>
                            <X className="h-5 w-5" />
                        </Button>
                    </div>

                    {/* Nav Items */}
                    <div className="flex-1 overflow-y-auto py-4 px-3 space-y-1">
                        {navItems.map((item, index) => (
                            <NavItem key={index} {...item} />
                        ))}
                    </div>
                </div>
            </aside>
        </>
    );
};
