// @ts-nocheck - Temporary fix for Material-UI Grid v7 TypeScript compatibility
import React, { useState, useEffect } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Grid,
  Typography,
  Box,
  Alert,
  Switch,
  FormControlLabel,
  Divider,
  IconButton,
  Avatar,
  Chip
} from '@mui/material';
import { Close, Upload, Delete, Image } from '@mui/icons-material';

interface CardData {
  cardId?: number;
  cardName: string;
  cardType: string;
  cardColor: string;
  rarity: string;
  cost: number;
  description: string;
  cardNumber?: string;
  cardImg?: string;
  createdAt?: string;
  updatedAt?: string;
  // Type-specific fields
  leaderSkill?: string;
  isAwakened?: boolean;
  burstSlot1?: number;
  burstSlot2?: number;
  burstSlot3?: number;
  power?: number;
  effect?: string;
  activationCondition?: string;
  isConsumable?: boolean;
  fieldEffect?: string;
  affectedColors?: string;
  affectedTypes?: string;
  spellEffect?: string;
  targetType?: string;
  burstValue?: number;
  images?: Array<{
    imageId: number;
    cardId: number;
    imageName: string;
    imageType: string;
    imageSize: number;
    imageCategory: string;
    width?: number;
    height?: number;
    uploadedAt: string;
  }>;
}

interface CardEditorProps {
  open: boolean;
  card?: CardData | null;
  onClose: () => void;
  onSave: (card: CardData) => void;
}

const CardEditor: React.FC<CardEditorProps> = ({ open, card, onClose, onSave }) => {
  const [formData, setFormData] = useState<CardData>({
    cardName: '',
    cardType: 'LEADER',
    cardColor: 'RED',
    rarity: 'COMMON',
    cost: 0,
    description: '',
    cardNumber: '',
    isAwakened: false,
    burstSlot1: 1,
    burstSlot2: 1,
    burstSlot3: 1,
    power: 0,
    isConsumable: true,
    burstValue: 1
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imagePreview, setImagePreview] = useState<string | null>(null);

  const cardTypes = ['LEADER', 'UNIT', 'ITEM', 'FIELD', 'SPELL'];
  const cardColors = ['RED', 'BLUE', 'GREEN', 'YELLOW', 'BLACK', 'COLORLESS'];
  const rarities = ['COMMON', 'RARE', 'SUPER_RARE', 'ULTRA_RARE', 'SECRET_RARE', 'LEGENDARY'];

  useEffect(() => {
    if (card) {
      setFormData({ ...card });
      if (card.images && card.images[0]) {
        setImagePreview(`http://localhost:8090/api/v1/cards/images/${card.images[0].imageId}`);
      }
    } else {
      setFormData({
        cardName: '',
        cardType: 'LEADER',
        cardColor: 'RED',
        rarity: 'COMMON',
        cost: 0,
        description: '',
        cardNumber: '',
        isAwakened: false,
        burstSlot1: 1,
        burstSlot2: 1,
        burstSlot3: 1,
        power: 0,
        isConsumable: true,
        burstValue: 1
      });
      setImagePreview(null);
    }
    setError('');
    setImageFile(null);
  }, [card, open]);

  const handleInputChange = (field: keyof CardData, value: any) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleImageSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setImageFile(file);
      const reader = new FileReader();
      reader.onload = () => {
        setImagePreview(reader.result as string);
      };
      reader.readAsDataURL(file);
    }
  };

  const uploadImage = async (cardId: number): Promise<boolean> => {
    if (!imageFile) return true;

    const formData = new FormData();
    formData.append('file', imageFile);
    formData.append('category', 'main');

    try {
      const response = await fetch(`http://localhost:8090/api/v1/admin/cards/${cardId}/images`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        },
        body: formData
      });

      if (!response.ok) {
        throw new Error('Image upload failed');
      }
      return true;
    } catch (error) {
      console.error('Error uploading image:', error);
      return false;
    }
  };

  const handleSave = async () => {
    if (!formData.cardName.trim()) {
      setError('카드명을 입력해주세요.');
      return;
    }

    setLoading(true);
    setError('');

    try {
      const url = card?.cardId 
        ? `http://localhost:8090/api/v1/admin/cards/${card.cardId}`
        : 'http://localhost:8090/api/v1/admin/cards';
      
      const method = card?.cardId ? 'PUT' : 'POST';
      
      const response = await fetch(url, {
        method,
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const result = await response.json();
        const cardId = result.cardId || card?.cardId;
        
        // Upload image if provided
        if (imageFile && cardId) {
          await uploadImage(cardId);
        }
        
        onSave({ ...formData, cardId });
        onClose();
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || 'Failed to save card');
      }
    } catch (error: any) {
      console.error('Error saving card:', error);
      setError(error.message || '카드 저장에 실패했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const renderTypeSpecificFields = () => {
    switch (formData.cardType) {
      case 'LEADER':
        return (
          <Box>
            <Typography variant="h6" sx={{ mb: 2, color: '#e91e63' }}>
              리더 카드 속성
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="리더 스킬"
                  multiline
                  rows={3}
                  value={formData.leaderSkill || ''}
                  onChange={(e) => handleInputChange('leaderSkill', e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.isAwakened || false}
                      onChange={(e) => handleInputChange('isAwakened', e.target.checked)}
                    />
                  }
                  label="각성 상태"
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 슬롯 1"
                  value={formData.burstSlot1 || 1}
                  onChange={(e) => handleInputChange('burstSlot1', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 슬롯 2"
                  value={formData.burstSlot2 || 1}
                  onChange={(e) => handleInputChange('burstSlot2', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
              <Grid item xs={4}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 슬롯 3"
                  value={formData.burstSlot3 || 1}
                  onChange={(e) => handleInputChange('burstSlot3', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 'UNIT':
        return (
          <Box>
            <Typography variant="h6" sx={{ mb: 2, color: '#2196f3' }}>
              유닛 카드 속성
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="파워"
                  value={formData.power || 0}
                  onChange={(e) => handleInputChange('power', parseInt(e.target.value) || 0)}
                  inputProps={{ min: 0 }}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 값"
                  value={formData.burstValue || 1}
                  onChange={(e) => handleInputChange('burstValue', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 'ITEM':
        return (
          <Box>
            <Typography variant="h6" sx={{ mb: 2, color: '#4caf50' }}>
              아이템 카드 속성
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="효과"
                  multiline
                  rows={3}
                  value={formData.effect || ''}
                  onChange={(e) => handleInputChange('effect', e.target.value)}
                />
              </Grid>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="발동 조건"
                  value={formData.activationCondition || ''}
                  onChange={(e) => handleInputChange('activationCondition', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <FormControlLabel
                  control={
                    <Switch
                      checked={formData.isConsumable !== false}
                      onChange={(e) => handleInputChange('isConsumable', e.target.checked)}
                    />
                  }
                  label="소모품"
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 값"
                  value={formData.burstValue || 1}
                  onChange={(e) => handleInputChange('burstValue', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 'FIELD':
        return (
          <Box>
            <Typography variant="h6" sx={{ mb: 2, color: '#ff9800' }}>
              필드 카드 속성
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="필드 효과"
                  multiline
                  rows={3}
                  value={formData.fieldEffect || ''}
                  onChange={(e) => handleInputChange('fieldEffect', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="영향받는 색상"
                  placeholder="예: RED,BLUE"
                  value={formData.affectedColors || ''}
                  onChange={(e) => handleInputChange('affectedColors', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="영향받는 타입"
                  placeholder="예: UNIT,ITEM"
                  value={formData.affectedTypes || ''}
                  onChange={(e) => handleInputChange('affectedTypes', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 값"
                  value={formData.burstValue || 1}
                  onChange={(e) => handleInputChange('burstValue', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      case 'SPELL':
        return (
          <Box>
            <Typography variant="h6" sx={{ mb: 2, color: '#9c27b0' }}>
              주문 카드 속성
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="주문 효과"
                  multiline
                  rows={3}
                  value={formData.spellEffect || ''}
                  onChange={(e) => handleInputChange('spellEffect', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  label="대상 타입"
                  placeholder="예: Any target, Unit only"
                  value={formData.targetType || ''}
                  onChange={(e) => handleInputChange('targetType', e.target.value)}
                />
              </Grid>
              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="버스트 값"
                  value={formData.burstValue || 1}
                  onChange={(e) => handleInputChange('burstValue', Math.min(3, Math.max(1, parseInt(e.target.value) || 1)))}
                  inputProps={{ min: 1, max: 3 }}
                />
              </Grid>
            </Grid>
          </Box>
        );

      default:
        return null;
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="md"
      fullWidth
      PaperProps={{
        sx: { minHeight: '80vh' }
      }}
    >
      <DialogTitle sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h6">
          {card ? '카드 편집' : '새 카드 생성'}
        </Typography>
        <IconButton onClick={onClose}>
          <Close />
        </IconButton>
      </DialogTitle>

      <DialogContent>
        {error && (
          <Alert severity="error" sx={{ mb: 2 }}>
            {error}
          </Alert>
        )}

        <Grid container spacing={3}>
          {/* Image Upload Section */}
          <Grid item xs={12} md={4}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              카드 이미지
            </Typography>
            <Box sx={{ textAlign: 'center' }}>
              <Avatar
                src={imagePreview || undefined}
                sx={{ 
                  width: 200, 
                  height: 280, 
                  borderRadius: 2,
                  bgcolor: '#f5f5f5',
                  border: '2px dashed #ccc',
                  mb: 2,
                  mx: 'auto'
                }}
              >
                <Image sx={{ fontSize: 60, color: '#bbb' }} />
              </Avatar>
              
              <input
                accept="image/*"
                style={{ display: 'none' }}
                id="image-upload"
                type="file"
                onChange={handleImageSelect}
              />
              <label htmlFor="image-upload">
                <Button
                  variant="outlined"
                  component="span"
                  startIcon={<Upload />}
                  sx={{ mb: 1 }}
                >
                  이미지 업로드
                </Button>
              </label>
              
              {imagePreview && (
                <Button
                  variant="text"
                  startIcon={<Delete />}
                  onClick={() => {
                    setImagePreview(null);
                    setImageFile(null);
                  }}
                  color="error"
                  size="small"
                  sx={{ display: 'block', mx: 'auto' }}
                >
                  이미지 제거
                </Button>
              )}
            </Box>
          </Grid>

          {/* Basic Information */}
          <Grid item xs={12} md={8}>
            <Typography variant="h6" sx={{ mb: 2 }}>
              기본 정보
            </Typography>
            
            <Grid container spacing={2}>
              <Grid item xs={12}>
                <TextField
                  fullWidth
                  required
                  label="카드명"
                  value={formData.cardName}
                  onChange={(e) => handleInputChange('cardName', e.target.value)}
                />
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth required>
                  <InputLabel>카드 타입</InputLabel>
                  <Select
                    value={formData.cardType}
                    onChange={(e) => handleInputChange('cardType', e.target.value)}
                    label="카드 타입"
                  >
                    {cardTypes.map(type => (
                      <MenuItem key={type} value={type}>
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                          <Chip 
                            label={type} 
                            size="small" 
                            sx={{ 
                              bgcolor: type === 'LEADER' ? '#e91e63' : 
                                      type === 'UNIT' ? '#2196f3' :
                                      type === 'ITEM' ? '#4caf50' :
                                      type === 'FIELD' ? '#ff9800' : '#9c27b0',
                              color: 'white'
                            }} 
                          />
                        </Box>
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth required>
                  <InputLabel>카드 색상</InputLabel>
                  <Select
                    value={formData.cardColor}
                    onChange={(e) => handleInputChange('cardColor', e.target.value)}
                    label="카드 색상"
                  >
                    {cardColors.map(color => (
                      <MenuItem key={color} value={color}>
                        {color}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={6}>
                <FormControl fullWidth required>
                  <InputLabel>레어도</InputLabel>
                  <Select
                    value={formData.rarity}
                    onChange={(e) => handleInputChange('rarity', e.target.value)}
                    label="레어도"
                  >
                    {rarities.map(rarity => (
                      <MenuItem key={rarity} value={rarity}>
                        {rarity.replace('_', ' ')}
                      </MenuItem>
                    ))}
                  </Select>
                </FormControl>
              </Grid>

              <Grid item xs={6}>
                <TextField
                  fullWidth
                  type="number"
                  label="코스트"
                  value={formData.cost}
                  onChange={(e) => handleInputChange('cost', parseInt(e.target.value) || 0)}
                  inputProps={{ min: 0 }}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="카드 번호"
                  placeholder="예: EST-001"
                  value={formData.cardNumber || ''}
                  onChange={(e) => handleInputChange('cardNumber', e.target.value)}
                />
              </Grid>

              <Grid item xs={12}>
                <TextField
                  fullWidth
                  label="설명"
                  multiline
                  rows={3}
                  value={formData.description}
                  onChange={(e) => handleInputChange('description', e.target.value)}
                />
              </Grid>
            </Grid>
          </Grid>

          {/* Type-specific fields */}
          <Grid item xs={12}>
            <Divider sx={{ my: 2 }} />
            {renderTypeSpecificFields()}
          </Grid>
        </Grid>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose}>
          취소
        </Button>
        <Button
          onClick={handleSave}
          variant="contained"
          disabled={loading}
        >
          {loading ? '저장 중...' : (card ? '수정' : '생성')}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default CardEditor;