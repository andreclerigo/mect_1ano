// @mui
import PropTypes from 'prop-types';
import { Box, Card, Paper, Typography, CardHeader, CardContent } from '@mui/material';
// utils
import { fShortenNumber } from '../../../utils/formatNumber';
import { styled } from '@mui/material/styles';
import Label from '../../../components/label';

// ----------------------------------------------------------------------

const StyledProductImg = styled('img')({
    top: 0,
    left:'25%',
    width: '50%',
    height: '100%',
    objectFit: 'cover',
    position: 'absolute',
    borderRadius: '4%',
});

AppTrafficBySite.propTypes = {
    title: PropTypes.string,
    subheader: PropTypes.string,
    list: PropTypes.array.isRequired,
    };

    export default function AppTrafficBySite({ title, subheader, list, ...other }) {
    return (
        <Card {...other}>
        <CardHeader title={title} subheader={subheader} />

        <CardContent>
            <Box
            sx={{
                display: 'grid',
                gap: 2,
                gridTemplateColumns: 'repeat(2, 1fr)',
            }}
            >
            {list.map((site) => (
                <Paper key={site.name} variant="outlined" sx={{ py: 2.5, textAlign: 'center' }}>
                <Box sx={{ pt: '50%', position: 'relative' }}>
                <StyledProductImg alt={site.description} src={site.url} />
            </Box>

                <Typography variant="h2">{site.value != 0 ? fShortenNumber(site.value) : 0}</Typography>

                <Typography variant="h4" sx={{ color: 'text.secondary' }}>
                    {site.description}
                </Typography>

                    <Typography variant="paragraph" sx={{ color: 'text.secondary' }}>
                    {site.name}
                    </Typography>
                </Paper>
            ))}
            </Box>
        </CardContent>
        </Card>
    );
}
