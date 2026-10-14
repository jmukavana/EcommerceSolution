import Link from "next/link";
import { cn } from "@/lib/utils";

const banners = [
  {
    eyebrow: "Exclusive Offer",
    title: "UP TO 30% OFF",
    desc: "Top electronics at unbeatable prices. Limited time only.",
    cta: "Buy Now →",
    href: "/shop?deals=true",
    eyebrowColor: "text-purple-400",
    gradient: "from-[#0d0d1a] to-[#2d1659]",
  },
  {
    eyebrow: "New Arrivals",
    title: "Up to 40% off",
    desc: "Just landed — fresh picks from top brands.",
    cta: "Buy Now →",
    href: "/shop?sort=createdDate,desc",
    eyebrowColor: "text-blue-400",
    gradient: "from-[#0a1628] to-[#0f3460]",
  },
  {
    eyebrow: "Featured Deals",
    title: "Flat 20% off",
    desc: "Handpicked products for the best value.",
    cta: "Shop Now →",
    href: "/shop?featured=true",
    eyebrowColor: "text-green-400",
    gradient: "from-[#0f1a0a] to-[#1a3a0f]",
  },
];

export function PromoSection() {
  return (
    <section className="py-8">
      <div className="max-w-[1280px] mx-auto px-6">
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {banners.map((b) => (
            <Link
              key={b.title}
              href={b.href}
              className={cn(
                "rounded-2xl p-8 relative overflow-hidden min-h-[180px] flex flex-col justify-end",
                "bg-gradient-to-br cursor-pointer hover:-translate-y-0.5 transition-transform",
                b.gradient,
              )}
            >
              <div
                className={cn(
                  "text-[10px] font-bold tracking-[2px] uppercase mb-1.5",
                  b.eyebrowColor,
                )}
              >
                {b.eyebrow}
              </div>
              <h3 className="text-lg font-extrabold text-white mb-1">
                {b.title}
              </h3>
              <p className="text-xs text-white/60 mb-4">{b.desc}</p>
              <span className="inline-flex items-center gap-1.5 bg-white text-gray-900 text-xs font-semibold px-4 py-2 rounded-lg min-h-[36px] w-fit hover:bg-gray-100 transition-colors">
                {b.cta}
              </span>
            </Link>
          ))}
        </div>
      </div>
    </section>
  );
}
