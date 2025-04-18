import React from "react";
import { Signup } from "@/components/forms/signup";
import { Login } from "@/components/forms/login";
import { MainLayout } from "@/layout/main-layout";
import { Row, Col } from "antd";
import { useTheme } from "@/context/theme-context";
import { useSearchParams } from "react-router-dom";

export function AuthenticateLayout() {
  const { darkMode } = useTheme();
  const [searchParams, setSearchParams] = useSearchParams();

  const mode = searchParams.get("mode") || "login";
  const isLoginMode = mode === "login";

  const containerBackgroundColor = darkMode ? "#272727" : "#f0f0f0";

  const toggleMode = () => {
    setSearchParams({ mode: isLoginMode ? "register" : "login" });
  };

  return (
    <MainLayout>
      <div
        className="flex flex-col items-center justify-center min-h-screen"
        style={{ padding: "20px", backgroundColor: containerBackgroundColor }}
      >
        <Row justify="center" style={{ width: "100%" }}>
          <Col xs={24} sm={18} md={12} lg={8}>
            {isLoginMode ? (
              <Login toggleMode={toggleMode} />
            ) : (
              <Signup toggleMode={toggleMode} />
            )}
          </Col>
        </Row>
      </div>
    </MainLayout>
  );
}

export default AuthenticateLayout;
