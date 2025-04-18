import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import HomeLayout from './layout/home/index'; // Ensure correct import paths
import ProfileLayout from './layout/profile/index';
import { AuthenticateLayout } from './layout/authentication/authenticate-layout';
import { CreateDonationLayout } from './layout/create/index';
import { AuthProvider, useAuth } from './context/auth-context';
import { ThemeProvider } from './context/theme-context';  // Separate ThemeContext
import { ForgotPassword } from './components/forms/forgot-password';
import { UserSpecificPosts } from './layout/item/user-specific-post';
import { ResetPasswordForm } from './components/forms/reset-password';
import { ItemLayout } from './layout/item';
import { NotFound } from './components/common/not-found';
import ChatLayout from './layout/chat/chat-layout';
import { VerifyEmail } from './layout/authentication/verify-email';
import  UpdatePasswordLayout  from './layout/profile/update-password-layout'

interface RouteConfig {
  path: string;
  element: React.ReactNode;
  protected?: boolean;
}

const ProtectedRoute: React.FC<{ element: React.ReactNode }> = ({ element }) => {
  const { isAuthenticated } = useAuth();
  return isAuthenticated ? element : <Navigate to="/auth" />;
};

const AppRoutes: React.FC = () => {
  const appRoutes: RouteConfig[] = [
    {
      path: '/',
      element: <Navigate to="/home" />,
      protected: true,
    },
    {
      path: '/auth',
      element: <AuthenticateLayout />,
    },
    {
      path: '/home',
      element: <HomeLayout />,
      protected: true,
    },
    {
      path: '/my-posts',
      element: <UserSpecificPosts />,
      protected: true,
    },
    {
      path: '/chats',
      element: <ChatLayout />,
      protected: true,
    },
    {
      path: '/create',
      element: <CreateDonationLayout />,
      protected: true,
    },
    {
      path: '/item/:id',
      element: <ItemLayout />,
      protected: true,
    },
    {
      path: '/profile',
      element: <ProfileLayout />,
      protected: true,
    },
    {
      path: '/forgot-password',
      element: <ForgotPassword />,
    },
    {
      path: '/reset-password/:token',
      element: <ResetPasswordForm />,
    },
    {
      path: '/verify-email/:token',
      element: <VerifyEmail />,
    },
    {
      path: '/update-password',
      element: <UpdatePasswordLayout />,
      protected: true,
    },
    {
      path: '*',
      element: <NotFound />,
    },
  ];

  return (
    <Routes>
      {appRoutes.map(({ path, element, protected: isProtected }) => (
        <Route
          key={path}
          path={path}
          element={isProtected ? <ProtectedRoute element={element} /> : element}
        />
      ))}
    </Routes>
  );
}

const App: React.FC = () => {
  return (
    <AuthProvider>
      <ThemeProvider>
        <Router>
          <AppRoutes />
        </Router>
      </ThemeProvider>
    </AuthProvider>
  );
}

export default App;
