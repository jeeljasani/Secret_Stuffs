import React, { useState, useEffect } from "react";
import { Form, Input, Button, Typography, Modal, message } from "antd";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/context/auth-context";
import { useTheme } from "@/context/theme-context";
import { resendVerificationEmail } from "@/services/auth";
import axios from "axios";

const { Title, Link, Text } = Typography;

interface LoginFormValues {
  email: string;
  password: string;
}

interface LoginProps {
  toggleMode: () => void; // Function to switch to Signup mode
}

export function Login({ toggleMode }: LoginProps) {
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [isModalVisible, setIsModalVisible] = useState(false);
  const [resendEmail, setResendEmail] = useState<string>("");
  const { login, isAuthenticated } = useAuth();
  const { darkMode } = useTheme();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate("/home");
    }
  }, [isAuthenticated, navigate]);

  const handleSubmit = async (values: LoginFormValues) => {
    setLoading(true);
    try {
      await login(values);
      message.success("Login successful!");
      navigate("/home");
    } catch (error: unknown) {
      const errorMessage = getErrorMessage(error);
      console.error("Login error:", errorMessage);

      if (errorMessage.includes("not verified")) {
        setResendEmail(values.email);
        setIsModalVisible(true);
        message.error(
          "Your account is not verified. Please verify your email or resend verification."
        );
      } else {
        message.error("Invalid email or password.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleResendVerification = async () => {
    setResendLoading(true);
    try {
      await resendVerificationEmail(resendEmail);
      message.success("Verification email resent. Please check your inbox.");
      setIsModalVisible(false);
    } catch (error: unknown) {
      console.error("Resend verification error:", getErrorMessage(error));
      message.error("Failed to resend verification email. Please try again.");
    } finally {
      setResendLoading(false);
    }
  };

  const handleForgotPassword = () => {
    navigate("/forgot-password");
  };

  const handleModalCancel = () => {
    setIsModalVisible(false);
  };

  // Dynamic styles for dark and light mode
  const containerBackgroundColor = darkMode ? "#121212" : "#fff";
  const textColor = darkMode ? "#fff" : "#000"; // White text in dark mode
  const inputBackgroundColor = darkMode ? "#333" : "#fff";
  const inputTextColor = darkMode ? "#fff" : "#000";
  const inputBorderColor = darkMode ? "#444" : "#d9d9d9";

  return (
      <div
        style={{
          width: "100%",
          maxWidth: "600px", // Matches Signup component
          padding: "40px", // Increased padding for spaciousness
          borderRadius: "12px",
          boxShadow: "0 8px 24px rgba(0, 0, 0, 0.2)",
          backgroundColor: containerBackgroundColor,
        }}
      >
        <Title
          level={2}
          style={{
            textAlign: "center",
            marginBottom: "30px",
            color: textColor,
          }}
        >
          Sign In
        </Title>

        <Form
          name="login-form"
          layout="vertical"
          onFinish={handleSubmit}
          initialValues={{ remember: true }}
        >
          <Form.Item
            label={<Text style={{ color: textColor }}>Email</Text>}
            name="email"
            rules={[
              { required: true, message: "Please input your email!" },
              { type: "email", message: "The input is not a valid email!" },
            ]}
          >
            <Input
              placeholder="Enter your email"
              style={{
                backgroundColor: inputBackgroundColor,
                color: inputTextColor,
                borderColor: inputBorderColor,
                borderRadius: "8px",
              }}
            />
          </Form.Item>

          <Form.Item
            label={<Text style={{ color: textColor }}>Password</Text>}
            name="password"
            rules={[{ required: true, message: "Please input your password!" }]}
          >
            <Input.Password
              placeholder="Enter your password"
              style={{
                backgroundColor: inputBackgroundColor,
                color: inputTextColor,
                borderColor: inputBorderColor,
                borderRadius: "8px",
              }}
            />
          </Form.Item>

          <Form.Item>
            <Link
              onClick={handleForgotPassword}
              style={{ float: "right", color: "#1890ff", cursor: "pointer" }}
            >
              Forgot Password?
            </Link>
          </Form.Item>

          <Form.Item>
            <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
              <Button
                type="primary"
                htmlType="submit"
                loading={loading}
                style={{
                  width: "100%",
                  height: "45px",
                  borderRadius: "8px",
                }}
              >
                Sign In
              </Button>
            </motion.div>
          </Form.Item>
        </Form>

        <div style={{ textAlign: "center", marginTop: "16px" }}>
          <Text style={{ color: textColor }}>
            Don't have an account?{" "}
            <Link
              onClick={toggleMode}
              style={{ color: "#1890ff", cursor: "pointer" }}
            >
              Register
            </Link>
          </Text>
        </div>

        <Modal
          title="Resend Verification Email"
          visible={isModalVisible}
          onCancel={handleModalCancel}
          footer={null}
        >
          <p>A verification email will be sent to:</p>
          <Input
            value={resendEmail}
            disabled
            style={{
              marginBottom: "15px",
              backgroundColor: inputBackgroundColor,
              color: inputTextColor,
              borderColor: inputBorderColor,
              borderRadius: "8px",
            }}
          />
          <Button
            type="primary"
            onClick={handleResendVerification}
            loading={resendLoading}
            style={{ width: "100%" }}
          >
            Resend Verification Email
          </Button>
        </Modal>
      </div>
  );
}

function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error) && error.response?.data?.message) {
    return error.response.data.message;
  }
  return String(error);
}