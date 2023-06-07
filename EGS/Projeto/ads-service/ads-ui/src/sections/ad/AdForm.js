import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
// @mui
import { Link, Stack, IconButton, InputAdornment, TextField, Checkbox, InputLabel, Select, FormControl, MenuItem } from '@mui/material';
import { LoadingButton } from '@mui/lab';
// components
import Iconify from '../../components/iconify';

// ----------------------------------------------------------------------

export default function AdForm() {
    const navigate = useNavigate();

    const [model, setModel] = useState("CPC");
    const [age, setAge] = useState('');
    const [location, setLocation] = useState('');
    const [description, setDescription] = useState('');
    const [url, setUrl] = useState('');
    const [target, setTarget] = useState('');
    const [redirect, setRedirect] = useState('');
    const hostAddress = window.location.origin;
    const [ip, port] = hostAddress.split(':');

    const handleClick = () => {
        fetch('http://ads-api-mixit.deti/v1/ads', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
            },
            body: JSON.stringify({
                pricing_model: model,
                age_range: age,
                location: location,
                description: description,
                ad_creative: url,
                target: target,
                redirect: redirect,
            }),
        })
        .then((response) => {
            if (response.status === 201) {
                navigate('/dashboard', { replace: true });
            } else {
                alert('Invalid information');
            }
        }
        )
        .catch((error) => {
            console.error('Error:', error);
        }
        );
    };

    const handleModel = (event) => {
        setModel(event.target.value);
    };

    const handleAge = (event) => {
        setAge(event.target.value);
    };

    const handleLocation = (event) => {
        setLocation(event.target.value);
    };


    return (
        <>
        <Stack spacing={3}>
            <TextField name="description" label="Short Advertisement Description" onChange={(e) => setDescription(e.target.value)}/>
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">Pricing Model</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={model}
                    label="Pricing Model"
                    onChange={handleModel}
                >
                    <MenuItem value={"CPC"}>Cost per click</MenuItem>
                    <MenuItem value={"CPM"}>Cost per impression</MenuItem>
                </Select>
            </FormControl>
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">Target Audience</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={age}
                    label="Pricing Model"
                    onChange={handleAge}
                >
                    <MenuItem value={"youth"}>Youth</MenuItem>
                    <MenuItem value={"adults"}>Adults</MenuItem>
                    <MenuItem value={"seniors"}>Seniors</MenuItem>
                </Select>
            </FormControl>
            <FormControl fullWidth>
                <InputLabel id="demo-simple-select-label">Target Location</InputLabel>
                <Select
                    labelId="demo-simple-select-label"
                    id="demo-simple-select"
                    value={location}
                    label="Pricing Model"
                    onChange={handleLocation}
                >
                    <MenuItem value={"portugal"}>Portugal</MenuItem>
                    <MenuItem value={"spain"}>Spain</MenuItem>
                    <MenuItem value={"france"}>France</MenuItem>
                    <MenuItem value={"germany"}>Germany</MenuItem>
                    <MenuItem value={"italy"}>Italy</MenuItem>
                    <MenuItem value={"uk"}>United Kingdom</MenuItem>
                    <MenuItem value={"netherlands"}>Netherlands</MenuItem>
                    <MenuItem value={"belgium"}>Belgium</MenuItem>
                    <MenuItem value={"austria"}>Austria</MenuItem>
                    <MenuItem value={"sweeden"}>Sweeden</MenuItem>
                    <MenuItem value={"croatia"}>Croatia</MenuItem>
                    <MenuItem value={"Poland"}>Poland</MenuItem>
                </Select>
            </FormControl>
            <TextField name="link" label={model == 'CPC' ? 'Total clicks goal' : 'Total impressions goal'} onChange={(e) => setTarget(e.target.value)}/>
            <TextField name="link" label="Advertisement link" onChange={(e) => setUrl(e.target.value)}/>
        </Stack>
        {model == "CPC" ?? <TextField name="link" label="Redirect destination" onChange={(e) => setRedirect(e.target.value)}/>}

        <Stack direction="row" alignItems="center" justifyContent="space-between" sx={{ my: 2 }}>
        </Stack>

        <LoadingButton fullWidth size="large" type="submit" variant="contained" onClick={handleClick} color='info'>
            Create Advertisement
        </LoadingButton>
        </>
    );
}
