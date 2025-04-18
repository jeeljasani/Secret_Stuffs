import React, { useState, useEffect, useCallback } from 'react';
import { List, message, Avatar, Modal, Tag, Button, Spin, Empty, Typography, Space } from 'antd';
import { UserOutlined, ExclamationCircleOutlined } from '@ant-design/icons';
import { Status, DonationService, Donation } from "@/services/item/donation";

const { confirm } = Modal;
const { Title, Text } = Typography;

interface ItemInterestListProps {
  itemId: number;
  itemTitle: string;
  modalVisible: boolean;
  onModalClose: () => void;
}

const ItemInterestList: React.FC<ItemInterestListProps> = ({
  itemId,
  itemTitle,
  modalVisible,
  onModalClose,
}) => {
  const [donationRequests, setDonationRequests] = useState<Donation[]>([]);
  const [donated, setDonated] = useState(false);
  const [loading, setLoading] = useState(false);
  const [donatingUserId, setDonatingUserId] = useState<number | null>(null);

  const fetchInterestedUsers = useCallback(async () => {
    try {
      setLoading(true);
      const response = await DonationService.getInterestUsers(itemId);
      const donationRequests = response.data;
      setDonated(donationRequests.some(donation => donation.status === 'ACCEPTED'));
      setDonationRequests(donationRequests);
    } catch (error) {
      console.error('Failed to load interested users:', error);
      message.error('Failed to load interested users. Please try again later.');
    } finally {
      setLoading(false);
    }
  }, [itemId]);

  const handleConfirmDonation = (userId: number) => {
    confirm({
      title: 'Are you sure you want to donate this item?',
      icon: <ExclamationCircleOutlined />,
      content: 'Once donated, this action cannot be undone.',
      okText: 'Yes, Donate',
      okType: 'primary',
      cancelText: 'Cancel',
      onOk: () => handleDonation(userId, itemId),
    });
  };

  const handleDonation = async (userId: number, itemId: number) => {
    setDonatingUserId(userId);
    try {
      await DonationService.donate(userId, itemId);
      message.success('Donation successfully accepted!');
      fetchInterestedUsers(); // Refresh the list
      onModalClose();
    } catch (error) {
      console.error('Failed to process donation:', error);
      message.error('Failed to process the donation. Please try again.');
    } finally {
      setDonatingUserId(null);
    }
  };

  useEffect(() => {
    if (modalVisible) {
      fetchInterestedUsers();
    }
  }, [fetchInterestedUsers, modalVisible]);

  const getStatusTag = (status?: Status) => {
    if (!status) return null;

    const statusConfig = {
      PENDING: { color: 'blue', text: 'Pending' },
      ACCEPTED: { color: 'green', text: 'Accepted' },
      REJECTED: { color: 'red', text: 'Rejected' },
      CANCELLED: { color: 'grey', text: 'Cancelled' },
    };

    const config = statusConfig[status];
    return <Tag color={config.color}>{config.text}</Tag>;
  };

  return (
    <Modal
      title={<Title level={4}>Interested Users for "{itemTitle}"</Title>}
      open={modalVisible}
      onCancel={onModalClose}
      footer={null}
      width={800}
      bodyStyle={{ padding: '20px 30px' }}
    >
      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px 0' }}>
          <Spin size="large" />
        </div>
      ) : donationRequests.length > 0 ? (
        <List
          dataSource={donationRequests}
          renderItem={(donationRequest) => (
            <List.Item
              style={{
                padding: '16px 0',
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                borderBottom: '1px solid #f0f0f0',
              }}
            >
              <Space>
                <Avatar
                  src={donationRequest.user.profileImageURL || undefined}
                  icon={<UserOutlined />}
                  size="large"
                />
                <div>
                  <Text strong>
                    {donationRequest.user.firstName} {donationRequest.user.lastName}
                  </Text>
                  <div>{getStatusTag(donationRequest.status as Status)}</div>
                </div>
              </Space>
              {!donated ? (
                <Button
                  type="primary"
                  onClick={() => handleConfirmDonation(donationRequest.user.id)}
                  loading={donatingUserId === donationRequest.user.id}
                  disabled={!!donatingUserId && donatingUserId !== donationRequest.user.id}
                >
                  Donate
                </Button>
              ) : (
                <Tag color="green" style={{ fontWeight: 'bold' }}>
                  Item Donated
                </Tag>
              )}
            </List.Item>
          )}
        />
      ) : (
        <Empty
          description="No interested users yet"
          image={Empty.PRESENTED_IMAGE_SIMPLE}
          style={{ padding: '50px 0' }}
        />
      )}
    </Modal>
  );
};

export default ItemInterestList;