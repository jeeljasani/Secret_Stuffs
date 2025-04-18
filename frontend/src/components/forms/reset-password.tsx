import React, { useState } from 'react';
import { Form, Input, Button, message } from 'antd';
import { useNavigate, useParams } from 'react-router-dom';
import { LockOutlined } from '@ant-design/icons';
import { resetPassword } from '../../services/auth';
import { useTheme } from '../../context/theme-context';

export function ResetPasswordForm() {
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const { darkMode } = useTheme();

  const { token } = useParams<{ token: string }>();

  const onFinish = async (values: { password: string; confirmPassword: string }) => {
    if (!token) {
      message.error('Invalid reset token');
      return;
    }

    try {
      setLoading(true);
      // Call the resetPassword API function
      await resetPassword(token, values.password, values.confirmPassword);
      message.success('Password reset successfully');
      navigate('/auth');
    } catch (error) {
      console.error(error);
      if (error instanceof Error) {
        message.error(error.message);
      } else {
        message.error('Failed to reset password. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const containerBackgroundColor = darkMode ? '#1f1f1f' : '#fff';
  const textColor = darkMode ? '#fff' : '#000';
  const inputBackgroundColor = darkMode ? '#333' : '#fff';
  const inputTextColor = darkMode ? '#fff' : '#000';

  return (
    <div className="flex flex-col items-center justify-center min-h-screen bg-gray-50 p-4">
      <div className="w-full max-w-[400px] bg-white p-8 rounded-lg shadow-md" style={{
        backgroundColor: containerBackgroundColor,
        color: textColor,
      }}>
        <h1 className="text-2xl font-bold text-center mb-6" style={{ color: textColor }}>Reset Password</h1>
        <p className="text-center text-gray-600 mb-6" style={{ color: textColor }}>
          Please enter your new password below
        </p>

        <Form
          form={form}
          name="reset-password"
          onFinish={onFinish}
          layout="vertical"
          requiredMark
        >
          <Form.Item
            name="password"
            label="New Password"
            rules={[
              { required: true, message: 'Please input your new password!' },
              { min: 8, message: 'Password must be at least 8 characters long' }
            ]}
          >
            <Input.Password 
              prefix={<LockOutlined />}
              placeholder="Enter your new password"
              size="large"
              style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
            />
          </Form.Item>

          <Form.Item
            name="confirmPassword"
            label="Confirm Password"
            dependencies={['password']}
            rules={[
              { required: true, message: 'Please confirm your password!' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('The two passwords do not match!'));
                },
              }),
            ]}
          >
            <Input.Password 
              prefix={<LockOutlined />}
              placeholder="Confirm your new password"
              size="large"
              style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              className="w-full h-10"
              loading={loading}
            >
              Reset Password
            </Button>
          </Form.Item>

          <div className="text-center">
            <a href="/login" className="text-blue-500 hover:underline">
              Back to Login
            </a>
          </div>
        </Form>
      </div>
    </div>
  );
};

export default ResetPasswordForm;