import { Helmet } from 'react-helmet-async';
// @mui
import { styled } from '@mui/material/styles';
import { Container, Typography, Divider, Stack, Button } from '@mui/material';
// hooks
import useResponsive from '../hooks/useResponsive';
// components
import Logo from '../components/logo';

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

export default function ConsumerPage() {
    const mdUp = useResponsive('up', 'md');
    const navigate = useNavigate();

    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login', { replace: true });
    }

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
                Hello, Welcome back!
                </Typography>
                <img src="/assets/illustrations/login_image.png" alt="login" />
                <Button variant="contained" onClick={handleLogout} style={{
                    backgroundColor: "#81A1C1", 
                    color: "#2E3440", 
                    marginTop: "20%", 
                    marginLeft: "20px", 
                    marginRight: "20px",
                    width:"30%",
                    }}>
                        Logout
                </Button>
            </StyledSection>
            )}

            <Container maxWidth="sm">
            <StyledContent>
                <Typography variant="h4" gutterBottom>
                Publisher information
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 5, mt:5 }}>
                    Name : {localStorage.getItem('name').replace(/"/g, "")}
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 5 }}>
                    Email : {localStorage.getItem('email').replace(/"/g, "")}
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 5 }}>
                    ID : {localStorage.getItem('id')}
                </Typography>
                <Divider sx={{ mb: 5 }} />
                <Typography variant="h4" gutterBottom>
                    How to request advertisements for your application?
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 2, mt:5 }}>
                    Perform a GET request to the following URL:
                </Typography>
                <Typography sx={{ color: '#81A1C1', mb: 5 }}>
                    <code>http://ads-api-mixit.deti/v1/ads?publisher_id={localStorage.getItem('id')}</code>
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 2 }}>
                    If you want to request a specific ad location or target audience age, you can add the following parameters:
                </Typography>
                <Typography sx={{ color: '#81A1C1', mb: 5 }}>
                    <code>http://ads-api-mixit.deti/v1/ads?publisher_id={localStorage.getItem('id')}&location=portugal&age_range=youth</code>
                </Typography>
                <Typography sx={{ color: 'text.secondary', mb: 2 }}>
                    The response will be a JSON object with the following format:
                </Typography>
                <Typography sx={{ color: '#81A1C1', mb: 5 }}>
                    <code>
                        {"{"}
                        <br/>
                        &nbsp;&nbsp;&nbsp;&nbsp;"ad": &lt; html code for the ad &gt;
                        <br/>
                        {"}"}
                    </code>
                </Typography>
            </StyledContent>
            </Container>
        </StyledRoot>
        </>
    );
}