import React, { useEffect, useState, CSSProperties } from "react";
import {
  Layout,
  Menu,
  Switch,
  Row,
  Col,
  Typography,
  Avatar,
  Dropdown,
  Drawer,
  Button,
  Grid,
  message,
} from "antd";
import { UserOutlined, MenuOutlined } from "@ant-design/icons";
import { useNavigate, useLocation } from "react-router-dom";
import { useAuth } from "@/context/auth-context";
import { useTheme } from "@/context/theme-context";
import { getUserProfile } from "@/services/user";
import icon from "@/assets/icons/icon.png";

const { Header, Content } = Layout;
const { Title } = Typography;
const { useBreakpoint } = Grid;

interface MainLayoutProps {
  children: React.ReactNode;
  styles?: CSSProperties;
}

export function MainLayout({ children, styles }: MainLayoutProps) {
  const { darkMode, toggleDarkMode } = useTheme();
  const { logout, isAuthenticated, getUser } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const screens = useBreakpoint();

  const [activeMenuKey, setActiveMenuKey] = useState(location.pathname);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [profileImage, setProfileImage] = useState<string | null>(null);

  useEffect(() => {
    setActiveMenuKey(location.pathname);
  }, [location.pathname]);

  useEffect(() => {
    const fetchProfileData = async () => {
      if (isAuthenticated) {
        try {
          const user = getUser();
          if (user?.email) {
            const profileData = await getUserProfile(user.email);
            setProfileImage(profileData.profileImageURL || null);
          }
        } catch {
          message.error("Failed to load profile information.");
        }
      }
    };

    fetchProfileData();
  }, [isAuthenticated]);

  const handleNavigate = (path: string) => {
    navigate(path);
    setActiveMenuKey(path);
    setDrawerVisible(false);
  };

  const profileMenu = (
    <Menu theme={darkMode ? "dark" : "light"}>
      <Menu.Item key="profile" onClick={() => handleNavigate("/profile")}>
        Profile
      </Menu.Item>
      <Menu.Item key="logout" onClick={logout}>
        Logout
      </Menu.Item>
    </Menu>
  );

  const menuItems = [
    { key: "/home", label: "Home" },
    { key: "/my-posts", label: "My Posts" },
    { key: "/create", label: "Create Post" },
    { key: "/chats", label: "My Chats" },
  ];

  const handleClick = () => {
    navigate('/home');
  };

  const renderMenuItems = () =>
    menuItems.map((item) => (
      <Menu.Item key={item.key} onClick={() => handleNavigate(item.key)}>
        {item.label}
      </Menu.Item>
    ));

  const headerBackgroundColor = darkMode ? "#2C3E50" : "#FFFFFF";
  const menuItemColor = darkMode ? "#ECF0F1" : "#34495E";
  const iconColor = darkMode ? "#3498DB" : "#E74C3C";
  const drawerBackgroundColor = darkMode ? "#34495E" : "#ECF0F1";

  return (
    <Layout style={{ minHeight: "100vh", overflow: "hidden", ...styles }}>
      <Header
        style={{
          position: "fixed",
          top: 0,
          zIndex: 1000,
          width: "100%",
          background: headerBackgroundColor,
          boxShadow: "0 2px 8px rgba(0, 0, 0, 0.15)",
          height: "64px",
          padding: "0 24px",
        }}
      >
        <Row justify="space-between" align="middle" style={{ height: "64px" }}>
          <Col className="cursor-pointer"
            onClick={handleClick}>
            <Row align="middle">
              <img
                src={icon}
                alt="icon"
                style={{ width: 28, height: 28, marginRight: 10 }}
              />
              <Title
                level={4}
                style={{
                  margin: 0,
                  color: darkMode ? "#ECF0F1" : "#34495E",
                  fontWeight: 600,
                  letterSpacing: "0.5px",
                }}
              >
                Secret Stuff
              </Title>
            </Row>
          </Col>

          <Col style={{ display: "flex", alignItems: "center", gap: "20px" }}>
            {isAuthenticated && screens.md ? (
              <Row align="middle" gutter={16}>
                {menuItems.map((item) => (
                  <Col
                    key={item.key}
                    onClick={() => handleNavigate(item.key)}
                    style={{
                      cursor: "pointer",
                      fontWeight: "bold",
                      padding: "0 10px",
                      color: menuItemColor,
                      transition: "color 0.3s ease",
                    }}
                  >
                    {item.label}
                  </Col>
                ))}
              </Row>
            ) : !screens.md && isAuthenticated ? (
              <Button
                icon={<MenuOutlined />}
                onClick={() => setDrawerVisible(true)}
                style={{
                  color: iconColor,
                  fontSize: "20px",
                  background: "none",
                  border: "none",
                }}
              />
            ) : null}

            <Switch
              checked={darkMode}
              onChange={toggleDarkMode}
              checkedChildren="Dark"
              unCheckedChildren="Light"
              style={{ marginRight: "8px" }}
            />

            {isAuthenticated && (
              <Dropdown overlay={profileMenu} trigger={["click"]}>
                <Avatar
                  src={profileImage || undefined}
                  icon={!profileImage ? <UserOutlined /> : undefined}
                  style={{
                    cursor: "pointer",
                    backgroundColor: profileImage ? "transparent" : iconColor,
                  }}
                />
              </Dropdown>
            )}
          </Col>
        </Row>
      </Header>

      <Drawer
        title="Menu"
        placement="left"
        onClose={() => setDrawerVisible(false)}
        visible={drawerVisible}
        bodyStyle={{ padding: 0, backgroundColor: drawerBackgroundColor }}
      >
        <Menu
          mode="inline"
          selectedKeys={[activeMenuKey]}
          theme={darkMode ? "dark" : "light"}
        >
          {isAuthenticated && renderMenuItems()}
        </Menu>
      </Drawer>

      <Layout style={{ marginTop: "64px", height: "calc(100vh - 64px)" }}>
        <Content
          style={{
            background: darkMode ? "#1C1C1C" : "#ECF0F1",
            transition: "background 0.3s ease",
          }}
        >
          {children}
        </Content>
      </Layout>
    </Layout>
  );
}

export default MainLayout;