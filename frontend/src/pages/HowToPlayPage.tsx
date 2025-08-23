import React, { useState } from 'react';
import {
  Container,
  Typography,
  Box,
  Button,
  Card,
  CardContent,
  Grid,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Avatar,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Divider,
  Paper,
  IconButton
} from '@mui/material';
import {
  ExpandMore,
  PlayArrow,
  Casino,
  EmojiEvents,
  School,
  VideoLibrary,
  Download,
  Help,
  Star,
  TrendingUp,
  Speed,
  Shield
} from '@mui/icons-material';
import Navigation from '../components/Navigation';

const HowToPlayPage: React.FC = () => {
  const [activeStep, setActiveStep] = useState(0);

  const gameSteps = [
    {
      label: '게임 준비',
      description: '각 플레이어는 60장의 덱과 1장의 리더 카드를 준비합니다.',
      details: [
        '덱을 잘 섞고 7장의 카드를 뽑습니다',
        '리더 카드를 테이블에 놓습니다',
        '라이프 포인트를 리더 카드의 수치로 설정합니다',
        '선공을 정하기 위해 주사위를 굴립니다'
      ]
    },
    {
      label: '턴 시작',
      description: '매 턴마다 카드를 1장 뽑고 마나를 1개씩 증가시킵니다.',
      details: [
        '덱에서 카드 1장을 뽑습니다',
        '마나가 1 증가합니다',
        '이전 턴에 사용한 카드들이 언탭됩니다',
        '리더 스킬을 사용할 수 있는지 확인합니다'
      ]
    },
    {
      label: '메인 페이즈',
      description: '마나를 사용하여 카드를 플레이하고 전략을 실행합니다.',
      details: [
        '손에서 카드를 플레이할 수 있습니다',
        '각 카드는 마나 비용이 필요합니다',
        '유닛 카드는 필드에 소환됩니다',
        '스펠 카드는 즉시 효과가 발동됩니다'
      ]
    },
    {
      label: '전투 페이즈',
      description: '필드의 유닛들로 상대를 공격합니다.',
      details: [
        '자신의 유닛으로 공격을 선언합니다',
        '상대방은 블록할 유닛을 선택합니다',
        '데미지를 계산하고 적용합니다',
        '리더를 직접 공격할 수 있습니다'
      ]
    },
    {
      label: '턴 종료',
      description: '턴을 끝내고 상대방의 턴으로 넘어갑니다.',
      details: [
        '손패가 7장을 초과하면 버립니다',
        '이번 턴에 소환된 유닛은 소환술에 걸립니다',
        '턴 종료 효과들이 발동됩니다',
        '상대방의 턴이 시작됩니다'
      ]
    }
  ];

  const cardTypes = [
    {
      name: '리더 카드',
      description: '게임의 핵심이 되는 카드로, 각자 고유한 능력을 가집니다.',
      icon: <Star sx={{ color: '#f4c87a' }} />,
      details: [
        '라이프 포인트를 가집니다',
        '특별한 리더 스킬이 있습니다',
        '게임 중 교체할 수 없습니다',
        '리더가 쓰러지면 패배합니다'
      ]
    },
    {
      name: '유닛 카드',
      description: '전투를 담당하는 카드로, 공격력과 방어력을 가집니다.',
      icon: <Shield sx={{ color: '#4caf50' }} />,
      details: [
        '공격력과 방어력 수치가 있습니다',
        '필드에서 전투를 수행합니다',
        '특수 능력을 가질 수 있습니다',
        '마나 비용을 지불해야 소환 가능합니다'
      ]
    },
    {
      name: '스펠 카드',
      description: '즉시 효과를 발동하는 마법 카드입니다.',
      icon: <Speed sx={{ color: '#2196f3' }} />,
      details: [
        '사용 즉시 효과가 발동됩니다',
        '일회성 효과를 가집니다',
        '전략적 타이밍이 중요합니다',
        '사용 후 묘지로 갑니다'
      ]
    },
    {
      name: '아이템 카드',
      description: '유닛을 강화하거나 특별한 효과를 주는 카드입니다.',
      icon: <TrendingUp sx={{ color: '#ff9800' }} />,
      details: [
        '유닛에 장착하여 능력을 향상시킵니다',
        '지속적인 효과를 제공합니다',
        '일부는 독립적으로 사용 가능합니다',
        '전략적 조합이 가능합니다'
      ]
    }
  ];

  const strategies = [
    {
      title: '어그로 전략',
      description: '빠른 공격으로 상대를 압박하는 전술',
      tips: ['저비용 유닛을 빠르게 전개', '상대 리더를 직접 공격', '게임을 빠르게 끝내기']
    },
    {
      title: '컨트롤 전략',
      description: '상대의 움직임을 제어하며 후반에 승부하는 전술',
      tips: ['상대 유닛 제거에 집중', '카드 드로우로 손패 확보', '강력한 고비용 카드로 마무리']
    },
    {
      title: '콤보 전략',
      description: '특정 카드들의 조합으로 강력한 효과를 만드는 전술',
      tips: ['핵심 콤보 카드 확보', '콤보 완성까지 버티기', '한 번에 큰 데미지 주기']
    }
  ];

  return (
    <Box>
      <Navigation />

      {/* 메인 콘텐츠 */}
      <Container maxWidth="lg" sx={{ pt: 12, pb: 4 }}>
        {/* 페이지 헤더 */}
        <Box sx={{ mb: 6, textAlign: 'center' }}>
          <Typography variant="h3" sx={{ fontWeight: 'bold', mb: 2, color: '#333' }}>
            놀이방법
          </Typography>
          <Typography variant="body1" color="text.secondary" sx={{ mb: 4 }}>
            ESTELA TCG의 기본 룰부터 고급 전략까지 모든 것을 배워보세요
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, justifyContent: 'center' }}>
            <Button 
              variant="contained" 
              startIcon={<VideoLibrary />}
              sx={{ backgroundColor: '#b8191c', '&:hover': { backgroundColor: '#a01018' } }}
            >
              튜토리얼 영상
            </Button>
            <Button 
              variant="outlined" 
              startIcon={<Download />}
              sx={{ borderColor: '#b8191c', color: '#b8191c' }}
            >
              룰북 다운로드
            </Button>
          </Box>
        </Box>

        {/* 게임 진행 순서 */}
        <Box sx={{ mb: 6 }}>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4, color: '#333', textAlign: 'center' }}>
            게임 진행 순서
          </Typography>
          <Paper sx={{ p: 4 }}>
            <Stepper activeStep={activeStep} orientation="vertical">
              {gameSteps.map((step, index) => (
                <Step key={step.label}>
                  <StepLabel 
                    onClick={() => setActiveStep(index)}
                    sx={{ cursor: 'pointer' }}
                  >
                    <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                      {step.label}
                    </Typography>
                  </StepLabel>
                  <StepContent>
                    <Typography variant="body1" sx={{ mb: 2 }}>
                      {step.description}
                    </Typography>
                    <List dense>
                      {step.details.map((detail, idx) => (
                        <ListItem key={idx}>
                          <ListItemIcon>
                            <PlayArrow sx={{ color: '#b8191c' }} />
                          </ListItemIcon>
                          <ListItemText primary={detail} />
                        </ListItem>
                      ))}
                    </List>
                    <Box sx={{ mb: 1 }}>
                      <Button
                        variant="contained"
                        onClick={() => setActiveStep(index + 1)}
                        disabled={index === gameSteps.length - 1}
                        sx={{ mt: 1, mr: 1, backgroundColor: '#b8191c' }}
                      >
                        다음 단계
                      </Button>
                      <Button
                        disabled={index === 0}
                        onClick={() => setActiveStep(index - 1)}
                        sx={{ mt: 1, mr: 1 }}
                      >
                        이전 단계
                      </Button>
                    </Box>
                  </StepContent>
                </Step>
              ))}
            </Stepper>
          </Paper>
        </Box>

        {/* 카드 타입 설명 */}
        <Box sx={{ mb: 6 }}>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4, color: '#333', textAlign: 'center' }}>
            카드 타입
          </Typography>
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {cardTypes.map((type, index) => (
              <Box key={index} sx={{ flex: '1 1 400px', minWidth: '400px' }}>
                <Card sx={{ height: '100%' }}>
                  <CardContent>
                    <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                      <Avatar sx={{ mr: 2, backgroundColor: 'transparent' }}>
                        {type.icon}
                      </Avatar>
                      <Typography variant="h5" sx={{ fontWeight: 'bold' }}>
                        {type.name}
                      </Typography>
                    </Box>
                    <Typography variant="body1" sx={{ mb: 2 }} color="text.secondary">
                      {type.description}
                    </Typography>
                    <Divider sx={{ my: 2 }} />
                    <List dense>
                      {type.details.map((detail, idx) => (
                        <ListItem key={idx}>
                          <ListItemIcon>
                            <Casino sx={{ color: '#4caf50', fontSize: 20 }} />
                          </ListItemIcon>
                          <ListItemText 
                            primary={detail}
                            primaryTypographyProps={{ variant: 'body2' }}
                          />
                        </ListItem>
                      ))}
                    </List>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Box>

        {/* 전략 가이드 */}
        <Box sx={{ mb: 6 }}>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4, color: '#333', textAlign: 'center' }}>
            기본 전략
          </Typography>
          <Box sx={{ display: 'flex', gap: 3, flexWrap: 'wrap' }}>
            {strategies.map((strategy, index) => (
              <Box key={index} sx={{ flex: '1 1 350px', minWidth: '350px' }}>
                <Card sx={{ 
                  height: '100%',
                  '&:hover': { 
                    transform: 'translateY(-5px)',
                    boxShadow: '0 8px 25px rgba(0,0,0,0.15)'
                  },
                  transition: 'all 0.3s'
                }}>
                  <CardContent>
                    <Typography variant="h5" sx={{ fontWeight: 'bold', mb: 2, color: '#b8191c' }}>
                      {strategy.title}
                    </Typography>
                    <Typography variant="body1" sx={{ mb: 2 }} color="text.secondary">
                      {strategy.description}
                    </Typography>
                    <Divider sx={{ my: 2 }} />
                    <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 1 }}>
                      핵심 포인트:
                    </Typography>
                    <List dense>
                      {strategy.tips.map((tip, idx) => (
                        <ListItem key={idx}>
                          <ListItemIcon>
                            <EmojiEvents sx={{ color: '#f4c87a', fontSize: 20 }} />
                          </ListItemIcon>
                          <ListItemText 
                            primary={tip}
                            primaryTypographyProps={{ variant: 'body2' }}
                          />
                        </ListItem>
                      ))}
                    </List>
                  </CardContent>
                </Card>
              </Box>
            ))}
          </Box>
        </Box>

        {/* FAQ 섹션 */}
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold', mb: 4, color: '#333', textAlign: 'center' }}>
            자주 묻는 질문
          </Typography>
          <Box sx={{ maxWidth: 800, mx: 'auto' }}>
            <Accordion>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  게임을 시작하려면 무엇이 필요한가요?
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>
                  ESTELA TCG를 시작하려면 스타터 덱 하나와 기본 룰북만 있으면 됩니다. 
                  스타터 덱에는 60장의 카드와 1장의 리더 카드가 포함되어 있어 즉시 게임을 시작할 수 있습니다.
                </Typography>
              </AccordionDetails>
            </Accordion>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  덱은 몇 장으로 구성해야 하나요?
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>
                  표준 덱은 정확히 60장으로 구성해야 합니다. 같은 카드는 최대 3장까지만 넣을 수 있으며, 
                  일부 전설 등급 카드는 1장 제한이 있습니다.
                </Typography>
              </AccordionDetails>
            </Accordion>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  게임 시간은 얼마나 걸리나요?
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>
                  일반적으로 한 게임은 15-30분 정도 소요됩니다. 
                  플레이어의 숙련도와 덱 타입에 따라 시간이 달라질 수 있습니다.
                </Typography>
              </AccordionDetails>
            </Accordion>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  온라인으로도 플레이할 수 있나요?
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>
                  네, ESTELA TCG 온라인 플랫폼을 통해 전 세계 플레이어들과 대전할 수 있습니다. 
                  모바일 앱도 제공되어 언제 어디서나 게임을 즐길 수 있습니다.
                </Typography>
              </AccordionDetails>
            </Accordion>

            <Accordion>
              <AccordionSummary expandIcon={<ExpandMore />}>
                <Typography variant="h6" sx={{ fontWeight: 'bold' }}>
                  대회에 참가하려면 어떻게 해야 하나요?
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Typography>
                  웹사이트의 대회 섹션에서 예정된 대회 목록을 확인하고 신청할 수 있습니다. 
                  초보자부터 프로까지 다양한 레벨의 대회가 정기적으로 개최됩니다.
                </Typography>
              </AccordionDetails>
            </Accordion>
          </Box>
        </Box>
      </Container>
    </Box>
  );
};

export default HowToPlayPage;