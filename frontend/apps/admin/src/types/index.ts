export interface Permission {
  id: string;
  name: string;
}

export interface Role {
  id: string;
  name: string;
  permissions: Permission[];
}

export interface User {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  enabled: boolean;
  locked: boolean;
  createdDate: string;
  roles: Role[];
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
}

export interface Category {
  id: string;
  name: string;
  slug: string;
  imageUrl: string;
  parentId: string | null;
}

export interface Brand {
  id: string;
  name: string;
  slug: string;
  logoUrl: string;
}

export interface ProductVariant {
  id: string;
  sku: string;
  name: string;
  price: number;
  compareAtPrice: number | null;
  isDefault: boolean;
  active: boolean;
  imageUrl: string | null;
}

export interface ProductImage {
  id: string;
  url: string;
  alt: string;
  primary: boolean;
}

export interface Product {
  id: string;
  name: string;
  slug: string;
  description?: string;
  shortDescription?: string;
  metaTitle?: string;
  metaDescription?: string;
  status: "DRAFT" | "PUBLISHED" | "ARCHIVED";
  type: "SIMPLE" | "VARIABLE";
  brand: Brand | null;
  categories: Category[];
  images: ProductImage[];
  variants: ProductVariant[];
  featured: boolean;
  createdDate: string;
  totalReviews: number;
  averageRating: number;
}

export interface OrderItem {
  id: string;
  productName: string;
  variantName: string;
  quantity: number;
  price: number;
}

export interface Order {
  id: string;
  orderNumber: string;
  status:
    | "PENDING"
    | "CONFIRMED"
    | "PROCESSING"
    | "SHIPPED"
    | "DELIVERED"
    | "CANCELLED";
  user: { firstName: string; lastName: string; email: string };
  items: OrderItem[];
  subtotal: number;
  discountAmount: number;
  shippingAmount: number;
  taxAmount: number;
  total: number;
  createdDate: string;
  paymentMethod: string;
  shippingAddress?: {
    street: string;
    city: string;
    state: string;
    country: string;
    postalCode: string;
  };
}

export interface InventoryItem {
  id: string;
  variant: ProductVariant & { product: { id: string; name: string } };
  warehouse: { id: string; name: string };
  quantity: number;
  reservedQuantity: number;
  availableQuantity: number;
  reorderPoint: number;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ApiResponse<T> {
  data: T;
  message: string;
  status: number;
}

export interface DashboardStats {
  totalRevenue: number;
  totalOrders: number;
  totalProducts: number;
  totalUsers: number;
  revenueChange: number;
  ordersChange: number;
}

export interface JournalEntry {
  id: string;
  description: string;
  date: string;
  posted: boolean;
  lines: {
    accountCode: string;
    accountName: string;
    debit: number;
    credit: number;
  }[];
}

export interface Account {
  id: string;
  code: string;
  name: string;
  type: string;
  balance: number;
}
