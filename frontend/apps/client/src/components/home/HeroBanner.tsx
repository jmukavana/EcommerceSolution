"use client";
import { useEffect, useState } from "react";
import Link from "next/link";
import {
  ArrowRight,
  Truck,
  RotateCcw,
  ShieldCheck,
  Headphones,
} from "lucide-react";
import { cn } from "@/lib/utils";

const slides = [
  {
    tag: "Limited Edition",
    title: "New Arrivals",
    titleEm: "Just Landed",
    sub: "Fresh products at unbeatable prices. Shop the latest trends today.",
    ctaLabel: "Shop Now",
    ctaLink: "/shop?sort=createdDate,desc",
  },
  {
    tag: "Best Deals",
    title: "Save Up to",
    titleEm: "60% Today",
    sub: "Top brands, handpicked discounts. Limited time — grab yours now.",
    ctaLabel: "View Deals",
    ctaLink: "/shop?deals=true",
  },
  {
    tag: "Free Shipping",
    title: "On Orders",
    titleEm: "Over $50",
    sub: "Fast & secure delivery straight to your door. No minimum hassle.",
    ctaLabel: "Start Shopping",
    ctaLink: "/shop",
  },
];

const trustItems = [
  { icon: Truck, title: "Free Shipping", sub: "Orders over $50" },
  { icon: RotateCcw, title: "14-Day Returns", sub: "Hassle-free policy" },
  { icon: ShieldCheck, title: "Secure Payments", sub: "100% protected" },
  { icon: Headphones, title: "24/7 Support", sub: "Anywhere & anytime" },
];

export function HeroBanner() {
  const [current, setCurrent] = useState(0);

  useEffect(() => {
    const t = setInterval(
      () => setCurrent((i) => (i + 1) % slides.length),
      4500,
    );
    return () => clearInterval(t);
  }, []);

  const slide = slides[current] ?? slides[0]!;

  return (
    <section className="py-5">
      <div className="max-w-[1280px] mx-auto px-6">
        {/* Hero Grid */}
        <div className="grid grid-cols-1 lg:grid-cols-[1fr_300px] gap-4 mb-4">
          {/* Main Slider */}
          <div className="relative rounded-2xl overflow-hidden min-h-[320px] md:min-h-[360px] flex items-end p-8 md:p-10 bg-[linear-gradient(135deg,#0a0a14_0%,#1a0a2e_55%,#0a1220_100%)]">
            {/* Glow */}
            <div
              className="absolute right-5 top-1/2 -translate-y-1/2 w-[360px] h-[360px] rounded-full pointer-events-none"
              style={{
                background:
                  "radial-gradient(circle, rgba(53,99,233,.18), transparent 70%)",
              }}
            />
            {/* Device silhouette */}
            <div className="absolute right-8 top-1/2 -translate-y-1/2 opacity-[0.08] pointer-events-none hidden lg:block">
              <svg width="160" height="280" viewBox="0 0 200 340" fill="none">
                <rect
                  x="10"
                  y="10"
                  width="180"
                  height="320"
                  rx="32"
                  stroke="#4f8ef7"
                  strokeWidth="2.5"
                />
                <rect
                  x="20"
                  y="20"
                  width="160"
                  height="300"
                  rx="24"
                  stroke="#4f8ef7"
                  strokeWidth="1"
                  strokeDasharray="5 5"
                  opacity=".4"
                />
                <rect
                  x="75"
                  y="24"
                  width="50"
                  height="10"
                  rx="5"
                  fill="#4f8ef7"
                  opacity=".4"
                />
                <circle
                  cx="100"
                  cy="305"
                  r="12"
                  stroke="#4f8ef7"
                  strokeWidth="2"
                  opacity=".4"
                />
              </svg>
            </div>

            {/* Content */}
            <div className="relative z-10 max-w-sm">
              <span className="inline-block text-[10px] font-bold tracking-[2px] uppercase text-[#7eb3ff] bg-[rgba(53,99,233,.15)] px-3 py-1 rounded-full mb-3">
                {slide.tag}
              </span>
              <h1 className="text-3xl md:text-4xl font-extrabold leading-tight tracking-tight text-white mb-3">
                {slide.title}
                <br />
                <em className="not-italic text-[#7eb3ff]">{slide.titleEm}</em>
              </h1>
              <p className="text-[#9999cc] text-sm leading-relaxed mb-6 font-normal">
                {slide.sub}
              </p>
              <Link
                href={slide.ctaLink}
                className="inline-flex items-center gap-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-semibold px-6 py-3 rounded-lg min-h-[44px] transition-all hover:-translate-y-px active:translate-y-0"
              >
                {slide.ctaLabel}
                <ArrowRight className="h-4 w-4" />
              </Link>

              {/* Slide dots */}
              <div className="flex gap-1.5 mt-5">
                {slides.map((_, i) => (
                  <button
                    key={i}
                    onClick={() => setCurrent(i)}
                    aria-label={`Slide ${i + 1}`}
                    className={cn(
                      "h-[3px] rounded-sm border-none transition-all duration-300",
                      i === current
                        ? "w-7 bg-blue-500"
                        : "w-5 bg-white/20 hover:bg-white/40",
                    )}
                  />
                ))}
              </div>
            </div>
          </div>

          {/* Side Promos */}
          <div className="hidden lg:flex flex-col gap-4">
            <Link
              href="/shop?deals=true"
              className="bg-blue-50 rounded-2xl p-6 flex items-center justify-between flex-1 hover:-translate-y-0.5 hover:shadow-md transition-all"
            >
              <div>
                <div className="text-[10px] font-bold tracking-[1.5px] uppercase text-blue-700 mb-1.5">
                  Smart Home
                </div>
                <div className="text-base font-bold text-gray-900 mb-1.5 leading-snug">
                  Smart Security
                  <br />
                  Home Camera
                </div>
                <div className="text-xs text-gray-500">
                  Save up to{" "}
                  <strong className="text-blue-700 text-sm font-bold">
                    $45 off
                  </strong>
                </div>
              </div>
              <div className="text-blue-400 opacity-60 shrink-0">
                <svg
                  width="76"
                  height="76"
                  viewBox="0 0 80 80"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.4"
                  strokeLinecap="round"
                >
                  <circle cx="40" cy="40" r="24" />
                  <circle cx="40" cy="40" r="15" />
                  <circle
                    cx="40"
                    cy="40"
                    r="7"
                    fill="currentColor"
                    fillOpacity=".12"
                  />
                  <line x1="16" y1="40" x2="8" y2="40" />
                  <line x1="64" y1="40" x2="72" y2="40" />
                  <line x1="40" y1="16" x2="40" y2="8" />
                  <line x1="40" y1="64" x2="40" y2="72" />
                </svg>
              </div>
            </Link>

            <Link
              href="/shop?featured=true"
              className="bg-amber-50 rounded-2xl p-6 flex items-center justify-between flex-1 hover:-translate-y-0.5 hover:shadow-md transition-all"
            >
              <div>
                <div className="text-[10px] font-bold tracking-[1.5px] uppercase text-orange-600 mb-1.5">
                  Best Deal
                </div>
                <div className="text-base font-bold text-gray-900 mb-1.5 leading-snug">
                  Featured
                  <br />
                  Products
                </div>
                <div className="text-xs text-gray-500">
                  Save up to{" "}
                  <strong className="text-orange-600 text-sm font-bold">
                    60% off
                  </strong>
                </div>
              </div>
              <div className="text-orange-400 opacity-60 shrink-0">
                <svg
                  width="76"
                  height="76"
                  viewBox="0 0 80 80"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="1.4"
                  strokeLinecap="round"
                >
                  <rect x="22" y="8" width="36" height="64" rx="8" />
                  <line x1="31" y1="16" x2="49" y2="16" />
                  <circle
                    cx="40"
                    cy="62"
                    r="3"
                    fill="currentColor"
                    stroke="none"
                  />
                  <rect
                    x="29"
                    y="24"
                    width="22"
                    height="30"
                    rx="2"
                    fill="currentColor"
                    fillOpacity=".07"
                  />
                </svg>
              </div>
            </Link>
          </div>
        </div>

        {/* Trust Bar */}
        <div
          className="grid grid-cols-2 md:grid-cols-4 border border-gray-200 rounded-xl overflow-hidden bg-white"
          role="list"
          aria-label="Service guarantees"
        >
          {trustItems.map(({ icon: Icon, title, sub }, i) => (
            <div
              key={title}
              role="listitem"
              className={cn(
                "flex items-center gap-3 px-4 py-5",
                i < trustItems.length - 1 && "border-r border-gray-200",
              )}
            >
              <div className="w-10 h-10 rounded-lg bg-blue-50 flex items-center justify-center shrink-0">
                <Icon className="h-5 w-5 text-blue-600" />
              </div>
              <div>
                <p className="text-sm font-semibold text-gray-900">{title}</p>
                <p className="text-xs text-gray-500">{sub}</p>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
