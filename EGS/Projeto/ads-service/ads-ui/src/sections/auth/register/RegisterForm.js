import { useState } from 'react';
import { json, useNavigate } from 'react-router-dom';
// @mui
import { Link, Stack, IconButton, InputAdornment, TextField, Checkbox, InputLabel, Select, FormControl, MenuItem } from '@mui/material';
import { LoadingButton } from '@mui/lab';
// components
import Iconify from '../../../components/iconify';

// ----------------------------------------------------------------------

export default function RegisterForm() {
    const navigate = useNavigate();

    const [showPassword, setShowPassword] = useState(false);

    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [userType, setUserType] = useState('');
    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');
        

    const handleClick = () => {
        console.log(email, name, password, confirmPassword, userType)
        if (password !== confirmPassword) {
            alert('Passwords do not match');
            return;
        }

        fetch('http://ads-api-mixit.deti/v1/users', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                email: email,
                name: name,
                password: password,
                type: userType,
            }),
        })
        .then((response) => {
            if (response.status === 201) {
                navigate('/dashboard', { replace: true });
            } else {
                alert('Invalid credentials');
            }
        }
        )
        .catch((error) => {
            console.error('Error:', error);
        }
        );
    };

    const handleTypeChange = (event) => {
        setUserType(event.target.value);
    };

    return (
        <>
        <Stack spacing={3}>
            <TextField name="email" label="Email address" onChange={(e) => setEmail(e.target.value)} />
            <TextField name="name" label="Name" onChange={(e) => setName(e.target.value)} />
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">User Type</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={userType}
                    label="User Type"
                    onChange={handleTypeChange}
                >
                    <MenuItem value={"advertiser"}>Advertiser</MenuItem>
                    <MenuItem value={"consumer"}>Consumer</MenuItem>
                </Select>
            </FormControl>
            <TextField
            name="password"
            label="Password"
            type={showPassword ? 'text' : 'password'}
            onChange={(e) => setPassword(e.target.value)}
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
            <TextField
            name="password"
            label="Confirm Password"
            type={showPassword ? 'text' : 'password'}
            onChange={(e) => setConfirmPassword(e.target.value)}
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
        </Stack>

        <LoadingButton fullWidth size="large" type="submit" variant="contained" onClick={handleClick} color='info'>
            Register
        </LoadingButton>
        </>
    );
}
