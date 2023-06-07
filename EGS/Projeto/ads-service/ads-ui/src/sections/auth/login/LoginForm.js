import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// @mui
import { Link, Stack, IconButton, InputAdornment, TextField, Checkbox, Button } from '@mui/material';
import { button } from '@mui/lab';
// components
import Iconify from '../../../components/iconify';

// ----------------------------------------------------------------------

export default function LoginForm() {
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');
    console.log("host address: " + hostAddress)
    console.log(ip);
    console.log(port);

    const [showPassword, setShowPassword] = useState(false);

    const handleClick = (email, password) => {
        //fetch
        fetch('http://ads-api-mixit.deti/v1/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                password: password,
            }),
        })
        //if response status is 200, navigate to dashboard else alert
        .then((response) => {
            if (response.status === 200) {
                //get json of response
                response.json().then((data) => {
                    localStorage.setItem('email', JSON.stringify(data.email));
                    localStorage.setItem('token', JSON.stringify(data.token));
                    localStorage.setItem('id', JSON.stringify(data.id));
                    localStorage.setItem('name', JSON.stringify(data.name));
                    localStorage.setItem('type', JSON.stringify(data.type));
                    console.log(localStorage.getItem('email') + " is logged in as " + localStorage.getItem('type'));
                });
                navigate('/dashboard', { replace: true });
            } else {
                alert('Invalid credentials');
            }
        }
        )
        .catch((error) => {
            console.error('Error:', error);
        });
    }

    return (
        <>
        <Stack spacing={3}>
            <TextField name="email" label="Email address" id='email' onChange={(e) => setEmail(e.target.value)} onInput={(e) => setEmail(e.target.value)} />

            <TextField
                name="password"
                label="Password"
                id='password'
                onChange={(e) => setPassword(e.target.value)}
                type={showPassword ? 'text' : 'password'}
                InputProps={{
                    endAdornment: (
                    <InputAdornment position="end">
                        <IconButton onClick={() => setShowPassword(!showPassword)} edge="end">
                        <Iconify icon={showPassword ? 'eva:eye-fill' : 'eva:eye-off-fill'} />
                        </IconButton>
                    </InputAdornment>
                    ),
                }}
            />
        </Stack>

        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ my: 2 }}>
                {/* <Checkbox name="remember" label="Remember me" />
                <Link variant="subtitle2" underline="hover">
                Forgot password?
                </Link> */}
        </Stack>

        <Button fullWidth size="large" type="submit" variant="contained" color='info'
            onClick={() => {
                handleClick(email, password);
              }}
            >
            Login
        </Button>
        </>
    );
}
