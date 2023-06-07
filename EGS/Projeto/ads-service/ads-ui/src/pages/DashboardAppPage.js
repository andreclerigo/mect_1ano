import { Helmet } from 'react-helmet-async';
import { faker } from '@faker-js/faker';
// @mui
import { useTheme } from '@mui/material/styles';
import { Grid, Container, Typography } from '@mui/material';
// components
import Iconify from '../components/iconify';
// sections
import {
  AppTasks,
  AppNewsUpdate,
  AppOrderTimeline,
  AppCurrentVisits,
  AppWebsiteVisits,
  AppTrafficBySite,
  AppWidgetSummary,
  AppCurrentSubject,
  AppConversionRates,
} from '../sections/@dashboard/app';

import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

// ----------------------------------------------------------------------

export default function DashboardAppPage() {
    const theme = useTheme();

    const navigate = useNavigate();

    const [numAds, setNumAds] = useState(0);
    const [numImpressions, setNumImpressions] = useState(0);
    const [numClicks, setNumClicks] = useState(0);
    const [avgCpr, setAvgCpr] = useState(0);
    const [bestClickAd, setBestClickAd] = useState(null);
    const [bestImpressionAd, setBestImpressionAd] = useState(null);
    const [hasData, setHasData] = useState(true);
    const hostAddress = window.location.host;
    const [ip, port] = hostAddress.split(':');

    //on page load, read user from props

    useEffect(() => {
        if(localStorage.getItem('type') == '"C"'){
            console.log("user is a consumer");
            navigate('/consumer');
            return;
        }

        if (localStorage.getItem('email') != null) {
            console.log(localStorage.getItem('email') + " is logged in");

            //get user data from backend

            fetch('http://ads-api-mixit.deti/v1/analytics/advertiser/' + localStorage.getItem('id'), {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + localStorage.getItem('token').replace(/['"]+/g, ''),
                },
            })
            .then((response) => {
                if (response.status === 200) {
                    response.json().then((data) => {
                        setNumAds(data.number_of_ads);
                        setNumImpressions(data.total_impressions);
                        setNumClicks(data.total_clicks);
                        setAvgCpr(data.total_ctr);
                        setBestClickAd(data.highest_click_ad);
                        setBestImpressionAd(data.highest_impression_ad);
                    });
                } else {
                    alert("No data found");
                    setHasData(false);
                }
            }
            )
        }
        else {
            console.log("no user");
            navigate('/login');
        }
    }, []);

    return (
        <>
        <Helmet>
            <title> Dashboard</title>
        </Helmet>

        <Container maxWidth="xl">
            <Typography variant="h4" sx={{ mb: 5 }}>
            Hi, Welcome back!
            </Typography>

            <Grid container spacing={3}>
            <Grid item xs={12} sm={6} md={3}>
                <AppWidgetSummary title="Active Image Ads" total={numAds} color="info" icon={'ant-design:picture-filled'} />
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
                <AppWidgetSummary title="Total Impressions" total={numImpressions} color="warning" icon={'ant-design:eye-filled'} />
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
                <AppWidgetSummary title="Total Clicks" total={numClicks} color="error" icon={'ant-design:pushpin-filled'} />
            </Grid>

            <Grid item xs={12} sm={6} md={3}>
                <AppWidgetSummary title="Average CPR" total={avgCpr} icon={'ant-design:rise-outlined'} />
            </Grid>

            <Grid item xs={12} md={6} lg={4}>
                <AppCurrentVisits
                title="Engagement Distribution"
                chartData={[
                    { label: 'Image Clicks', value: numClicks },
                    { label: 'Image Impressions', value: numImpressions },
                ]}
                chartColors={[

                    "#E8A09A",
                    "#FBE29F",

                ]}
                />
            </Grid>

            {bestImpressionAd != null && <Grid item xs={12} md={6} lg={8}>
                <AppTrafficBySite
                title="Top performing advertisements"
                list={[
                    {
                    name: bestImpressionAd.description,
                    url: bestImpressionAd.ad_creative,
                    value: bestImpressionAd.impressions,
                    id: "Advertisement " + bestImpressionAd.id,
                    description: 'Impressions',
                    active: bestImpressionAd.active
                    },
                    {
                    name: bestClickAd.description,
                    url: bestClickAd.ad_creative,
                    value: bestClickAd.clicks,
                    id: "Advertisement " + bestClickAd.id,
                    description: 'Clicks',
                    active: bestClickAd.active
                    },
                ]}
                />
            </Grid>
            }
            </Grid>
        </Container>
        </>
    );
}
