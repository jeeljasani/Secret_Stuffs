import axios from "axios";

// Interface for profile data
export interface ProfileData {
  email: string;
  profileImageURL?: string;
  firstName?: string;
  lastName?: string;
}

// Omit email from update payload
export type UpdateProfileData = Omit<ProfileData, "email">;

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

// Axios instance with default headers
const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

// Fetch user profile by email
export const getUserProfile = async (email: string): Promise<ProfileData> => {
  try {
    const response = await axiosInstance.get<{ data: ProfileData }>(
      `/users/${encodeURIComponent(email)}` // Use `encodeURIComponent` to handle special characters in email
    );
    return response.data.data;
  } catch (error) {
    handleAxiosError(error, "Error fetching user profile");
    return {} as ProfileData;
  }
};

// Update user profile
export const updateUserProfile = async (
  email: string,
  profileData: UpdateProfileData
): Promise<ProfileData> => {
  try {
    const response = await axiosInstance.put<{ data: ProfileData }>(
      `/users/update`,
      profileData,
      {
        params: { email }, // Use `params` to send the email as a query parameter
      }
    );
    return response.data.data;
  } catch (error) {
    handleAxiosError(error, "Error updating user profile");
    return {} as ProfileData;
  }
};

// Centralized error handler for Axios requests
const handleAxiosError = (error: unknown, defaultMessage: string) => {
  if (axios.isAxiosError(error)) {
    console.error(defaultMessage, error.response?.data || error.message);
    throw new Error(error.response?.data?.message || defaultMessage);
  } else {
    console.error(defaultMessage, error);
    throw new Error(defaultMessage);
  }
};