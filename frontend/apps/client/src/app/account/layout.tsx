"use client";
import { useEffect } from "react";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import { useAppSelector, useAppDispatch } from "@/store/hooks";
import { hydrate, logout } from "@/store/slices/authSlice";
import {
  LayoutDashboard,
  Package,
  MapPin,
  Heart,
  User,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Header } from "@/components/layout/Header";
import { Footer } from "@/components/layout/Footer";
import { Button } from "@/components/ui/button";
import { toast } from "sonner";

const navLinks = [
  { href: "/account", label: "Dashboard", icon: LayoutDashboard, exact: true },
  { href: "/account/orders", label: "My Orders", icon: Package },
  { href: "/account/addresses", label: "Addresses", icon: MapPin },
  { href: "/account/wishlist", label: "Wishlist", icon: Heart },
  { href: "/account/profile", label: "Profile", icon: User },
];

export default function AccountLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const router = useRouter();
  const pathname = usePathname();
  const dispatch = useAppDispatch();
  const { accessToken, user } = useAppSelector((s) => s.auth);

  useEffect(() => {
    dispatch(hydrate());
  }, [dispatch]);
  useEffect(() => {
    if (typeof window !== "undefined" && !localStorage.getItem("accessToken")) {
      router.push("/login");
    }
  }, [accessToken, router]);

  const handleLogout = () => {
    dispatch(logout());
    toast.success("Logged out");
    router.push("/");
  };

  return (
    <div className="flex flex-col min-h-screen">
      <Header />
      <main className="flex-1 max-w-7xl mx-auto px-4 py-8 w-full">
        <div className="flex gap-8">
          {/* Sidebar */}
          <aside className="w-56 shrink-0">
            <div className="border rounded-xl overflow-hidden bg-white">
              <div className="p-4 border-b bg-gray-50">
                <p className="font-semibold text-sm truncate">
                  {user?.firstName} {user?.lastName}
                </p>
                <p className="text-xs text-gray-500 truncate">{user?.email}</p>
              </div>
              <nav className="p-2 space-y-1">
                {navLinks.map(({ href, label, icon: Icon, exact }) => {
                  const active = exact
                    ? pathname === href
                    : pathname.startsWith(href);
                  return (
                    <Link
                      key={href}
                      href={href}
                      className={cn(
                        "flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm transition-colors",
                        active
                          ? "bg-blue-600 text-white"
                          : "hover:bg-gray-100 text-gray-700",
                      )}
                    >
                      <Icon className="w-4 h-4" /> {label}
                    </Link>
                  );
                })}
                <button
                  onClick={handleLogout}
                  className="flex items-center gap-2.5 px-3 py-2 rounded-lg text-sm w-full text-red-500 hover:bg-red-50 transition-colors"
                >
                  <LogOut className="w-4 h-4" /> Logout
                </button>
              </nav>
            </div>
          </aside>
          <div className="flex-1 min-w-0">{children}</div>
        </div>
      </main>
      <Footer />
    </div>
  );
}
