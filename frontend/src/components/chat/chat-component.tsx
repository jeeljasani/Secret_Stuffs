import React, { useState, useRef, useEffect, useCallback } from 'react';
import { Layout, List, Avatar, Input, Button, Typography, Spin, message as antdMessage } from 'antd';
import { SendOutlined, UserOutlined } from '@ant-design/icons';
import chatService from '@/services/chat/chat-service';
import { useAuth } from '@/context/auth-context';
import { useTheme } from '@/context/theme-context';
import { Subscription } from 'stompjs';

const { Header, Content } = Layout;
const { Title, Text } = Typography;

interface Message {
  id: number;
  sender: string;
  content: string;
  timestamp: string | undefined;
}

interface ChatComponentProps {
  selectedUser: {
    id: string;
    name: string;
    avatar: string;
  };
}

export default function ChatComponent({ selectedUser }: ChatComponentProps) {
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const [messages, setMessages] = useState<Message[]>([]);
  const [inputMessage, setInputMessage] = useState('');
  const [loading, setLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const subscriptionRef = useRef<Subscription | null>(null);

  const arrayToTimestamp = (timestamp: string) => {
    if (!timestamp) return 'Unknown Time';
    if (Array.isArray(timestamp)) {
      const [year, month, day, hour, minute, second, nanoseconds] = timestamp;
      const date = new Date(year, month - 1, day, hour, minute, second);
      const milliseconds = Math.floor(nanoseconds / 1000000);
      date.setMilliseconds(milliseconds);
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    }
    return 'Invalid Time';
  }

  const formatTimestamp = (timestamp: string) => {
    if (!timestamp) return 'Unknown Time';
    const date = new Date(timestamp);
    console.log('date: ', date);
    return isNaN(date.getTime())
      ? 'Invalid Time'
      : date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  };

  const unsubscribeFromMessages = useCallback(() => {
    if (subscriptionRef.current) {
      try {
        subscriptionRef.current.unsubscribe();
        console.log('Unsubscribed from previous WebSocket subscription');
      } catch (error) {
        console.error('Error during unsubscription:', error);
      } finally {
        subscriptionRef.current = null;
      }
    }
  }, []);

  const loadMessages = useCallback(async () => {
    if (user?.email) {
      setLoading(true);
      try {
        const chatMessages = await chatService.getChatMessages(user.email, selectedUser.id);
        setMessages(
          chatMessages.map((msg) => ({
            id: msg.id || Date.now(),
            sender: msg.senderId === user.email ? 'You' : selectedUser.name,
            content: msg.content,
            timestamp: arrayToTimestamp(msg.timestamp),
          }))
        );
      } catch (error) {
        console.error('Error loading messages:', error);
        antdMessage.error('Failed to load messages.');
      } finally {
        setLoading(false);
      }
    }
  }, [selectedUser.id, user?.email]);

  const handleSendMessage = useCallback(() => {
    if (inputMessage.trim() && user?.email) {
      const newMessage = {
        id: Date.now(),
        sender: 'You',
        content: inputMessage,
        timestamp: formatTimestamp(new Date().toISOString()),
      };

      setMessages([...messages, newMessage]);
      setInputMessage('');

      chatService
        .sendMessage('/app/chat', {
          id: Date.now(),
          senderId: user.email,
          recipientId: selectedUser.id,
          content: inputMessage,
          timestamp: new Date().toISOString(),
        })
        .catch((error) => {
          console.error('Error sending message:', error);
          antdMessage.error('Failed to send message.');
        });
    }
  }, [inputMessage, messages, selectedUser.id, user?.email]);

  useEffect(() => {
    const subscribeToMessages = async () => {
      if (user?.email && selectedUser.id) {
        unsubscribeFromMessages();

        const topic = `/user/${user.email}/queue/messages/${selectedUser.id}`;
        try {
          const subscription = await chatService.subscribe(topic, (msg) => {
            if (msg.senderId === selectedUser.id || msg.recipientId === selectedUser.id) {
              const incomingMessage = {
                id: msg.id || Date.now(),
                sender: msg.senderId === user.email ? 'You' : selectedUser.name,
                content: msg.content,
                timestamp: formatTimestamp(msg.timestamp),
              };
              setMessages((prevMessages) => [...prevMessages, incomingMessage]);
            }
          });
          if (subscription) {
            subscriptionRef.current = subscription;
            console.log(`Subscribed to messages: ${user.email} <-> ${selectedUser.id}`);
          }
        } catch (error) {
          console.error('Subscription error:', error);
          antdMessage.error('Failed to subscribe to messages.');
        }
      }
    };

    subscribeToMessages();
    return unsubscribeFromMessages;
  }, [selectedUser, user?.email, unsubscribeFromMessages]);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  useEffect(() => {
    loadMessages();
  }, [loadMessages]);

  return (
    <Layout
      style={{
        height: '100%',
        backgroundColor: darkMode ? '#1c1c1e' : '#ffffff',
      }}
    >
      <Header
        style={{
          backgroundColor: darkMode ? '#2c2c2e' : '#f0f2f5',
          padding: '16px',
          display: 'flex',
          alignItems: 'center',
          borderBottom: darkMode ? '1px solid #3a3a3c' : '1px solid #d9d9d9',
        }}
      >
        <Avatar src={selectedUser.avatar} icon={<UserOutlined />} size="large" />
        <Title
          level={4}
          style={{ margin: '0 0 0 16px', color: darkMode ? '#ffffff' : '#000000' }}
        >
          {selectedUser.name}
        </Title>
      </Header>

      <Content
        style={{
          padding: '16px',
          overflowY: 'auto',
          backgroundColor: darkMode ? '#1c1c1e' : '#ffffff',
        }}
      >
        {loading ? (
          <Spin />
        ) : (
          <List
            dataSource={messages}
            renderItem={(message) => (
              <List.Item
                style={{
                  justifyContent: message.sender === 'You' ? 'flex-end' : 'flex-start',
                  padding: '4px 0',
                }}
              >
                <div
                  style={{
                    backgroundColor:
                      message.sender === 'You'
                        ? darkMode
                          ? '#2f80ed'
                          : '#e6f7ff'
                        : darkMode
                          ? '#3a3a3c'
                          : '#f0f0f0',
                    color: darkMode ? '#ffffff' : '#000000',
                    padding: '10px 16px',
                    borderRadius: '18px',
                    maxWidth: '70%',
                    boxShadow: '0 1px 4px rgba(0, 0, 0, 0.1)',
                  }}
                >
                  <Text
                    strong
                    style={{
                      color: darkMode ? '#bbbbbb' : '#595959',
                    }}
                  >
                    {message.sender}
                  </Text>
                  <p style={{ margin: '4px 0' }}>{message.content}</p>
                  <Text
                    type="secondary"
                    style={{
                      fontSize: '12px',
                      color: darkMode ? '#888888' : '#8c8c8c',
                    }}
                  >
                    {message.timestamp}
                  </Text>
                </div>
              </List.Item>
            )}
          />
        )}
        <div ref={messagesEndRef} />
      </Content>

      <div
        style={{
          padding: '16px',
          backgroundColor: darkMode ? '#2c2c2e' : '#f9f9f9',
          borderTop: darkMode ? '1px solid #3a3a3c' : '1px solid #d9d9d9',
          display: 'flex',
          alignItems: 'center',
        }}
      >
        <Input
          style={{
            flex: 1,
            marginRight: '8px',
            borderRadius: '20px',
            padding: '8px 16px',
            backgroundColor: darkMode ? '#1c1c1e' : '#ffffff',
            color: darkMode ? '#ffffff' : '#000000',
            borderColor: darkMode ? '#444' : '#d9d9d9',
          }}
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          onPressEnter={handleSendMessage}
          placeholder="Type a message..."
        />
        <Button
          type="primary"
          shape="circle"
          icon={<SendOutlined />}
          onClick={handleSendMessage}
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            width: '40px',
            height: '40px',
            backgroundColor: darkMode ? '#2f80ed' : '#1890ff',
            borderColor: darkMode ? '#2f80ed' : '#1890ff',
          }}
        />
      </div>
    </Layout>
  );
}