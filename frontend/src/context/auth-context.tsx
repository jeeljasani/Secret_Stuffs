import React, {
  createContext,
  useContext,
  useEffect,
  useState,
  ReactNode,
} from "react";
import Cookies from "js-cookie"; // Manage cookies
import {
  login as loginUser,
  register as registerUser,
  logout as logoutUser,
} from "@/services/auth"; // Service layer functions

// Define the User interface for clarity
export interface User {
  id: number;
  email: string;
  address?: string | null;
  profileImageURL?: string | null;
  active?: boolean;
  firstName?: string;
  lastName?: string;
}

// Define the type for the API response data
interface AuthResponse {
  id: number;
  token: string;
  email: string;
  expiresAt: number;
  address?: string | null;
  profileImageURL?: string | null;
}

// Define the type for the AuthContext
interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  login: (data: { email: string; password: string }) => Promise<void>;
  register: (data: { firstName: string; lastName:string; email: string; password: string }) => Promise<void>;
  logout: () => void;
  getUser: () => User | null;
  updateUser: (updatedData: Partial<User>) => void;
}

// Create the context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Define the AuthProvider component
export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(
    !!Cookies.get("token")
  );

  // Check authentication status on initial load
  useEffect(() => {
    const token = Cookies.get("token");
    const expiresAt = Cookies.get("expiresAt");

    if (token && expiresAt && new Date().getTime() < parseInt(expiresAt, 10)) {
      setIsAuthenticated(true);
      const userFromCookies = getUser();
      setUser(userFromCookies);
    } else {
      logout(); // Clear expired sessions
    }
  }, []);

  // Login with email and password
  const login = async (data: {
    email: string;
    password: string;
  }): Promise<void> => {
    try {
      const response = await loginUser(data); // Call the login API
      const { id, email, expiresAt, token } = response as AuthResponse;

      // Store token and user info in cookies
      Cookies.set("token", token, { expires: 7 });
      Cookies.set("user", JSON.stringify({ email, id }), { expires: 7 });
      Cookies.set("expiresAt", expiresAt.toString(), { expires: 7 });

      setIsAuthenticated(true);
      setUser({ email, id });
    } catch (error) {
      console.error("Login failed:", error);
      throw error;
    }
  };

  // Register new user
  const register = async (data: {
    firstName:string;
    lastName:string;
    email: string;
    password: string;
  }): Promise<void> => {
    try {
      const response = await registerUser(data); // Call the register API
      const { id, token, expiresAt, email, address, profileImageURL } =
        response as AuthResponse;

      const user: User = { email, address, profileImageURL, id };

      // Store token and user info in cookies
      Cookies.set("token", token, { expires: 7 });
      Cookies.set("user", JSON.stringify(user), { expires: 7 });
      Cookies.set("expiresAt", expiresAt.toString(), { expires: 7 });

      setIsAuthenticated(true);
      setUser(user);
    } catch (error) {
      console.error("Registration failed:", error);
      throw error;
    }
  };

  // Logout the user and clear cookies
  const logout = () => {
    logoutUser(); // Optional: Call backend logout API
    Cookies.remove("token");
    Cookies.remove("user");
    Cookies.remove("expiresAt");

    setIsAuthenticated(false);
    setUser(null);
  };

  // Update the user information
  const updateUser = (updatedData: Partial<User>) => {
    setUser((prevUser) => {
      const newUser = { ...prevUser!, ...updatedData };
      Cookies.set("user", JSON.stringify(newUser), { expires: 7 }); // Update user cookie
      return newUser;
    });
  };

  const getUser = (): User | null => {
    const storedUser = Cookies.get("user");
    return storedUser ? JSON.parse(storedUser) : null;
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        getUser,
        isAuthenticated,
        login,
        register,
        logout,
        updateUser,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

// Custom hook to use the AuthContext
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
