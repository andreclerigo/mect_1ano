import { Helmet } from 'react-helmet-async';
import { useState, useEffect } from 'react';
// @mui
import { Container, Stack, Typography } from '@mui/material';
// components
import { ProductSort, ProductList, ProductCartWidget, ProductFilterSidebar } from '../sections/@dashboard/advertisements';
// mock
import PRODUCTS from '../_mock/products';
import { useNavigate } from 'react-router-dom';

// ----------------------------------------------------------------------

export default function ProductsPage() {

    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');


    const navigate = useNavigate();
    
    const [ads, setAds] = useState([]);

    useEffect(() => {
        if (localStorage.getItem('email') != null) {
            console.log(localStorage.getItem('email') + " is logged in");
            fetch('http://ads-api-mixit.deti/v1/profile', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
                },
            })
            .then(response => response.json())
            .then(data => {
                console.log(data.ads);
                setAds(data.ads);
            });
        }
        else {
            console.log("no user");
            navigate('/login');
        }
    }, []);

    const [openFilter, setOpenFilter] = useState(false);

    const handleOpenFilter = () => {
        setOpenFilter(true);
    };

    const handleCloseFilter = () => {
        setOpenFilter(false);
    };

    return (
        <>
        <Helmet>
            <title> Advertisements </title>
        </Helmet>

        <Container>
            <Typography variant="h4" sx={{ mb: 5 }}>
            My Advertisements
            </Typography>
            <ProductList products={ads} />
            <div id='advertisements'></div>
        </Container>
        </>
    );
}
