"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useAppDispatch, useAppSelector } from "@/store/hooks";
import { hydrate } from "@/store/slices/authSlice";
import { Sidebar } from "./Sidebar";
import { AdminHeader } from "./AdminHeader";

export function AdminLayout({ children }: { children: React.ReactNode }) {
  const router = useRouter();
  const dispatch = useAppDispatch();
  const { accessToken } = useAppSelector((s) => s.auth);

  useEffect(() => {
    dispatch(hydrate());
  }, [dispatch]);

  useEffect(() => {
    if (typeof window !== "undefined" && !localStorage.getItem("accessToken")) {
      router.push("/auth/login");
    }
  }, [router]);

  return (
    <div className="flex h-screen overflow-hidden bg-gray-50">
      <Sidebar />
      <div className="flex flex-col flex-1 min-w-0 overflow-hidden">
        <AdminHeader />
        <main className="flex-1 overflow-y-auto p-6">{children}</main>
      </div>
    </div>
  );
}
