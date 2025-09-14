import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { userAPI, authAPI } from '../services/api';
import './ProfileEdit.css';

interface User {
  uid: number;
  userid: string;
  nickname: string;
  email: string;
  userType: string;
  createdAt: string;
  updatedAt: string;
}

const ProfileEdit: React.FC = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [activeTab, setActiveTab] = useState<'profile' | 'password'>('profile');

  // Profile form state
  const [nickname, setNickname] = useState('');
  const [email, setEmail] = useState('');

  // Password form state
  const [currentPassword, setCurrentPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');

  useEffect(() => {
    loadUserProfile();
  }, []);

  const loadUserProfile = async () => {
    try {
      setLoading(true);
      const token = localStorage.getItem('tcg_lounge_token');
      if (!token) {
        navigate('/login');
        return;
      }

      // Get current user info
      const response = await authAPI.getCurrentUser();
      if (response.data.success) {
        const userData = response.data.user;
        setUser(userData);
        setNickname(userData.nickname || '');
        setEmail(userData.email || '');
      } else {
        setError('사용자 정보를 불러올 수 없습니다.');
      }
    } catch (error: any) {
      console.error('Error loading user profile:', error);
      setError('사용자 정보를 불러오는 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleProfileUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) return;
    
    if (!nickname.trim()) {
      setError('닉네임을 입력해주세요.');
      return;
    }

    if (!email.trim()) {
      setError('이메일을 입력해주세요.');
      return;
    }

    // Email validation
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      setError('올바른 이메일 형식을 입력해주세요.');
      return;
    }

    try {
      setSaving(true);
      setError('');
      
      const response = await userAPI.updateProfile(user.uid, {
        nickname: nickname.trim(),
        email: email.trim()
      });

      if (response.data.success) {
        alert('프로필이 성공적으로 업데이트되었습니다!');
        // Update local user state
        setUser(response.data.user);
        // Update localStorage if needed
        const storedUser = localStorage.getItem('tcg_lounge_user');
        if (storedUser) {
          const updatedUser = { ...JSON.parse(storedUser), ...response.data.user };
          localStorage.setItem('tcg_lounge_user', JSON.stringify(updatedUser));
        }
      } else {
        setError(response.data.message || '프로필 업데이트에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Error updating profile:', error);
      setError(error.response?.data?.message || '프로필 업데이트 중 오류가 발생했습니다.');
    } finally {
      setSaving(false);
    }
  };

  const handlePasswordChange = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (!user) return;

    if (!currentPassword || !newPassword || !confirmPassword) {
      setError('모든 비밀번호 필드를 입력해주세요.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('새 비밀번호와 확인 비밀번호가 일치하지 않습니다.');
      return;
    }

    if (newPassword.length < 6) {
      setError('새 비밀번호는 최소 6자 이상이어야 합니다.');
      return;
    }

    try {
      setSaving(true);
      setError('');
      
      const response = await userAPI.changePassword(user.uid, {
        currentPassword,
        newPassword
      });

      if (response.data.success) {
        alert('비밀번호가 성공적으로 변경되었습니다!');
        setCurrentPassword('');
        setNewPassword('');
        setConfirmPassword('');
      } else {
        setError(response.data.message || '비밀번호 변경에 실패했습니다.');
      }
    } catch (error: any) {
      console.error('Error changing password:', error);
      setError(error.response?.data?.message || '비밀번호 변경 중 오류가 발생했습니다.');
    } finally {
      setSaving(false);
    }
  };

  const handleGoBack = () => {
    navigate(-1);
  };

  if (loading) {
    return (
      <div className="profile-edit-loading">
        <div className="loading-spinner"></div>
        <p>사용자 정보를 불러오는 중...</p>
      </div>
    );
  }

  if (!user) {
    return (
      <div className="profile-edit-error">
        <h2>사용자 정보를 불러올 수 없습니다</h2>
        <button onClick={handleGoBack} className="go-back-btn">
          돌아가기
        </button>
      </div>
    );
  }

  return (
    <div className="profile-edit">
      <div className="profile-edit-container">
        <div className="profile-edit-header">
          <button onClick={handleGoBack} className="back-button">
            ← 돌아가기
          </button>
          <h1>프로필 설정</h1>
        </div>

        <div className="user-info-card">
          <div className="user-avatar">
            <div className="avatar-placeholder">
              {user.nickname ? user.nickname.charAt(0).toUpperCase() : user.userid.charAt(0).toUpperCase()}
            </div>
          </div>
          <div className="user-details">
            <h2>{user.nickname || user.userid}</h2>
            <p className="user-id">ID: {user.userid}</p>
            <p className="user-type">{user.userType === 'GAMER' ? '게이머' : '매장 운영자'}</p>
            <p className="join-date">가입일: {new Date(user.createdAt).toLocaleDateString('ko-KR')}</p>
          </div>
        </div>

        <div className="profile-tabs">
          <button 
            className={`tab-button ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            기본 정보
          </button>
          <button 
            className={`tab-button ${activeTab === 'password' ? 'active' : ''}`}
            onClick={() => setActiveTab('password')}
          >
            비밀번호 변경
          </button>
        </div>

        {error && (
          <div className="error-message">
            <span className="error-icon">⚠️</span>
            {error}
          </div>
        )}

        {activeTab === 'profile' && (
          <div className="profile-form-section">
            <h3>기본 정보 수정</h3>
            <form onSubmit={handleProfileUpdate} className="profile-form">
              <div className="form-group">
                <label htmlFor="userid">사용자 ID</label>
                <input
                  id="userid"
                  type="text"
                  value={user.userid}
                  disabled
                  className="form-input disabled"
                />
                <span className="form-help">사용자 ID는 변경할 수 없습니다.</span>
              </div>

              <div className="form-group">
                <label htmlFor="nickname">닉네임 *</label>
                <input
                  id="nickname"
                  type="text"
                  value={nickname}
                  onChange={(e) => setNickname(e.target.value)}
                  className="form-input"
                  placeholder="닉네임을 입력하세요"
                  maxLength={50}
                />
              </div>

              <div className="form-group">
                <label htmlFor="email">이메일 *</label>
                <input
                  id="email"
                  type="email"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="form-input"
                  placeholder="이메일을 입력하세요"
                />
              </div>

              <button 
                type="submit" 
                className="submit-button"
                disabled={saving}
              >
                {saving ? '저장 중...' : '프로필 업데이트'}
              </button>
            </form>
          </div>
        )}

        {activeTab === 'password' && (
          <div className="password-form-section">
            <h3>비밀번호 변경</h3>
            <form onSubmit={handlePasswordChange} className="password-form">
              <div className="form-group">
                <label htmlFor="currentPassword">현재 비밀번호 *</label>
                <input
                  id="currentPassword"
                  type="password"
                  value={currentPassword}
                  onChange={(e) => setCurrentPassword(e.target.value)}
                  className="form-input"
                  placeholder="현재 비밀번호를 입력하세요"
                />
              </div>

              <div className="form-group">
                <label htmlFor="newPassword">새 비밀번호 *</label>
                <input
                  id="newPassword"
                  type="password"
                  value={newPassword}
                  onChange={(e) => setNewPassword(e.target.value)}
                  className="form-input"
                  placeholder="새 비밀번호를 입력하세요 (최소 6자)"
                  minLength={6}
                />
              </div>

              <div className="form-group">
                <label htmlFor="confirmPassword">새 비밀번호 확인 *</label>
                <input
                  id="confirmPassword"
                  type="password"
                  value={confirmPassword}
                  onChange={(e) => setConfirmPassword(e.target.value)}
                  className="form-input"
                  placeholder="새 비밀번호를 다시 입력하세요"
                />
              </div>

              <button 
                type="submit" 
                className="submit-button password-submit"
                disabled={saving}
              >
                {saving ? '변경 중...' : '비밀번호 변경'}
              </button>
            </form>
          </div>
        )}
      </div>
    </div>
  );
};

export default ProfileEdit;