import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Util } from "@/util/globle";

interface ComponentWithProps {
  [key: string]: unknown;
}

const withAuthRedirect = (Component: React.ComponentType<ComponentWithProps>) => {
  return function WithAuthRedirect(props: ComponentWithProps) {
    const router = useRouter();
    const auth = Util.token?.trim() || '';

    useEffect(() => {
      if (auth.length > 0) {
        router.push("/");
      }
    }, [auth, router]);

    if (auth.length > 0) {
      return null;
    }

    return <Component {...props} />;
  };
};

export default withAuthRedirect;