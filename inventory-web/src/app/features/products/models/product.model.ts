export interface Product {
  id: number;
  name: string;
  description: string | null;
  quantity: number;
  price: number;
  createdAt: string;
  updatedAt: string;
}
