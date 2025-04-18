import axios from "axios";

export interface Post {
  id: number;
  title: string;
  description: string;
  category: string;
  condition: string;
  email: string;
  itemPostImageUrl: string;
  userImageUrl?: string;
  userName?: string;
  createdAt?: string;
  address?: string;
}

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

const axiosInstance = axios.create({
  baseURL: API_BASE_URL,
  headers: { "Content-Type": "application/json" },
});

export const fetchPosts = async (): Promise<Post[]> => {
  try {
    const response = await axiosInstance.get<{ data: Post[] }>("/items/all-posts");
    const posts = response.data.data;

    // Map to store fetched user data
    const userRequests: Record<string, Promise<{ profileImageURL: string; firstName: string; lastName: string }>> = {};

    const updatedPosts = await Promise.all(
      posts.map(async (post) => {
        if (!userRequests[post.email]) {
          // Initiate a single request for the email if not already started
          userRequests[post.email] = axiosInstance
            .get<{ data: { profileImageURL: string; firstName: string; lastName: string } }>(`/users/${post.email}`)
            .then((response) => response.data.data)
            .catch((error) => {
              console.error("Error fetching user data for:", post.email, error);
              return { profileImageURL: "", firstName: "", lastName: "" };
            });
        }

        const userData = await userRequests[post.email];
        const userName = `${userData.firstName || ""} ${userData.lastName || ""}`.trim();

        return {
          ...post,
          userImageUrl: userData.profileImageURL || "",
          userName: userName || "Anonymous",
        };
      })
    );

    return updatedPosts;
  } catch (error) {
    handleAxiosError(error, "Error fetching posts");
    return [];
  }
};

export const fetchPostsByEmail = async (email: string): Promise<Post[]> => {
  try {
    const response = await axiosInstance.get<{ data: Post[] }>("/items/user-posts", {
      params: { email },
    });
    const posts = response.data.data;

    const userRequests: Record<string, Promise<{ profileImageURL: string; firstName: string; lastName: string }>> = {};

    const updatedPosts = await Promise.all(
      posts.map(async (post) => {
        if (!userRequests[post.email]) {
          // Initiate a single request for the email if not already started
          userRequests[post.email] = axiosInstance
            .get<{ data: { profileImageURL: string; firstName: string; lastName: string } }>(`/users/${post.email}`)
            .then((response) => response.data.data)
            .catch((error) => {
              console.error("Error fetching user data for:", post.email, error);
              return { profileImageURL: "", firstName: "", lastName: "" };
            });
        }

        const userData = await userRequests[post.email];
        const userName = `${userData.firstName || ""} ${userData.lastName || ""}`.trim();

        return {
          ...post,
          userImageUrl: userData.profileImageURL || "",
          userName: userName || "Anonymous",
        };
      })
    );

    return updatedPosts;
  } catch (error) {
    handleAxiosError(error, "Error fetching posts by email");
    return [];
  }
};

export const createPost = async (postData: Post): Promise<void> => {
  try {
    await axiosInstance.post("/items/create", postData);
  } catch (error) {
    handleAxiosError(error, "Error creating post");
  }
};

export const fetchCategories = async (): Promise<string[]> => {
  try {
    const response = await axiosInstance.get<{ data: string[] }>("/items/categories");
    return response.data.data;
  } catch (error) {
    handleAxiosError(error, "Error fetching categories");
    return [];
  }
};

export const fetchConditions = async (): Promise<string[]> => {
  try {
    const response = await axiosInstance.get<{ data: string[] }>("/items/conditions");
    return response.data.data;
  } catch (error) {
    handleAxiosError(error, "Error fetching conditions");
    return [];
  }
};

export const getItemById = async (id: number): Promise<Post> => {
  try {
    const response = await axiosInstance.get<{ data: Post }>(`/items/${id}`);
    return response.data.data;
  } catch (error) {
    handleAxiosError(error, "Error fetching item by ID");
    return {} as Post;
  }
};

const handleAxiosError = (error: unknown, defaultMessage: string) => {
  if (axios.isAxiosError(error)) {
    throw new Error(error.response?.data?.message || defaultMessage);
  }
  throw new Error(defaultMessage);
};

export const deletePostById = async (id: number): Promise<void> => {
  const token = localStorage.getItem("authToken");
  await axiosInstance.delete(`/items/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
  });
};

export const updatePost = async (id: number, updatedData: Partial<Post>): Promise<void> => {
  const token = localStorage.getItem("authToken");
  try {
    await axiosInstance.put(`/items/${id}`, updatedData, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  } catch (error) {
    handleAxiosError(error, "Failed to update post");
  }
};
