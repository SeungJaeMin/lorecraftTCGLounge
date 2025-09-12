import React, { useState, useEffect } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  IconButton,
  Chip,
  TextField,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Grid,
  Avatar,
  Alert,
  Pagination,
  Tooltip,
  Menu,
  MenuItem as MenuItemComponent,
  ListItemIcon,
  ListItemText,
  Snackbar
} from '@mui/material';
import {
  Add,
  Edit,
  Delete,
  Search,
  FilterList,
  Image,
  Visibility,
  MoreVert,
  Upload,
  Download,
  Refresh
} from '@mui/icons-material';

interface CardData {
  cardId: number;
  cardName: string;
  cardType: string;
  cardColor: string;
  rarity: string;
  cost: number;
  description: string;
  cardNumber?: string;
  cardImg?: string;
  createdAt: string;
  updatedAt: string;
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

interface CardManagementProps {
  onCreateNew: () => void;
  onEdit: (card: CardData) => void;
}

const CardManagement: React.FC<CardManagementProps> = ({ onCreateNew, onEdit }) => {
  const [cards, setCards] = useState<CardData[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCardType, setSelectedCardType] = useState('');
  const [selectedCardColor, setSelectedCardColor] = useState('');
  const [selectedRarity, setSelectedRarity] = useState('');
  const [selectedCard, setSelectedCard] = useState<CardData | null>(null);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);
  const [cardToDelete, setCardToDelete] = useState<CardData | null>(null);
  const [menuAnchor, setMenuAnchor] = useState<null | HTMLElement>(null);
  const [selectedCards, setSelectedCards] = useState<number[]>([]);
  const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' as 'success' | 'error' });

  const cardTypes = ['', 'LEADER', 'UNIT', 'ITEM', 'FIELD', 'SPELL'];
  const cardColors = ['', 'RED', 'BLUE', 'GREEN', 'YELLOW', 'BLACK', 'COLORLESS'];
  const rarities = ['', 'COMMON', 'RARE', 'SUPER_RARE', 'ULTRA_RARE', 'SECRET_RARE', 'LEGENDARY'];

  const fetchCards = async () => {
    try {
      setLoading(true);
      const params = new URLSearchParams({
        page: page.toString(),
        size: '20',
        sort: 'createdAt,desc'
      });
      
      if (searchTerm) params.append('searchTerm', searchTerm);
      if (selectedCardType) params.append('cardType', selectedCardType);
      if (selectedCardColor) params.append('cardColor', selectedCardColor);
      if (selectedRarity) params.append('rarity', selectedRarity);

      const response = await fetch(`http://localhost:8090/api/v1/cards/admin?${params}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        }
      });

      if (response.ok) {
        const data = await response.json();
        setCards(data.content);
        setTotalPages(data.totalPages);
        setTotalElements(data.totalElements);
      } else {
        throw new Error('Failed to fetch cards');
      }
    } catch (error) {
      console.error('Error fetching cards:', error);
      setSnackbar({
        open: true,
        message: '카드 목록을 불러오는데 실패했습니다.',
        severity: 'error'
      });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCards();
  }, [page, selectedCardType, selectedCardColor, selectedRarity]);

  const handleSearch = () => {
    setPage(0);
    fetchCards();
  };

  const handleDeleteCard = async (card: CardData) => {
    try {
      const response = await fetch(`http://localhost:8090/api/v1/cards/${card.cardId}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        }
      });

      if (response.ok) {
        setSnackbar({
          open: true,
          message: '카드가 성공적으로 삭제되었습니다.',
          severity: 'success'
        });
        fetchCards();
      } else {
        throw new Error('Failed to delete card');
      }
    } catch (error) {
      console.error('Error deleting card:', error);
      setSnackbar({
        open: true,
        message: '카드 삭제에 실패했습니다.',
        severity: 'error'
      });
    }
    setDeleteDialogOpen(false);
    setCardToDelete(null);
  };

  const handleBulkDelete = async () => {
    if (selectedCards.length === 0) return;

    try {
      const response = await fetch(`http://localhost:8090/api/v1/cards/bulk-delete`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('adminToken')}`
        },
        body: JSON.stringify(selectedCards)
      });

      if (response.ok) {
        setSnackbar({
          open: true,
          message: `${selectedCards.length}개의 카드가 성공적으로 삭제되었습니다.`,
          severity: 'success'
        });
        setSelectedCards([]);
        fetchCards();
      } else {
        throw new Error('Failed to bulk delete cards');
      }
    } catch (error) {
      console.error('Error bulk deleting cards:', error);
      setSnackbar({
        open: true,
        message: '카드 일괄 삭제에 실패했습니다.',
        severity: 'error'
      });
    }
  };

  const getCardTypeColor = (type: string) => {
    switch (type) {
      case 'LEADER': return '#e91e63';
      case 'UNIT': return '#2196f3';
      case 'ITEM': return '#4caf50';
      case 'FIELD': return '#ff9800';
      case 'SPELL': return '#9c27b0';
      default: return '#757575';
    }
  };

  const getCardColorChip = (color: string) => {
    const colorMap: { [key: string]: string } = {
      'RED': '#f44336',
      'BLUE': '#2196f3',
      'GREEN': '#4caf50',
      'YELLOW': '#ffeb3b',
      'BLACK': '#424242',
      'COLORLESS': '#9e9e9e'
    };
    
    return (
      <Chip 
        label={color} 
        size="small" 
        sx={{ 
          backgroundColor: colorMap[color] || '#9e9e9e',
          color: color === 'YELLOW' ? '#000' : '#fff',
          minWidth: '70px'
        }} 
      />
    );
  };

  const getRarityChip = (rarity: string) => {
    const rarityColors: { [key: string]: string } = {
      'COMMON': '#9e9e9e',
      'RARE': '#2196f3',
      'SUPER_RARE': '#9c27b0',
      'ULTRA_RARE': '#ff9800',
      'SECRET_RARE': '#f44336',
      'LEGENDARY': '#ffd700'
    };
    
    return (
      <Chip 
        label={rarity.replace('_', ' ')} 
        size="small" 
        sx={{ 
          backgroundColor: rarityColors[rarity] || '#9e9e9e',
          color: '#fff',
          fontWeight: 'bold'
        }} 
      />
    );
  };

  return (
    <Card>
      <CardContent>
        {/* Header */}
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
            카드 관리 ({totalElements.toLocaleString()}개)
          </Typography>
          <Box sx={{ display: 'flex', gap: 2 }}>
            <Button
              variant="contained"
              startIcon={<Add />}
              onClick={onCreateNew}
              sx={{ backgroundColor: '#1976d2' }}
            >
              새 카드 추가
            </Button>
            {selectedCards.length > 0 && (
              <Button
                variant="contained"
                startIcon={<Delete />}
                onClick={handleBulkDelete}
                color="error"
              >
                선택한 카드 삭제 ({selectedCards.length})
              </Button>
            )}
            <IconButton onClick={fetchCards} title="새로고침">
              <Refresh />
            </IconButton>
          </Box>
        </Box>

        {/* Filters */}
        <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap', alignItems: 'center' }}>
          <TextField
            size="small"
            placeholder="카드명 또는 설명 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
            sx={{ minWidth: 200 }}
            InputProps={{
              endAdornment: (
                <IconButton size="small" onClick={handleSearch}>
                  <Search />
                </IconButton>
              )
            }}
          />
          
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>카드 타입</InputLabel>
            <Select
              value={selectedCardType}
              onChange={(e) => setSelectedCardType(e.target.value)}
              label="카드 타입"
            >
              {cardTypes.map(type => (
                <MenuItem key={type} value={type}>
                  {type || '전체'}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>카드 색상</InputLabel>
            <Select
              value={selectedCardColor}
              onChange={(e) => setSelectedCardColor(e.target.value)}
              label="카드 색상"
            >
              {cardColors.map(color => (
                <MenuItem key={color} value={color}>
                  {color || '전체'}
                </MenuItem>
              ))}
            </Select>
          </FormControl>

          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>레어도</InputLabel>
            <Select
              value={selectedRarity}
              onChange={(e) => setSelectedRarity(e.target.value)}
              label="레어도"
            >
              {rarities.map(rarity => (
                <MenuItem key={rarity} value={rarity}>
                  {rarity ? rarity.replace('_', ' ') : '전체'}
                </MenuItem>
              ))}
            </Select>
          </FormControl>
        </Box>

        {/* Cards Table */}
        <TableContainer>
          <Table>
            <TableHead>
              <TableRow>
                <TableCell padding="checkbox">
                  <input
                    type="checkbox"
                    onChange={(e) => {
                      if (e.target.checked) {
                        setSelectedCards(cards.map(card => card.cardId));
                      } else {
                        setSelectedCards([]);
                      }
                    }}
                    checked={selectedCards.length === cards.length && cards.length > 0}
                  />
                </TableCell>
                <TableCell>이미지</TableCell>
                <TableCell>카드명</TableCell>
                <TableCell>타입</TableCell>
                <TableCell>색상</TableCell>
                <TableCell>레어도</TableCell>
                <TableCell>코스트</TableCell>
                <TableCell>카드 번호</TableCell>
                <TableCell>상세 정보</TableCell>
                <TableCell>생성일</TableCell>
                <TableCell align="center">액션</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {loading ? (
                <TableRow>
                  <TableCell colSpan={11} align="center" sx={{ py: 4 }}>
                    로딩 중...
                  </TableCell>
                </TableRow>
              ) : cards.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={11} align="center" sx={{ py: 4 }}>
                    카드가 없습니다.
                  </TableCell>
                </TableRow>
              ) : (
                cards.map((card) => (
                  <TableRow key={card.cardId} hover>
                    <TableCell padding="checkbox">
                      <input
                        type="checkbox"
                        checked={selectedCards.includes(card.cardId)}
                        onChange={(e) => {
                          if (e.target.checked) {
                            setSelectedCards([...selectedCards, card.cardId]);
                          } else {
                            setSelectedCards(selectedCards.filter(id => id !== card.cardId));
                          }
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      <Avatar
                        src={card.images && card.images[0] ? 
                          `http://localhost:8090/api/v1/cards/images/${card.images[0].imageId}` : 
                          undefined}
                        sx={{ width: 48, height: 48, bgcolor: '#e0e0e0' }}
                      >
                        <Image />
                      </Avatar>
                    </TableCell>
                    <TableCell>
                      <Box>
                        <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                          {card.cardName}
                        </Typography>
                        <Typography variant="caption" color="text.secondary">
                          ID: {card.cardId}
                        </Typography>
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Chip 
                        label={card.cardType} 
                        size="small"
                        sx={{ 
                          backgroundColor: getCardTypeColor(card.cardType),
                          color: 'white',
                          fontWeight: 'bold'
                        }}
                      />
                    </TableCell>
                    <TableCell>
                      {getCardColorChip(card.cardColor)}
                    </TableCell>
                    <TableCell>
                      {getRarityChip(card.rarity)}
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" sx={{ fontWeight: 'bold' }}>
                        {card.cost}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2" color="text.secondary">
                        {card.cardNumber || '-'}
                      </Typography>
                    </TableCell>
                    <TableCell>
                      <Box sx={{ maxWidth: 200 }}>
                        {card.cardType === 'LEADER' && card.leaderSkill && (
                          <Typography variant="caption" display="block">
                            스킬: {card.leaderSkill.substring(0, 30)}...
                          </Typography>
                        )}
                        {card.cardType === 'UNIT' && card.power !== undefined && (
                          <Typography variant="caption" display="block">
                            파워: {card.power}
                          </Typography>
                        )}
                        {card.cardType === 'ITEM' && card.effect && (
                          <Typography variant="caption" display="block">
                            효과: {card.effect.substring(0, 30)}...
                          </Typography>
                        )}
                        {card.cardType === 'FIELD' && card.fieldEffect && (
                          <Typography variant="caption" display="block">
                            필드 효과: {card.fieldEffect.substring(0, 30)}...
                          </Typography>
                        )}
                        {card.cardType === 'SPELL' && card.spellEffect && (
                          <Typography variant="caption" display="block">
                            주문 효과: {card.spellEffect.substring(0, 30)}...
                          </Typography>
                        )}
                      </Box>
                    </TableCell>
                    <TableCell>
                      <Typography variant="body2">
                        {new Date(card.createdAt).toLocaleDateString()}
                      </Typography>
                    </TableCell>
                    <TableCell align="center">
                      <IconButton
                        size="small"
                        onClick={() => onEdit(card)}
                        color="primary"
                      >
                        <Edit />
                      </IconButton>
                      <IconButton
                        size="small"
                        onClick={() => {
                          setCardToDelete(card);
                          setDeleteDialogOpen(true);
                        }}
                        color="error"
                      >
                        <Delete />
                      </IconButton>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </TableContainer>

        {/* Pagination */}
        {totalPages > 1 && (
          <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
            <Pagination
              count={totalPages}
              page={page + 1}
              onChange={(event, value) => setPage(value - 1)}
              color="primary"
            />
          </Box>
        )}

        {/* Delete Confirmation Dialog */}
        <Dialog
          open={deleteDialogOpen}
          onClose={() => setDeleteDialogOpen(false)}
        >
          <DialogTitle>카드 삭제</DialogTitle>
          <DialogContent>
            <Typography>
              "{cardToDelete?.cardName}" 카드를 삭제하시겠습니까?
              이 작업은 되돌릴 수 없습니다.
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeleteDialogOpen(false)}>
              취소
            </Button>
            <Button 
              onClick={() => cardToDelete && handleDeleteCard(cardToDelete)}
              color="error"
              variant="contained"
            >
              삭제
            </Button>
          </DialogActions>
        </Dialog>

        {/* Snackbar */}
        <Snackbar
          open={snackbar.open}
          autoHideDuration={6000}
          onClose={() => setSnackbar({ ...snackbar, open: false })}
        >
          <Alert
            onClose={() => setSnackbar({ ...snackbar, open: false })}
            severity={snackbar.severity}
            sx={{ width: '100%' }}
          >
            {snackbar.message}
          </Alert>
        </Snackbar>
      </CardContent>
    </Card>
  );
};

export default CardManagement;