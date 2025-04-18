import React, { useState } from 'react';
import { Form, Input, Button, Typography, message } from 'antd';
import { motion } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useTheme } from '../../context/theme-context';
import { forgotPassword } from '../../services/auth'; // Import the new function

const { Title, Text } = Typography;

export function ForgotPassword() {
  const [loading, setLoading] = useState(false);
  const [email, setEmail] = useState('');
  const { darkMode } = useTheme(); // Use dark mode state
  const navigate = useNavigate();
  const [disabled, setDisabled] = useState(false);
  const [timer, setTimer] = useState(60);

  const handleSubmit = async () => {
    
    setLoading(true);
    setDisabled(true);

    try {
      // Use the new forgotPassword function
      await forgotPassword(email);
      message.success('Password reset link sent! Check your email.');
      navigate('/auth?mode=login');
    } catch (error) {
      if (error instanceof Error) {
        message.error(error.message);
      } else {

        message.error('An error occurred. Please try again later.');
      }
    } finally {
      setLoading(false);
      startTimer(); 
    }
  };

  const startTimer = () => {
    let timeLeft = 60; 
    const countdown = setInterval(() => {
      timeLeft -= 1;
      setTimer(timeLeft);

      if (timeLeft <= 0) {
        clearInterval(countdown);
        setDisabled(false); // Enable the button after the timer ends
      }
    }, 1000); // Update every second
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
        Forgot Password
      </Title>
      <Text style={{ display: 'block', marginBottom: '20px', textAlign: 'center', color: textColor }}>
        Enter your email address to receive a password reset link.
      </Text>

      <Form layout="vertical" onFinish={handleSubmit}>
        <Form.Item
          label="Email"
          name="email"
          rules={[
            { required: true, message: 'Please input your email!' },
            { type: 'email', message: 'The input is not a valid email!' },
          ]}
        >
          <Input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
            style={{ backgroundColor: inputBackgroundColor, color: inputTextColor }}
          />
        </Form.Item>

        <Form.Item>
          <motion.div whileHover={{ scale: 1.05 }} whileTap={{ scale: 0.95 }}>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              disabled={disabled}
              style={{ width: '100%', height: '40px' }}
            >
              {disabled ? `Try Again in ${timer}s` : 'Send Reset Link'}
            </Button>
          </motion.div>
        </Form.Item>
      </Form>

      <div style={{ textAlign: 'center', marginTop: '10px' }}>
        <Text style={{ color: textColor }}>Remembered your password?</Text>{' '}
        <Button
          type="link"
          onClick={() => navigate('/auth?mode=login')}
          style={{ padding: 0, color: '#1890ff' }}
        >
          Log In
        </Button>
      </div>
    </div>
  );
}

export default ForgotPassword;