import React, { useState, useEffect, useMemo } from 'react';
import { Layout, List, Avatar, Input, Typography, Spin, Empty, message as antdMessage } from 'antd';
import { UserOutlined } from '@ant-design/icons';
import ChatComponent from '@/components/chat/chat-component';
import MainLayout from '@/layout/main-layout';
import chatService from '@/services/chat/chat-service';
import { useAuth } from '@/context/auth-context';
import { useTheme } from '@/context/theme-context';

const { Header, Sider, Content } = Layout;
const { Search } = Input;
const { Text, Title } = Typography;

// Define User type
type User = {
  id: string;
  name: string;
  avatar: string;
};

export default function ChatLayout() {
  const { user } = useAuth();
  const { darkMode } = useTheme();
  const [users, setUsers] = useState<User[]>([]);
  const [selectedUser, setSelectedUser] = useState<User | null>(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!user?.email) return;

    const fetchUsers = async () => {
      setLoading(true);
      try {
        const recipientEmails = await chatService.getRecipientsBySender(user.email);
        const userData: User[] = await Promise.all(
          recipientEmails.map(async (email) => {
            try {
              const profile = await chatService.getUserProfile(email);
              if (!profile) {
                console.warn(`Profile not found for email: ${email}`);
                return {
                  id: email,
                  name: 'Unknown User',
                  avatar: 'https://via.placeholder.com/150',
                };
              }

              return {
                id: email,
                name: `${profile.firstName || ''} ${profile.lastName || ''}`.trim() || 'Anonymous User',
                avatar: profile.profileImageURL || 'https://via.placeholder.com/150',
              };
            } catch (error) {
              console.error(`Error fetching user profile for ${email}:`, error);
              return {
                id: email,
                name: 'Unknown User',
                avatar: 'https://via.placeholder.com/150',
              };
            }
          })
        );

        setUsers(userData);
        if (userData.length > 0) setSelectedUser(userData[0]);
      } catch (error) {
        console.error('Error fetching recipients:', error);
        antdMessage.error('Failed to load users. Please try again.');
      } finally {
        setLoading(false);
      }
    };

    chatService.connect();
    fetchUsers();

    return () => {
      chatService.disconnect();
    };
  }, [user?.email]);

  const filteredUsers = useMemo(
    () => users.filter((user) => user.name.toLowerCase().includes(searchTerm.toLowerCase())),
    [users, searchTerm]
  );

  const themeStyles = {
    sider: {
      backgroundColor: darkMode ? '#1c1c1e' : '#f5f7fa',
      borderRight: darkMode ? '1px solid #333' : '1px solid #e0e0e0',
    },
    header: {
      backgroundColor: darkMode ? '#1c1c1e' : '#f5f7fa',
      borderBottom: darkMode ? '1px solid #333' : '1px solid #e0e0e0',
      padding: '16px',
    },
    searchBar: {
      borderRadius: '24px', // Adjusted for rounded corners
      padding: '6px 16px', // Adjusted padding for a balanced look
      height: '40px', // Consistent height
      backgroundColor: darkMode ? '#2b2b2b' : '#ffffff',
      borderColor: darkMode ? '#444' : '#ddd',
      color: darkMode ? '#ffffff' : '#000000',
    },
    listItem: (isSelected: boolean) => ({
      cursor: 'pointer',
      background: isSelected ? (darkMode ? '#3a3a3c' : '#e6f7ff') : 'transparent',
      padding: '12px 16px',
      transition: 'background 0.2s ease',
      borderLeft: isSelected ? `4px solid ${darkMode ? '#007acc' : '#1890ff'}` : 'none',
    }),
    text: {
      color: darkMode ? '#ffffff' : '#333333',
    },
    emptyDescription: {
      color: darkMode ? '#bbbbbb' : '#888888',
    },
    loadingText: {
      color: darkMode ? '#bbbbbb' : '#888888',
    },
    content: {
      backgroundColor: darkMode ? '#1c1c1e' : '#ffffff',
    },
  };

  return (
    <MainLayout>
      <Layout style={{ height: 'calc(100vh - 64px)' }}>
        {/* Sidebar */}
        <Sider width={250} style={themeStyles.sider}>
        <Header style={{ backgroundColor: 'transparent', padding: '16px 16px' }}>
        <Search
          placeholder="Search users"
          onChange={(e) => setSearchTerm(e.target.value)}
          value={searchTerm}
        />
      </Header>

          {loading ? (
            <div style={{ textAlign: 'center', marginTop: '20px' }}>
              <Spin />
              <Text style={{ ...themeStyles.loadingText, display: 'block', marginTop: '8px' }}>
                Loading contacts...
              </Text>
            </div>
          ) : filteredUsers.length > 0 ? (
            <List
              dataSource={filteredUsers}
              renderItem={(user) => (
                <List.Item
                  onClick={() => setSelectedUser(user)}
                  style={themeStyles.listItem(selectedUser?.id === user.id)}
                >
                  <List.Item.Meta
                    avatar={<Avatar src={user.avatar} icon={<UserOutlined />} />}
                    title={<Text style={themeStyles.text}>{user.name}</Text>}
                  />
                </List.Item>
              )}
            />
          ) : (
            <Empty description="No contacts available" style={{ padding: '20px' }}/>
          )}
        </Sider>

        {/* Chat Content */}
        <Content style={themeStyles.content}>
          {selectedUser ? (
            <ChatComponent selectedUser={selectedUser} />
          ) : (
            <div style={{ textAlign: 'center', padding: '24px' }}>
              <Title level={4} style={themeStyles.text}>
                Select a user to start chatting
              </Title>
              <Text type="secondary" style={themeStyles.emptyDescription}>
                Click on a contact from the list to view messages.
              </Text>
            </div>
          )}
        </Content>
      </Layout>
    </MainLayout>
  );
}