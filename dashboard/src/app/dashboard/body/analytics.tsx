import React from 'react';
import { DollarSign, Users, ShoppingBag, Package, MapPin } from "lucide-react";
import { Card } from '@/components/ui/card/card';
import { useQuery } from '@tanstack/react-query';
import { getAnalyse } from '@/lib/api/analyse';

const Analytics = () => {
 const { data, isLoading, isError } = useQuery({

        queryKey: ["analytics"],
        queryFn: () => getAnalyse(),

    });

    const stats = [
        {
            title: "Total Revenue",
            value: `$${data?.totalFee.toLocaleString()}`,
            change: "+20.1% from last month",
            icon: DollarSign,
            color: "text-green-500",
            bg: "bg-green-500/10"
        },
        {
            title: "Total Orders",
            value: data?.totalOrders.toLocaleString(),
            change: "+180.1% from last month",
            icon: ShoppingBag,
            color: "text-blue-500",
            bg: "bg-blue-500/10"
        },
        {
            title: "Total Users",
            value: data?.usersCount.toLocaleString(),
            change: "+19% from last month",
            icon: Users,
            color: "text-orange-500",
            bg: "bg-orange-500/10"
        },
        {
            title: "Total Delivery Distance",
            value: data?.totalDeliveryDistance.toLocaleString(),
            change: "+201 since last hour",
            icon: MapPin,
            color: "text-purple-500",
            bg: "bg-purple-500/10"
        },
        {
            title: "Total Product",
            value: data?.productCount.toLocaleString(),
            change: "+201 since last hour",
            icon: Package,
            color: "text-purple-500",
            bg: "bg-purple-500/10"
        },
    ];

   

    if (isLoading || isError) return null;


    const anylyseByIndex = (index: number) => {
        switch (index) {
            case 0: return data?.totalFee
            case 1: return data?.totalOrders
            case 2: return data?.usersCount
            case 3: return data?.totalDeliveryDistance
            case 4: return data?.productCount
        }
    }


    return (
        <div className="flex flex-col w-full h-full space-y-6 p-6 animate-in fade-in duration-500">
            <h1 className="text-3xl font-bold tracking-tight bg-gradient-to-r from-primary to-indigo-600 bg-clip-text text-transparent">
                Analytics Dashboard
            </h1>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {stats.map((stat, index) => (
                    <Card
                        key={index}
                        title={stat.title}
                        additionalIcon={
                            <div className={`p-2 rounded-full ${stat.bg}`}>
                                <stat.icon className={`h-4 w-4 ${stat.color}`} />
                            </div>}
                        content={<div className="text-2xl font-bold">{anylyseByIndex(index)}</div>}
                    />
                ))}
            </div>
        </div>
    );
};

export default Analytics;
