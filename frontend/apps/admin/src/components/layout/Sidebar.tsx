"use client";

import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import {
  LayoutDashboard,
  Package,
  Tag,
  Bookmark,
  ShoppingCart,
  Warehouse,
  Users,
  BarChart3,
  DollarSign,
  Settings,
  Shield,
  ChevronLeft,
  ChevronRight,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { useAppSelector, useAppDispatch } from "@/store/hooks";
import { toggleSidebar } from "@/store/slices/uiSlice";
import { logout } from "@/store/slices/authSlice";
import { useLogout } from "@/services/auth.service";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

const navGroups = [
  {
    label: "Main",
    items: [{ label: "Dashboard", href: "/", icon: LayoutDashboard }],
  },
  {
    label: "Catalog",
    items: [
      { label: "Products", href: "/products", icon: Package },
      { label: "Categories", href: "/categories", icon: Tag },
      { label: "Brands", href: "/brands", icon: Bookmark },
    ],
  },
  {
    label: "Orders",
    items: [
      { label: "Orders", href: "/orders", icon: ShoppingCart },
      { label: "Inventory", href: "/inventory", icon: Warehouse },
    ],
  },
  {
    label: "Users",
    items: [{ label: "Users", href: "/users", icon: Users }],
  },
  {
    label: "Finance",
    items: [
      { label: "Analytics", href: "/analytics", icon: BarChart3 },
      { label: "Reports", href: "/accounting", icon: DollarSign },
    ],
  },
  {
    label: "Settings",
    items: [{ label: "Settings", href: "/settings", icon: Settings }],
  },
];

export function Sidebar() {
  const pathname = usePathname();
  const dispatch = useAppDispatch();
  const router = useRouter();
  const collapsed = useAppSelector((state) => state.ui.sidebarCollapsed);
  const user = useAppSelector((state) => state.auth.user);
  const { mutate: doLogout } = useLogout();

  const handleLogout = () => {
    doLogout(undefined, {
      onSettled: () => {
        dispatch(logout());
        router.push("/auth/login");
        toast.success("Logged out successfully");
      },
    });
  };

  const isActive = (href: string) => {
    if (href === "/") return pathname === "/";
    return pathname.startsWith(href);
  };

  return (
    <aside
      className={cn(
        "flex flex-col h-screen bg-slate-900 text-white transition-all duration-300 shrink-0",
        collapsed ? "w-16" : "w-64",
      )}
    >
      {/* Logo */}
      <div
        className={cn(
          "flex items-center gap-3 px-4 py-5 border-b border-slate-700",
          collapsed && "justify-center px-2",
        )}
      >
        <Shield className="size-7 text-indigo-400 shrink-0" />
        {!collapsed && (
          <Link
            href="/"
            className="text-lg font-bold tracking-tight text-white"
          >
            AdminPanel
          </Link>
        )}
      </div>

      {/* Navigation */}
      <nav className="flex-1 overflow-y-auto py-4 px-2 space-y-1">
        {navGroups.map((group) => (
          <div key={group.label} className="mb-4">
            {!collapsed && (
              <p className="px-3 mb-1 text-xs font-semibold uppercase tracking-wider text-slate-500">
                {group.label}
              </p>
            )}
            {group.items.map((item) => {
              const Icon = item.icon;
              const active = isActive(item.href);
              return (
                <Link
                  key={item.href}
                  href={item.href}
                  className={cn(
                    "flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium transition-colors",
                    active
                      ? "bg-indigo-600 text-white"
                      : "text-slate-300 hover:bg-slate-700 hover:text-white",
                    collapsed && "justify-center px-2",
                  )}
                  title={collapsed ? item.label : undefined}
                >
                  <Icon className="size-5 shrink-0" />
                  {!collapsed && <span>{item.label}</span>}
                </Link>
              );
            })}
          </div>
        ))}
      </nav>

      {/* User info */}
      <div
        className={cn(
          "border-t border-slate-700 p-3",
          collapsed ? "flex flex-col items-center gap-2" : "",
        )}
      >
        {!collapsed && user && (
          <div className="flex items-center gap-3 mb-3 px-1">
            <Avatar size="sm">
              <AvatarFallback className="bg-indigo-600 text-white text-xs">
                {user.firstName?.[0]}
                {user.lastName?.[0]}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-white truncate">
                {user.firstName} {user.lastName}
              </p>
              <p className="text-xs text-slate-400 truncate">{user.email}</p>
            </div>
          </div>
        )}
        <div
          className={cn(
            "flex items-center",
            collapsed ? "flex-col gap-2" : "justify-between",
          )}
        >
          <Button
            variant="ghost"
            size="icon-sm"
            className="text-slate-400 hover:text-white hover:bg-slate-700"
            onClick={handleLogout}
            title="Logout"
          >
            <LogOut className="size-4" />
          </Button>
          <Button
            variant="ghost"
            size="icon-sm"
            className="text-slate-400 hover:text-white hover:bg-slate-700"
            onClick={() => dispatch(toggleSidebar())}
            title={collapsed ? "Expand sidebar" : "Collapse sidebar"}
          >
            {collapsed ? (
              <ChevronRight className="size-4" />
            ) : (
              <ChevronLeft className="size-4" />
            )}
          </Button>
        </div>
      </div>
    </aside>
  );
}
