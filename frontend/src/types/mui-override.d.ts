import { GridProps as MuiGridProps } from '@mui/material';

declare module '@mui/material' {
  interface GridProps extends MuiGridProps {
    item?: boolean;
    xs?: boolean | number;
    sm?: boolean | number;
    md?: boolean | number;
    lg?: boolean | number;
    xl?: boolean | number;
  }
}