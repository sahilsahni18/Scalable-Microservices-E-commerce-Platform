import { Product, Category, Review, ApiResponse, PaginatedResponse, SearchFilters } from './types';

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'https://api.example.com';

class ApiClient {
  private async request<T>(
    endpoint: string,
    options: RequestInit = {}
  ): Promise<ApiResponse<T>> {
    try {
      const url = `${API_BASE_URL}${endpoint}`;
      const config: RequestInit = {
        headers: {
          'Content-Type': 'application/json',
          ...options.headers,
        },
        ...options,
      };

      // Add auth token if available
      const token = localStorage.getItem('auth-token');
      if (token) {
        config.headers = {
          ...config.headers,
          Authorization: `Bearer ${token}`,
        };
      }

      const response = await fetch(url, config);
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const data = await response.json();
      return data;
    } catch (error) {
      console.error('API request failed:', error);
      return {
        data: null as T,
        success: false,
        error: error instanceof Error ? error.message : 'Unknown error occurred',
      };
    }
  }

  // Products
  async getProducts(
    page = 1,
    limit = 12,
    filters?: SearchFilters
  ): Promise<PaginatedResponse<Product>> {
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });

    if (filters) {
      if (filters.category) params.append('category', filters.category);
      if (filters.brand) filters.brand.forEach(b => params.append('brand', b));
      if (filters.priceRange) {
        params.append('minPrice', filters.priceRange[0].toString());
        params.append('maxPrice', filters.priceRange[1].toString());
      }
      if (filters.rating) params.append('rating', filters.rating.toString());
      if (filters.inStock) params.append('inStock', 'true');
      if (filters.sortBy) params.append('sortBy', filters.sortBy);
    }

    return this.request<Product[]>(`/products?${params.toString()}`) as Promise<PaginatedResponse<Product>>;
  }

  async getProduct(id: string): Promise<ApiResponse<Product>> {
    return this.request<Product>(`/products/${id}`);
  }

  async getFeaturedProducts(): Promise<ApiResponse<Product[]>> {
    return this.request<Product[]>('/products/featured');
  }

  async getNewProducts(): Promise<ApiResponse<Product[]>> {
    return this.request<Product[]>('/products/new');
  }

  async getRelatedProducts(productId: string): Promise<ApiResponse<Product[]>> {
    return this.request<Product[]>(`/products/${productId}/related`);
  }

  async searchProducts(query: string, filters?: SearchFilters): Promise<PaginatedResponse<Product>> {
    const params = new URLSearchParams({ q: query });
    
    if (filters) {
      if (filters.category) params.append('category', filters.category);
      if (filters.sortBy) params.append('sortBy', filters.sortBy);
    }

    return this.request<Product[]>(`/products/search?${params.toString()}`) as Promise<PaginatedResponse<Product>>;
  }

  // Categories
  async getCategories(): Promise<ApiResponse<Category[]>> {
    return this.request<Category[]>('/categories');
  }

  async getCategory(slug: string): Promise<ApiResponse<Category>> {
    return this.request<Category>(`/categories/${slug}`);
  }

  async getFeaturedCategories(): Promise<ApiResponse<Category[]>> {
    return this.request<Category[]>('/categories/featured');
  }

  // Reviews
  async getProductReviews(
    productId: string,
    page = 1,
    limit = 10
  ): Promise<PaginatedResponse<Review>> {
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });

    return this.request<Review[]>(`/products/${productId}/reviews?${params.toString()}`) as Promise<PaginatedResponse<Review>>;
  }

  async addReview(review: Omit<Review, 'id' | 'createdAt' | 'helpfulCount'>): Promise<ApiResponse<Review>> {
    return this.request<Review>('/reviews', {
      method: 'POST',
      body: JSON.stringify(review),
    });
  }

  // Newsletter
  async subscribeNewsletter(email: string): Promise<ApiResponse<{ message: string }>> {
    return this.request<{ message: string }>('/newsletter/subscribe', {
      method: 'POST',
      body: JSON.stringify({ email }),
    });
  }
}

export const api = new ApiClient();

// Mock data for development
export const mockProducts: Product[] = [
  {
    id: '1',
    name: 'Premium Wireless Headphones',
    description: 'Experience crystal-clear audio with our premium wireless headphones featuring active noise cancellation and 30-hour battery life.',
    price: 299.99,
    originalPrice: 399.99,
    images: [
      'https://images.pexels.com/photos/3394650/pexels-photo-3394650.jpeg',
      'https://images.pexels.com/photos/1649771/pexels-photo-1649771.jpeg',
    ],
    category: 'Electronics',
    subcategory: 'Audio',
    brand: 'AudioTech',
    rating: 4.8,
    reviewCount: 1247,
    stock: 45,
    features: ['Active Noise Cancellation', 'Wireless Charging', '30hr Battery', 'Premium Sound'],
    tags: ['headphones', 'wireless', 'audio', 'premium'],
    isFeatured: true,
    isNew: false,
    createdAt: '2024-01-15T10:00:00Z',
    updatedAt: '2024-01-20T14:30:00Z',
  },
  {
    id: '2',
    name: 'Smart Fitness Watch',
    description: 'Track your health and fitness goals with our advanced smartwatch featuring heart rate monitoring, GPS, and 7-day battery life.',
    price: 199.99,
    images: [
      'https://images.pexels.com/photos/437037/pexels-photo-437037.jpeg',
      'https://images.pexels.com/photos/1127130/pexels-photo-1127130.jpeg',
    ],
    category: 'Electronics',
    subcategory: 'Wearables',
    brand: 'FitTech',
    rating: 4.6,
    reviewCount: 892,
    stock: 78,
    features: ['Heart Rate Monitor', 'GPS Tracking', '7-day Battery', 'Water Resistant'],
    tags: ['smartwatch', 'fitness', 'health', 'wearable'],
    isFeatured: true,
    isNew: true,
    createdAt: '2024-01-10T08:00:00Z',
    updatedAt: '2024-01-18T16:45:00Z',
  },
  {
    id: '3',
    name: 'Professional Camera Lens',
    description: 'Capture stunning photos with this professional-grade camera lens featuring optical image stabilization and weather sealing.',
    price: 899.99,
    originalPrice: 1099.99,
    images: [
      'https://images.pexels.com/photos/190819/pexels-photo-190819.jpeg',
      'https://images.pexels.com/photos/1983037/pexels-photo-1983037.jpeg',
    ],
    category: 'Electronics',
    subcategory: 'Photography',
    brand: 'LensMaster',
    rating: 4.9,
    reviewCount: 456,
    stock: 23,
    features: ['Optical Stabilization', 'Weather Sealed', 'Ultra Sharp', 'Professional Grade'],
    tags: ['camera', 'lens', 'photography', 'professional'],
    isFeatured: true,
    isNew: false,
    createdAt: '2024-01-05T12:00:00Z',
    updatedAt: '2024-01-15T09:30:00Z',
  },
];

export const mockCategories: Category[] = [
  {
    id: '1',
    name: 'Electronics',
    slug: 'electronics',
    description: 'Latest technology and electronic devices',
    image: 'https://images.pexels.com/photos/356056/pexels-photo-356056.jpeg',
    productCount: 1247,
    isFeatured: true,
  },
  {
    id: '2',
    name: 'Fashion',
    slug: 'fashion',
    description: 'Trendy clothing and accessories',
    image: 'https://images.pexels.com/photos/996329/pexels-photo-996329.jpeg',
    productCount: 892,
    isFeatured: true,
  },
  {
    id: '3',
    name: 'Home & Garden',
    slug: 'home-garden',
    description: 'Everything for your home and garden',
    image: 'https://images.pexels.com/photos/1571460/pexels-photo-1571460.jpeg',
    productCount: 634,
    isFeatured: true,
  },
];