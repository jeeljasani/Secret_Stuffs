import React, { useState } from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../../context/theme-context';
import { updatePassword } from '../../services/auth'; 
import { useAuth } from '@/context/auth-context';

const { Title, Text } = Typography;

export function UpdatePassword() {
  const [loading, setLoading] = useState(false);
  const { darkMode } = useTheme();
  const navigate = useNavigate();
  const { user } = useAuth();
  const userEmail = String(user?.email);
  console.log(userEmail);

  const handleSubmit = async (values: { oldPassword: string; newPassword: string; confirmPassword: string }) => {
    if(!user?.email) {
      console.error("User not logged in");
      navigate('/auth');
      return;
    }
    if (values.newPassword !== values.confirmPassword) {
      message.error('New passwords do not match');
      return;
    }

    setLoading(true);

    try {
      await updatePassword(userEmail, values.oldPassword, values.newPassword);
      message.success('Password updated successfully');
      navigate('/profile');
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      } else {
        message.error('An error occurred. Please try again later.');
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
    <div
      style={{
        width: '100%',
        maxWidth: '400px',
        padding: '20px',
        borderRadius: '10px',
        boxShadow: darkMode
          ? '0px 4px 12px rgba(255, 255, 255, 0.1)'
          : '0px 4px 12px rgba(0, 0, 0, 0.1)',
        backgroundColor: containerBackgroundColor,
        color: textColor,
        margin: '50px auto',
      }}
    >
      <Title level={2} style={{ textAlign: 'center', marginBottom: '20px', color: textColor }}>
        Update Password
      </Title>
      <Text style={{ display: 'block', marginBottom: '20px', textAlign: 'center', color: textColor }}>
        Enter your old password and your new password to update.
      </Text>

      <Form layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          label="Old Password"
          name="oldPassword"
          rules={[{ required: true, message: 'Please input your old password!' }]}
        >
          <Input.Password
            placeholder="Enter your old password"
            style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
          />
        </Form.Item>

        <Form.Item
          label="New Password"
          name="newPassword"
          rules={[{ required: true, message: 'Please input your new password!' }]}
        >
          <Input.Password
            placeholder="Enter your new password"
            style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
          />
        </Form.Item>

        <Form.Item
          label="Confirm New Password"
          name="confirmPassword"
          rules={[
            { required: true, message: 'Please confirm your new password!' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('newPassword') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('The two passwords do not match!'));
              },
            }),
          ]}
        >
          <Input.Password
            placeholder="Confirm your new password"
            style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
          />
        </Form.Item>

        <Form.Item>
          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              style={{ width: '100%', height: '40px' }}
            >
              Update Password
            </Button>
          </motion.div>
        </Form.Item>
      </Form>

      <div style={{ textAlign: 'center', marginTop: '10px' }}>
        <Button
          type="link"
          onClick={() => navigate('/profile')}
          style={{ padding: 0, color: '#1890ff' }}
        >
          Back to Profile
        </Button>
      </div>
    </div>
  );
}

export default UpdatePassword;