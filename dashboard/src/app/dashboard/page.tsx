'use client';

import dynamic from 'next/dynamic';
import IsCanNavToDashboard from '../../middleware/isCanNavToDashboard';
import { ReactNode } from 'react';

const DashboardContent = dynamic(() => import('./dashboard'), { ssr: false });

interface DashboardPageProps {
    children?: ReactNode;
}

const DashboardPage = ({}: DashboardPageProps) => {
    return <DashboardContent />;
};

export default IsCanNavToDashboard(DashboardPage);