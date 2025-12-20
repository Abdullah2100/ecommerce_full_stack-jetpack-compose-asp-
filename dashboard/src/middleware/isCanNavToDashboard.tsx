"use client";

import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { Util } from "@/util/globle";

export default function IsCanNavToDashboard(Component: React.ComponentType<any>) {
    return function WithAuth(props: any) {
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
}
