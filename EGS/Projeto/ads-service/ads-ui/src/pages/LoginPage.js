import { Helmet } from 'react-helmet-async';
// @mui
import { styled } from '@mui/material/styles';
import { Link, Container, Typography, Divider, Stack, Button } from '@mui/material';
// hooks
import useResponsive from '../hooks/useResponsive';
// components
import Logo from '../components/logo';
import Iconify from '../components/iconify';
// sections
import { LoginForm } from '../sections/auth/login';

import { useEffect } from 'react';

// ----------------------------------------------------------------------

const StyledRoot = styled('div')(({ theme }) => ({
  [theme.breakpoints.up('md')]: {
    display: 'flex',
  },
}));

const StyledSection = styled('div')(({ theme }) => ({
  width: '100%',
  maxWidth: 480,
  display: 'flex',
  flexDirection: 'column',
  justifyContent: 'center',
  boxShadow: theme.customShadows.card,
  backgroundColor: theme.palette.background.default,
}));

const StyledContent = styled('div')(({ theme }) => ({
  maxWidth: 480,
  margin: 'auto',
  minHeight: '100vh',
  display: 'flex',
  justifyContent: 'center',
  flexDirection: 'column',
  padding: theme.spacing(12, 0),
}));

// ----------------------------------------------------------------------

export default function LoginPage() {
    const mdUp = useResponsive('up', 'md');

    useEffect(() => {
        localStorage.clear();
        }, []);

    return (
        <>
        <Helmet>
            <title> Login </title>
        </Helmet>

        <StyledRoot>
            <Logo
            sx={{
                position: 'fixed',
                top: { xs: 16, sm: 24, md: 40 },
                left: { xs: 16, sm: 24, md: 40 },
            }}
            />

            {mdUp && (
            <StyledSection >
                <Typography variant="h3" sx={{ px: 5, mt: 10, mb: 5 }}>
                Hi, Welcome back
                </Typography>
                <img src="/assets/illustrations/login_image.png" alt="login" />
            </StyledSection>
            )}

            <Container maxWidth="sm">
            <StyledContent>
                <Typography variant="h4" gutterBottom>
                Sign in
                </Typography>

                <Typography variant="body2" sx={{ mb: 5 }}>
                Don’t have an account? {''}
                <Link variant="subtitle2" style={{cursor:"pointer"}} onClick={() => window.location.href = "/register"}> Sign up </Link>
                </Typography>

                <LoginForm />

            </StyledContent>
            </Container>
        </StyledRoot>
        </>
    );
}
