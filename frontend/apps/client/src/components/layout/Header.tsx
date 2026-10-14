"use client";
import { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter, usePathname } from "next/navigation";
import {
  Search,
  User,
  Heart,
  ShoppingCart,
  Menu,
  X,
  LogOut,
  Package,
  Settings,
  Trash2,
  Plus,
  Minus,
  ChevronDown,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Separator } from "@/components/ui/separator";
import { useAppSelector, useAppDispatch } from "@/store/hooks";
import { logout, hydrate } from "@/store/slices/authSlice";
import { setCart, openCart, closeCart } from "@/store/slices/cartSlice";
import {
  useCart,
  useRemoveCartItem,
  useUpdateCartItem,
} from "@/services/cart.service";
import { useLogout } from "@/services/auth.service";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

const navLinks = [
  { label: "Popular", href: "/" },
  { label: "Shop", href: "/shop" },
  { label: "Deals", href: "/shop?deals=true" },
  { label: "Brands", href: "/shop" },
  { label: "Contact", href: "/" },
];

export function Header() {
  const router = useRouter();
  const pathname = usePathname();
  const dispatch = useAppDispatch();
  const { user, accessToken } = useAppSelector((s) => s.auth);
  const { itemCount, cart, isOpen: cartIsOpen } = useAppSelector((s) => s.cart);
  const [searchQuery, setSearchQuery] = useState("");
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);

  const { data: cartData } = useCart(!!accessToken);
  const removeItem = useRemoveCartItem();
  const updateItem = useUpdateCartItem();
  const logoutMutation = useLogout();

  useEffect(() => {
    dispatch(hydrate());
  }, [dispatch]);
  useEffect(() => {
    if (cartData) dispatch(setCart(cartData));
  }, [cartData, dispatch]);

  useEffect(() => {
    const onScroll = () => setScrolled(window.scrollY > 4);
    window.addEventListener("scroll", onScroll, { passive: true });
    return () => window.removeEventListener("scroll", onScroll);
  }, []);

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      router.push(`/shop?search=${encodeURIComponent(searchQuery.trim())}`);
      setSearchQuery("");
    }
  };

  const handleLogout = async () => {
    try {
      await logoutMutation.mutateAsync();
    } catch {
      /* ignore */
    }
    dispatch(logout());
    router.push("/");
    toast.success("Logged out successfully");
  };

  return (
    <div className="sticky top-0 z-50">
      {/* Top Bar */}
      <div className="bg-gray-900 text-gray-300 text-[11px] py-1.5">
        <div className="max-w-[1280px] mx-auto px-6 flex justify-between items-center">
          <span>
            Free delivery on orders over $50 &middot; Use code{" "}
            <strong className="text-white">SAVE10</strong> for 10% off
          </span>
          <div className="flex items-center gap-4">
            <Link
              href="/account/orders"
              className="hover:text-white transition-colors"
            >
              Track Order
            </Link>
            <Link href="/" className="hover:text-white transition-colors">
              Help
            </Link>
          </div>
        </div>
      </div>

      {/* Main Header */}
      <header
        className={cn(
          "bg-white border-b border-gray-200 transition-shadow duration-200",
          scrolled && "shadow-md",
        )}
      >
        <div className="max-w-[1280px] mx-auto px-6">
          <div className="flex items-center gap-4 py-4">
            {/* Logo */}
            <Link
              href="/"
              className="text-xl font-extrabold tracking-tight text-gray-900 shrink-0"
              aria-label="ShopNow — home"
            >
              Shop<span className="text-blue-600">Now</span>
            </Link>

            {/* All Categories */}
            <button className="hidden md:flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white px-4 py-2.5 rounded-lg text-sm font-semibold shrink-0 min-h-[44px] transition-colors">
              <Menu className="h-4 w-4" />
              All Categories
              <ChevronDown className="h-3 w-3" />
            </button>

            {/* Search */}
            <form
              onSubmit={handleSearch}
              className="hidden md:flex flex-1 relative"
            >
              <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              <input
                type="search"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search products..."
                className="w-full border-[1.5px] border-gray-200 rounded-lg pl-10 pr-24 py-2.5 text-sm outline-none focus:border-blue-500 focus:ring-[3px] focus:ring-blue-500/20 transition-all min-h-[44px] text-gray-900 placeholder:text-gray-400"
              />
              <button
                type="submit"
                className="absolute right-1.5 top-1/2 -translate-y-1/2 bg-blue-600 hover:bg-blue-700 text-white text-xs font-semibold px-3 py-1.5 rounded-md transition-colors min-h-[32px]"
              >
                Search
              </button>
            </form>

            {/* Actions */}
            <nav
              className="flex items-center gap-0.5 ml-auto"
              aria-label="User actions"
            >
              {/* Account */}
              {accessToken && user ? (
                <DropdownMenu>
                  <DropdownMenuTrigger
                    render={
                      <button className="flex flex-col items-center gap-0.5 px-2.5 py-1.5 min-w-[52px] min-h-[44px] justify-center rounded-lg hover:bg-gray-50 text-gray-500 hover:text-gray-900 transition-colors" />
                    }
                  >
                    <Avatar className="h-6 w-6">
                      <AvatarFallback className="bg-blue-100 text-blue-600 text-[9px] font-bold">
                        {user.firstName[0]}
                        {user.lastName[0]}
                      </AvatarFallback>
                    </Avatar>
                    <span className="text-[10px] text-gray-900 font-normal">
                      Account
                    </span>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent align="end" className="w-48">
                    <div className="px-3 py-2">
                      <p className="text-sm font-medium">
                        {user.firstName} {user.lastName}
                      </p>
                      <p className="text-xs text-gray-500 truncate">
                        {user.email}
                      </p>
                    </div>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem onClick={() => router.push("/account")}>
                      <User className="h-4 w-4 mr-2" />
                      Profile
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() => router.push("/account/orders")}
                    >
                      <Package className="h-4 w-4 mr-2" />
                      My Orders
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() => router.push("/account/profile")}
                    >
                      <Settings className="h-4 w-4 mr-2" />
                      Settings
                    </DropdownMenuItem>
                    <DropdownMenuSeparator />
                    <DropdownMenuItem
                      onClick={handleLogout}
                      className="text-red-600 focus:text-red-600"
                    >
                      <LogOut className="h-4 w-4 mr-2" />
                      Logout
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              ) : (
                <Link
                  href="/auth/login"
                  className="hidden md:flex flex-col items-center gap-0.5 px-2.5 py-1.5 min-w-[52px] min-h-[44px] justify-center rounded-lg hover:bg-gray-50 text-gray-500 hover:text-gray-900 transition-colors"
                >
                  <User className="h-5 w-5 text-gray-900" />
                  <span className="text-[10px] text-gray-900 font-normal">
                    Account
                  </span>
                </Link>
              )}

              {/* Wishlist */}
              <Link
                href="/account/wishlist"
                aria-label="Wishlist"
                className="flex flex-col items-center gap-0.5 px-2.5 py-1.5 min-w-[52px] min-h-[44px] justify-center rounded-lg hover:bg-gray-50 transition-colors"
              >
                <Heart className="h-5 w-5 text-gray-900" />
                <span className="text-[10px] text-gray-900 font-normal">
                  Wishlist
                </span>
              </Link>

              {/* Cart */}
              <Sheet
                open={cartIsOpen}
                onOpenChange={(open) =>
                  dispatch(open ? openCart() : closeCart())
                }
              >
                <SheetTrigger
                  render={
                    <button
                      className="flex flex-col items-center gap-0.5 px-2.5 py-1.5 min-w-[52px] min-h-[44px] justify-center rounded-lg hover:bg-gray-50 transition-colors relative"
                      aria-label={`Cart — ${itemCount} items`}
                    />
                  }
                >
                  <span className="relative">
                    <ShoppingCart className="h-5 w-5 text-gray-900" />
                    {itemCount > 0 && (
                      <span className="absolute -top-1.5 -right-2 min-w-[16px] h-4 bg-red-500 text-white rounded-full text-[9px] font-bold flex items-center justify-center px-0.5 border-2 border-white">
                        {itemCount}
                      </span>
                    )}
                  </span>
                  <span className="text-[10px] text-gray-900 font-normal">
                    Cart
                  </span>
                </SheetTrigger>

                <SheetContent className="w-full sm:max-w-md flex flex-col">
                  <SheetHeader>
                    <SheetTitle>Shopping Cart ({itemCount} items)</SheetTitle>
                  </SheetHeader>
                  <div className="flex-1 overflow-y-auto mt-4">
                    {!cart || cart.items.length === 0 ? (
                      <div className="flex flex-col items-center justify-center h-48 gap-3 text-gray-500">
                        <ShoppingCart className="h-12 w-12 text-gray-300" />
                        <p className="text-sm">Your cart is empty</p>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => {
                            dispatch(closeCart());
                            router.push("/shop");
                          }}
                        >
                          Start Shopping
                        </Button>
                      </div>
                    ) : (
                      <div className="space-y-4">
                        {cart.items.map((item) => (
                          <div key={item.id} className="flex gap-3">
                            <div className="w-16 h-16 bg-gray-100 rounded-md overflow-hidden shrink-0">
                              {item.variant?.product?.images?.[0]?.url ? (
                                <img
                                  src={item.variant.product.images[0].url}
                                  alt={item.variant.product.name}
                                  className="w-full h-full object-cover"
                                />
                              ) : (
                                <div className="w-full h-full bg-gray-200" />
                              )}
                            </div>
                            <div className="flex-1 min-w-0">
                              <p className="text-sm font-medium truncate">
                                {item.variant?.product?.name ?? "Product"}
                              </p>
                              <p className="text-xs text-gray-500">
                                {item.variant?.name}
                              </p>
                              <p className="text-sm font-semibold text-blue-600">
                                ${(item.price * item.quantity).toFixed(2)}
                              </p>
                              <div className="flex items-center gap-2 mt-1">
                                <Button
                                  variant="outline"
                                  size="icon"
                                  className="h-6 w-6"
                                  onClick={() =>
                                    item.quantity > 1
                                      ? updateItem.mutate({
                                          id: item.id,
                                          quantity: item.quantity - 1,
                                        })
                                      : removeItem.mutate(item.id)
                                  }
                                >
                                  <Minus className="h-3 w-3" />
                                </Button>
                                <span className="text-sm w-6 text-center">
                                  {item.quantity}
                                </span>
                                <Button
                                  variant="outline"
                                  size="icon"
                                  className="h-6 w-6"
                                  onClick={() =>
                                    updateItem.mutate({
                                      id: item.id,
                                      quantity: item.quantity + 1,
                                    })
                                  }
                                >
                                  <Plus className="h-3 w-3" />
                                </Button>
                                <Button
                                  variant="ghost"
                                  size="icon"
                                  className="h-6 w-6 ml-auto text-red-500 hover:text-red-700"
                                  onClick={() => removeItem.mutate(item.id)}
                                >
                                  <Trash2 className="h-3 w-3" />
                                </Button>
                              </div>
                            </div>
                          </div>
                        ))}
                      </div>
                    )}
                  </div>
                  {cart && cart.items.length > 0 && (
                    <div className="border-t pt-4 mt-4 space-y-3">
                      <div className="flex justify-between text-sm">
                        <span className="text-gray-600">Subtotal</span>
                        <span className="font-semibold">
                          ${cart.subtotal.toFixed(2)}
                        </span>
                      </div>
                      <Button
                        className="w-full bg-blue-600 hover:bg-blue-700"
                        onClick={() => {
                          dispatch(closeCart());
                          router.push("/checkout");
                        }}
                      >
                        Go to Checkout
                      </Button>
                      <Button
                        variant="outline"
                        className="w-full"
                        onClick={() => {
                          dispatch(closeCart());
                          router.push("/cart");
                        }}
                      >
                        View Cart
                      </Button>
                    </div>
                  )}
                </SheetContent>
              </Sheet>

              {/* Mobile Menu Toggle */}
              <button
                className="md:hidden flex flex-col items-center gap-0.5 px-2.5 py-1.5 min-w-[44px] min-h-[44px] justify-center rounded-lg hover:bg-gray-50"
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
              >
                {mobileMenuOpen ? (
                  <X className="h-5 w-5 text-gray-900" />
                ) : (
                  <Menu className="h-5 w-5 text-gray-900" />
                )}
              </button>
            </nav>
          </div>

          {/* Mobile Search */}
          <div className="md:hidden pb-3">
            <form onSubmit={handleSearch} className="relative">
              <Search className="absolute left-3.5 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400 pointer-events-none" />
              <input
                type="search"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search products..."
                className="w-full border-[1.5px] border-gray-200 rounded-lg pl-10 pr-4 py-2.5 text-sm outline-none focus:border-blue-500 transition-all"
              />
            </form>
          </div>
        </div>
      </header>

      {/* Secondary Nav */}
      <nav
        className="hidden md:block bg-white border-b border-gray-200"
        aria-label="Main navigation"
      >
        <div className="max-w-[1280px] mx-auto px-6">
          <div className="flex items-center justify-between h-11">
            <ul className="flex items-center" role="menubar">
              {navLinks.map((link) => (
                <li key={link.label} role="none">
                  <Link
                    href={link.href}
                    role="menuitem"
                    className={cn(
                      "flex items-center px-3 h-11 text-sm border-b-2 transition-colors",
                      pathname === link.href && link.href !== "/"
                        ? "text-blue-600 border-blue-600 font-medium"
                        : link.href === "/" && pathname === "/"
                          ? "text-blue-600 border-blue-600 font-medium"
                          : "text-gray-600 border-transparent hover:text-blue-600 font-normal",
                    )}
                  >
                    {link.label}
                  </Link>
                </li>
              ))}
            </ul>
            <div className="flex items-center gap-2 text-sm font-semibold text-gray-700">
              Best Selling
              <span className="bg-red-500 text-white text-[10px] font-bold px-2 py-0.5 rounded">
                SALE
              </span>
            </div>
          </div>
        </div>
      </nav>

      {/* Mobile Menu */}
      {mobileMenuOpen && (
        <div className="md:hidden bg-white border-t">
          <nav className="max-w-[1280px] mx-auto px-6 py-4 space-y-3">
            {navLinks.map((link) => (
              <Link
                key={link.label}
                href={link.href}
                className="block text-sm font-medium text-gray-700 hover:text-blue-600 py-1"
                onClick={() => setMobileMenuOpen(false)}
              >
                {link.label}
              </Link>
            ))}
            <Separator />
            {accessToken ? (
              <>
                <Link
                  href="/account"
                  className="block text-sm text-gray-700 py-1"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  My Account
                </Link>
                <Link
                  href="/account/orders"
                  className="block text-sm text-gray-700 py-1"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Orders
                </Link>
                <button
                  onClick={handleLogout}
                  className="block text-sm text-red-600 py-1"
                >
                  Logout
                </button>
              </>
            ) : (
              <>
                <Link
                  href="/auth/login"
                  className="block text-sm text-gray-700 py-1"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Login
                </Link>
                <Link
                  href="/auth/register"
                  className="block text-sm text-gray-700 py-1"
                  onClick={() => setMobileMenuOpen(false)}
                >
                  Register
                </Link>
              </>
            )}
          </nav>
        </div>
      )}
    </div>
  );
}
