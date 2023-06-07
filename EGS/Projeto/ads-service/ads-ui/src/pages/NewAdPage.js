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
import { AdForm } from '../sections/ad';

import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

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

export default function NewAdPage() {
    const mdUp = useResponsive('up', 'md');

    const navigate = useNavigate();

    useEffect(() => {
        if(localStorage.getItem('type') == '"C"'){
            console.log("user is a consumer");
            navigate('/consumer');
            return;
        }
        if (localStorage.getItem('email') != null) {
            console.log(localStorage.getItem('email') + " is logged in");
            console.log("type: " + localStorage.getItem('type'));
            if(localStorage.getItem('type') != '"A"') {
                console.log("not an advertiser");
                navigate('/login');
            }
        }
        else {
            console.log("no user");
            navigate('/login');
        }
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
                Upload your ad !
                </Typography>
                <img src="/assets/illustrations/new_ad.png" alt="new-ad" />
            </StyledSection>
            )}

            <Container maxWidth="sm">
            <StyledContent>
                <Typography variant="h4" gutterBottom>
                Advertisement Details
                </Typography>
                <AdForm />
            </StyledContent>
            </Container>
        </StyledRoot>
        </>
    );
}
