"use client";

import { useEffect, useRef, useState } from "react";
import useOrder from "@/stores/order";
import * as signalR from "@microsoft/signalr";
import Main from "@/app/dashboard/body/main";
import { Sidebar } from "@/components/layout/Sidebar";
import { Header } from "@/components/layout/Header";
import { ToastContainer } from "react-toastify";

interface DashboardContentProps { }

const Dashboard = ({ }: DashboardContentProps) => {
  const [selectedIndex, setSelectedIndex] = useState(0);
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const { isHasNewOrder, changeHasNewOrderStatus } = useOrder();
  const [connection, setConnection] = useState<signalR.HubConnection | undefined>(undefined);
  const audioRef = useRef<HTMLAudioElement>(null);

  const play = () => {
    if (audioRef.current) {
      audioRef.current.pause();
      audioRef.current.currentTime = 0;
      audioRef.current.play();
    }
  };

  useEffect(() => {
    const newConnection = new signalR.HubConnectionBuilder()
      .withUrl(process.env.NEXT_PUBLIC_ORDER_HUB_URL || "")
      .withAutomaticReconnect()
      .build();

    setConnection(newConnection);
  }, []);

  useEffect(() => {
    if (connection != undefined) {
      connection
        .start()
        .then(() => { })
        .catch((e) => { });

      connection.on("createdOrder", (user, message) => {
        changeHasNewOrderStatus(true);
        play();
      });

    }
  }, [connection]);

  useEffect(() => {
  }, [selectedIndex]);

  return (
    <div className="flex min-h-screen bg-background text-foreground">
      <audio ref={audioRef} src="/sound/mixkit-bell-notification-933.wav" />

      {/* Sidebar */}
      <Sidebar
        selectedIndex={selectedIndex}
        setSelectedIndex={(index) => {
          setSelectedIndex(index);
          setIsSidebarOpen(false); // Close sidebar on selection (mobile)
        }}
        isNewOrder={isHasNewOrder}
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
      />

      {/* Main Content Area */}
      <div className="flex flex-1 flex-col overflow-hidden">
        <Header onMenuClick={() => setIsSidebarOpen(true)} />

        <main className="flex-1 overflow-y-auto p-4 lg:p-8">
          <Main currentPage={selectedIndex} />
        </main>
      </div>
      <ToastContainer />
    </div>
  );
};

export default Dashboard;
