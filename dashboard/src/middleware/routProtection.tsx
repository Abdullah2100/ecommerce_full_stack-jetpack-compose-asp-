import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Util } from "@/util/globle";

interface ComponentWithProps {
  [key: string]: unknown;
}

const withAuth = (Component: React.ComponentType<ComponentWithProps>) => {
  return function WithAuth(props: ComponentWithProps) {
    const router = useRouter();
    const auth = Util.token?.trim() || '';

    useEffect(() => {
      if (auth.length === 0) {
        router.push("/login");
      }
    }, [auth, router]);

    if (auth.length === 0) {
      return null;
    }

    return <Component {...props} />;
  };
};

export default withAuth;
