const homIcon = "/images/home.svg";
const logo = "/images/logo.svg";
const myInfo = "/images/user.svg";
const users = "/images/users.svg";
const store = "/images/store.svg";
const category = "/images/category.svg";
const order = "/images/order.svg";
const product = "/images/product.svg";
const varient = "/images/products-major.svg";

import Image from "next/image";
import NavItem, { iNavItemProp } from "@/components/ui/nav/navItem";
import useOrder from "@/stores/order";

export interface iNavProp {
  selectedIndex: number;
  setSelectedIndex: (index: number) => void;
  isNewOrder: boolean;
}

export const useNavItems = ({ selectedIndex, setSelectedIndex, isNewOrder }: iNavProp) => {
  const { changeHasNewOrderStatus } = useOrder();

  const navLinkItems: iNavItemProp[] = [
    {
      name: "Analytics",
      icon: homIcon,
      currentIndex: 0,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
    {
      name: "Products",
      icon: product,
      currentIndex: 1,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
    {
      name: "Orders",
      icon: order,
      currentIndex: 2,
      selectedIndex: selectedIndex,
      isNewOrder: isNewOrder,
      chageSelectedIndex: (index) => {
        changeHasNewOrderStatus(false)
        setSelectedIndex(index)
      }
    },
    {
      name: "Users",
      icon: users,
      currentIndex: 3,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
    {
      name: "Stores",
      icon: store,
      currentIndex: 4,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
    {
      name: "Category",
      icon: category,
      currentIndex: 5,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
     {
      name: "Variant",
      icon: varient,
      currentIndex: 6,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
    {
      name: "My Info",
      icon: myInfo,
      currentIndex: 7,
      selectedIndex: selectedIndex,
      isNewOrder: false,
      chageSelectedIndex: setSelectedIndex
    },
  ];

  return navLinkItems;
}

const NavLink = (props: iNavProp) => {
  const navLinkItems = useNavItems(props);

  return (
    <div className="sticky top-0">
      <Image className={`h-24 w-30 object-contain`} src={logo} alt={"logo"} />
      {navLinkItems.map((item, index) => (
        <NavItem key={index} {...item} />
      ))}
    </div>
  );
};

export default NavLink;
