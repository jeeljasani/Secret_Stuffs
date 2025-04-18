import axios, { AxiosError } from "axios";
import Cookies from "js-cookie";
import { message } from "antd";

interface ApiResponseDTO<T> {
  data: T;
  message: string;
  statusCode: number;
}

interface AuthResponseData {
  id: number;
  email: string;
  firstName?: string; // Added
  lastName?: string; // Added
  expiresAt: number;
  token: string;
  address?: string | null;
  profileImageURL?: string | null;
  active?: boolean;
}

interface AuthResponse {
  data: AuthResponseData;
  message: string;
  statusCode: number;
}

const apiUrl = import.meta.env.VITE_API_BASE_URL;

const tokenKey = "__auth_token__";
const userKey = "__auth_user__";
const expiresAtKey = "__auth_expiresAt__";

export const getToken = () => Cookies.get(tokenKey);

export const handleUserResponse = (response: AuthResponseData) => {
  const { token, expiresAt } = response;

  Cookies.set(tokenKey, token, { expires: 7 });
  Cookies.set(userKey, JSON.stringify(response), { expires: 7 });
  Cookies.set(expiresAtKey, expiresAt.toString(), { expires: 7 });

  window.localStorage.setItem(tokenKey, token);
  window.localStorage.setItem(userKey, JSON.stringify(response));
  window.localStorage.setItem(expiresAtKey, expiresAt.toString());

  return response;
};

export const login = async (data: { email: string; password: string }) => {
  try {
    const response = await axios.post<AuthResponse>(
      `${apiUrl}/auth/login`,
      data
    );
    return handleUserResponse(response.data.data);
  } catch (error: unknown) {
    if (isAxiosErrorWithMessage(error)) {
      if (error.response?.data.message === "USER_NOT_VERIFIED") {
        throw new Error("User is not verified.");
      }
      throw new Error(error.response?.data?.message || "Login failed");
    }
    console.error("Login error:", error);
    throw new Error("Login failed. Please try again.");
  }
};

export const register = async (data: {
  firstName: string; // Added
  lastName: string; // Added
  email: string;
  password: string;
}) => {
  try {
    const response = await axios.post<AuthResponse>(
      `${apiUrl}/auth/register`,
      data
    );
    message.success(
      "Signup successful! Please check your email to verify your account."
    );
    return handleUserResponse(response.data.data);
  } catch (error: unknown) {
    if (isAxiosErrorWithMessage(error)) {
      if (error.response?.data.message === "USER_ALREADY_EXISTS") {
        message.error(
          "This email is already registered. Please login or use a different email."
        );
      } else {
        message.error(
          error.response?.data?.message || "Registration failed. Please try again."
        );
      }
    } else {
      console.error("Registration error:", error);
      message.error("Registration failed. Please try again.");
    }
    throw error;
  }
};

export const logout = async () => {
  try {
    Cookies.remove(tokenKey);
    Cookies.remove(userKey);
    Cookies.remove(expiresAtKey);

    window.localStorage.removeItem(tokenKey);
    window.localStorage.removeItem(userKey);
    window.localStorage.removeItem(expiresAtKey);
  } catch (error: unknown) {
    console.error("Logout error:", error);
    throw new Error("Logout failed. Please try again.");
  }
};

export const resendVerificationEmail = async (email: string) => {
  try {
    const response = await axios.post(
      `${apiUrl}/auth/resend-verification-email`,
      null,
      {
        params: { email },
      }
    );
    return response.data.message || "Verification email sent successfully!";
  } catch (error: unknown) {
    if (isAxiosErrorWithMessage(error)) {
      throw new Error(
        error.response?.data.message || "Failed to resend verification email"
      );
    }
    console.error("Resend verification error:", error);
    throw new Error("Failed to resend verification email. Please try again.");
  }
};

export const forgotPassword = async (email: string) => {
  try {
    const response = await axios.post<ApiResponseDTO<string>>(
      `${apiUrl}/auth/forgot-password`,
      null,
      {
        params: { email },
      }
    );
    return response.data.data;
  } catch (error) {
    if (isAxiosErrorWithMessage(error)) {
      throw new Error(
        error.response?.data.message || "Failed to send password reset link"
      );
    }
    console.error("Forgot password error:", error);
    throw new Error("Failed to send password reset link. Please try again.");
  }
};

export const resetPassword = async (
  token: string,
  newPassword: string,
  confirmPassword: string
) => {
  try {
    const response = await axios.post<ApiResponseDTO<string>>(
      `${apiUrl}/auth/reset-password`,
      null,
      {
        params: {
          token,
          newPassword,
          confirmPassword,
        },
        headers: {
          "Content-Type": "application/json",
        },
      }
    );

    if (response.data.statusCode === 200) {
      return response.data.data;
    }
    throw new Error(response.data.message || "Failed to reset password");
  } catch (error) {
    if (isAxiosErrorWithMessage(error)) {
      throw new Error(
        error.response?.data.message || "Failed to reset password"
      );
    }
    console.error("Reset password error:", error);
    throw new Error("Failed to reset password. Please try again.");
  }
};

export const verifyEmail = async (token: string) => {
  try {
    const response = await axios.get(`${apiUrl}/auth/verify-email`, {
      params: { token },
    });
    return response.data.message || "Email verified successfully!";
  } catch (error) {
    console.error("Verify email error:", error);
    throw new Error("Failed to verify email. Please try again.");
  }
}

export const updatePassword = async (email: string, oldPassword: string, newPassword: string): Promise<void> => {
  try {
    const response = await axios.put(`${apiUrl}/users/change-password`, {
      oldPassword,
      newPassword,
      confirmPassword: newPassword
    }, 
    {
      params: {
        email: email
      },
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
      },
    });

    if (response.status !== 200) {
      throw new Error(response.data.message || 'Failed to update password');
    }
  } catch (error) {
    if (axios.isAxiosError(error)) {
      throw new Error(error.response?.data?.message || 'An error occurred while updating the password');
    }
    throw error;
  }
};

function isAxiosErrorWithMessage(
  error: unknown
): error is AxiosError<{ message: string }> {
  return axios.isAxiosError(error) && error.response?.data?.message !== undefined;
}
