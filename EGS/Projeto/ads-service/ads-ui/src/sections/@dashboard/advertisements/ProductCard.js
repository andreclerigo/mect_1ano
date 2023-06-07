import PropTypes from 'prop-types';
// @mui
import { Box, Card, Link, Typography, Stack } from '@mui/material';
import { styled } from '@mui/material/styles';
// utils
import { fCurrency } from '../../../utils/formatNumber';
// components
import Label from '../../../components/label';
import { ColorPreview } from '../../../components/color-utils';

// ----------------------------------------------------------------------

const StyledProductImg = styled('img')({
  top: 0,
  width: '100%',
  height: '100%',
  objectFit: 'cover',
  position: 'absolute',
});

// ----------------------------------------------------------------------

ShopProductCard.propTypes = {
  product: PropTypes.object,
};

export default function ShopProductCard({ product }) {
  const { active, description, ad_creative, impressions, clicks } = product;

  return (
    <Card>
      <Box sx={{ pt: '100%', position: 'relative' }}>
        {active === 1 && (
          <Label
            variant="filled"
            color={'warning'}
            sx={{
              zIndex: 9,
              top: 16,
              right: 16,
              position: 'absolute',
              textTransform: 'uppercase',
            }}
          >
            {'Active'}
          </Label>
        )}
        {active === 0 && (
          <Label
            variant="filled"
            color={'info'}
            sx={{
              zIndex: 9,
              top: 16,
              right: 16,
              position: 'absolute',
              textTransform: 'uppercase',
            }}
          >
            {'Completed'}
          </Label>
        )}
        <StyledProductImg alt={description} src={ad_creative} />
      </Box>

      <Stack spacing={2} sx={{ p: 3 }}>
        <Link color="inherit" underline="hover">
          <Typography variant="subtitle2" noWrap>
            {description}
          </Typography>
        </Link>

        <Stack direction="row" alignItems="center" justifyContent="space-between">
          {/* <ColorPreview colors={colors} /> */}
          <Typography variant="subtitle1" color= 'grey'>
            &nbsp;
            {impressions + ' views'}
          </Typography>
          <Typography variant="subtitle1">
            &nbsp;
            {clicks+ ' clicks'}
          </Typography>
        </Stack>
      </Stack>
    </Card>
  );
}
