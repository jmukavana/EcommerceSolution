import Link from "next/link";
import { Separator } from "@/components/ui/separator";

const socials = [
  {
    label: "Facebook",
    href: "https://facebook.com",
    icon: (
      <svg
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="currentColor"
        aria-hidden="true"
      >
        <path d="M18 2h-3a5 5 0 0 0-5 5v3H7v4h3v8h4v-8h3l1-4h-4V7a1 1 0 0 1 1-1h3z" />
      </svg>
    ),
  },
  {
    label: "Twitter / X",
    href: "https://twitter.com",
    icon: (
      <svg
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="currentColor"
        aria-hidden="true"
      >
        <path d="M18.244 2.25h3.308l-7.227 8.26 8.502 11.24H16.17l-5.214-6.817L4.99 21.75H1.68l7.73-8.835L1.254 2.25H8.08l4.713 6.231zm-1.161 17.52h1.833L7.084 4.126H5.117z" />
      </svg>
    ),
  },
  {
    label: "Instagram",
    href: "https://instagram.com",
    icon: (
      <svg
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        strokeWidth="2"
        strokeLinecap="round"
        aria-hidden="true"
      >
        <rect x="2" y="2" width="20" height="20" rx="5" />
        <circle cx="12" cy="12" r="4" />
        <circle cx="17.5" cy="6.5" r="0.5" fill="currentColor" />
      </svg>
    ),
  },
  {
    label: "YouTube",
    href: "https://youtube.com",
    icon: (
      <svg
        width="16"
        height="16"
        viewBox="0 0 24 24"
        fill="currentColor"
        aria-hidden="true"
      >
        <path d="M22.54 6.42a2.78 2.78 0 0 0-1.95-1.96C18.88 4 12 4 12 4s-6.88 0-8.59.46a2.78 2.78 0 0 0-1.95 1.96A29 29 0 0 0 1 12a29 29 0 0 0 .46 5.58a2.78 2.78 0 0 0 1.95 1.96C5.12 20 12 20 12 20s6.88 0 8.59-.46a2.78 2.78 0 0 0 1.95-1.96A29 29 0 0 0 23 12a29 29 0 0 0-.46-5.58zM9.75 15.02V8.98L15.5 12l-5.75 3.02z" />
      </svg>
    ),
  },
];

const paymentMethods = ["Visa", "Mastercard", "PayPal", "M-Pesa", "Cash"];

export function Footer() {
  return (
    <footer className="bg-gray-900 text-gray-400 mt-auto">
      <div className="max-w-[1280px] mx-auto px-6 pt-12 pb-0">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-[2fr_1fr_1fr_1fr_1.5fr] gap-8 mb-10">
          {/* Brand */}
          <div>
            <Link
              href="/"
              className="text-xl font-extrabold text-white mb-3 inline-block"
            >
              Shop<span className="text-blue-500">Now</span>
            </Link>
            <p className="text-xs leading-relaxed mb-4">
              Your one-stop destination for amazing products at unbeatable
              prices. Shop with confidence.
            </p>
            <div className="flex items-center gap-2">
              {socials.map((s) => (
                <a
                  key={s.label}
                  href={s.href}
                  target="_blank"
                  rel="noopener noreferrer"
                  aria-label={s.label}
                  className="w-8 h-8 rounded-lg bg-white/[0.06] border border-white/[0.08] flex items-center justify-center text-gray-400 hover:bg-white/[0.12] transition-colors"
                >
                  {s.icon}
                </a>
              ))}
            </div>
          </div>

          {/* Shop */}
          <div>
            <h4 className="text-[11px] font-bold tracking-[1px] uppercase text-white mb-4">
              Shop
            </h4>
            <ul className="space-y-2">
              {[
                { label: "All Products", href: "/shop" },
                { label: "Deals & Offers", href: "/shop?deals=true" },
                { label: "New Arrivals", href: "/shop?sort=createdDate,desc" },
                { label: "Featured Products", href: "/shop?featured=true" },
              ].map((l) => (
                <li key={l.label}>
                  <Link
                    href={l.href}
                    className="text-xs hover:text-white transition-colors"
                  >
                    {l.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Account */}
          <div>
            <h4 className="text-[11px] font-bold tracking-[1px] uppercase text-white mb-4">
              Account
            </h4>
            <ul className="space-y-2">
              {[
                { label: "Login", href: "/auth/login" },
                { label: "Register", href: "/auth/register" },
                { label: "My Orders", href: "/account/orders" },
                { label: "Wishlist", href: "/account/wishlist" },
              ].map((l) => (
                <li key={l.label}>
                  <Link
                    href={l.href}
                    className="text-xs hover:text-white transition-colors"
                  >
                    {l.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Help */}
          <div>
            <h4 className="text-[11px] font-bold tracking-[1px] uppercase text-white mb-4">
              Help
            </h4>
            <ul className="space-y-2">
              {[
                { label: "Contact Us", href: "/" },
                { label: "FAQ", href: "/" },
                { label: "Returns & Refunds", href: "/" },
                { label: "Shipping Info", href: "/" },
              ].map((l) => (
                <li key={l.label}>
                  <Link
                    href={l.href}
                    className="text-xs hover:text-white transition-colors"
                  >
                    {l.label}
                  </Link>
                </li>
              ))}
            </ul>
          </div>

          {/* Download App */}
          <div>
            <h4 className="text-[11px] font-bold tracking-[1px] uppercase text-white mb-4">
              Get the App
            </h4>
            <p className="text-xs mb-3 leading-relaxed">
              Shop on the go with our mobile app. Available on iOS and Android.
            </p>
            <button className="flex items-center gap-3 bg-white/[0.06] border border-white/[0.08] rounded-lg px-4 py-3 mb-2 hover:bg-white/10 transition-colors w-full text-left">
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="currentColor"
                className="text-white shrink-0"
                aria-hidden="true"
              >
                <path d="M18.71 19.5c-.83 1.24-1.71 2.45-3.05 2.47-1.34.03-1.77-.79-3.29-.79-1.53 0-2 .77-3.27.82-1.31.05-2.3-1.32-3.14-2.53C4.25 17 2.94 12.45 4.7 9.39c.87-1.52 2.43-2.48 4.12-2.51 1.28-.02 2.5.87 3.29.87.78 0 2.26-1.07 3.8-.91.65.03 2.47.26 3.64 1.98-.09.06-2.17 1.28-2.15 3.81.03 3.02 2.65 4.03 2.68 4.04-.03.07-.42 1.44-1.38 2.83M13 3.5c.73-.83 1.94-1.46 2.94-1.5.13 1.17-.34 2.35-1.04 3.19-.69.85-1.83 1.51-2.95 1.42-.15-1.15.41-2.35 1.05-3.11z" />
              </svg>
              <div>
                <span className="block text-[9px] text-gray-400">
                  Download on the
                </span>
                <span className="block text-sm font-semibold text-white">
                  App Store
                </span>
              </div>
            </button>
            <button className="flex items-center gap-3 bg-white/[0.06] border border-white/[0.08] rounded-lg px-4 py-3 hover:bg-white/10 transition-colors w-full text-left">
              <svg
                width="20"
                height="20"
                viewBox="0 0 24 24"
                fill="currentColor"
                className="text-white shrink-0"
                aria-hidden="true"
              >
                <path d="M3.18 23.76c.31.17.67.17.99.02L15.55 12 3.18.22C2.86.07 2.5.07 2.19.24 1.87.4 1.67.74 1.67 1.1v21.8c0 .36.2.7.51.86zM16.54 13l2.56 2.56-11.37 6.43L16.54 13zm2.56-4.56L16.54 11 8.73 2.01l10.37 5.87a.97.97 0 0 1 0 .56zM21.5 10.5l-2.77-1.57L21.5 6.9V10.5z" />
              </svg>
              <div>
                <span className="block text-[9px] text-gray-400">
                  Get it on
                </span>
                <span className="block text-sm font-semibold text-white">
                  Google Play
                </span>
              </div>
            </button>
          </div>
        </div>

        <Separator className="bg-white/[0.07]" />

        <div className="flex flex-col md:flex-row items-center justify-between gap-4 py-5">
          <p className="text-xs text-gray-500">
            &copy; 2026 ShopNow. All rights reserved.
          </p>
          <div className="flex items-center gap-2">
            <span className="text-[10px] text-gray-500">We accept:</span>
            {paymentMethods.map((m) => (
              <span
                key={m}
                className="bg-white/[0.08] rounded px-2.5 py-1 text-[9px] font-bold text-white tracking-[0.5px]"
              >
                {m}
              </span>
            ))}
          </div>
        </div>
      </div>
    </footer>
  );
}
