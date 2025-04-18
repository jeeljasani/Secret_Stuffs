import axios from "axios";

export interface Donation {
  id: number;
  createdAt: Date | null;
  updatedAt: Date | null;
  status: string | null;
  user: User;
  itemPost: ItemPost;
}

export type Status = "PENDING" | "ACCEPTED" | "REJECTED";

export interface User {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  profileImageURL: string;
  active: boolean;
  createdAt: Date;
  updatedAt: Date;
  status?: Status;
}

export interface ItemPost {
  id: number;
  email: string;
  title: string;
  address: string;
  description: string;
  category: string;
  itemPostImageUrl: string;
  condition: string;
}

interface ApiResponse<T> {
  message: string;
  statusCode: number;
  data: T;
  timestamp: Date;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export class DonationService {
  static async createDonation(
    itemPostId: string,
    userId: string
  ): Promise<ApiResponse<Donation>> {
    try {
      const response = await axios.post(`${API_BASE_URL}/donations/`, {
        itemPostId,
        userId,
      });
      return response.data;
    } catch (error) {
      throw new Error(`Failed to create donation: ${error}`);
    }
  }

  static async getInterestUsers(itemId: number): Promise<ApiResponse<Donation[]>> {
    try {
      const response = await axios.get(`${API_BASE_URL}/donations/item/${itemId}`);
      return response.data
    }
    catch (error) {
      throw new Error(`Failed to load interested users: ${error}`);
    }
  }

  static async donate(userId: number, itemPostId: number): Promise<ApiResponse<Donation>> {
    try {
      const response = await axios.post(`${API_BASE_URL}/donations/donate`, { userId, itemPostId });
      return response.data;
    }
    catch (error) {
      throw new Error(`Failed to donate: ${error}`);
    }
  }
}
